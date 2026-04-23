package in.sigma.SplitDash.service;

import in.sigma.SplitDash.dto.ItemDto;
import in.sigma.SplitDash.entity.Bill;
import in.sigma.SplitDash.entity.Item;
import in.sigma.SplitDash.entity.ItemStatus;
import in.sigma.SplitDash.repository.BillRepo;
import in.sigma.SplitDash.repository.ItemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BillServiceImpl implements BillService {

    private final BillRepo billRepository;
    private final ItemRepo itemRepository;

    @Override
    public String createBill(String billName) {
        Bill bill = new Bill(billName);
        Bill savedBill = billRepository.save(bill);
        return savedBill.getId();
    }

    @Override
    public Bill getBill(String sessionId) {
        return billRepository.findById(sessionId).orElse(null);
    }

    @Override
    public List<ItemDto> getItems(String sessionId) {
        List<Item> items = itemRepository.findBySessionId(sessionId);
        return items.stream()
                .map(item -> new ItemDto(item.getId(), item.getName(), item.getPrice(),
                        item.getStatus(), item.getClaimedBy()))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto claimItem(String sessionId, Long itemId, String claimedBy) {
        Item item = itemRepository.findByIdAndSessionId(itemId, sessionId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

        if (item.getStatus() != ItemStatus.UNCLAIMED) {
            throw new RuntimeException("Item is not available. Current status: " + item.getStatus());
        }

        // Atomic claim operation
        item.setStatus(ItemStatus.CLAIMED);
        item.setClaimedBy(claimedBy);
        item.setClaimedAt(Instant.now());

        Item savedItem = itemRepository.save(item);
        return new ItemDto(savedItem.getId(), savedItem.getName(), savedItem.getPrice(),
                savedItem.getStatus(), savedItem.getClaimedBy());
    }

    @Override
    public void addItems(String sessionId, List<ItemDto> itemDtos) {
        itemDtos.forEach(dto -> {
            Item item = new Item(sessionId, dto.getName(), dto.getPrice());
            itemRepository.save(item);
        });
    }

    @Override
    public BigDecimal getClaimedPercentage(String sessionId) {
        List<Item> allItems = itemRepository.findBySessionId(sessionId);
        List<Item> claimedItems = itemRepository.findBySessionIdAndStatus(sessionId, ItemStatus.CLAIMED);

        if (allItems.isEmpty()) return BigDecimal.ZERO;

        BigDecimal totalValue = allItems.stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal claimedValue = claimedItems.stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalValue.compareTo(BigDecimal.ZERO) > 0 ?
                claimedValue.divide(totalValue, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;
    }

    @Override
    public Map<String, BigDecimal> calculateShares(String sessionId, BigDecimal taxPercent, BigDecimal tipPercent) {
        List<Item> claimedItems = itemRepository.findBySessionIdAndStatus(sessionId, ItemStatus.CLAIMED);

        if (claimedItems.isEmpty()) {
            return Map.of();
        }

        BigDecimal totalClaimed = claimedItems.stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = totalClaimed.multiply(taxPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal tip = totalClaimed.multiply(tipPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal extraPerPerson = tax.add(tip)
                .divide(BigDecimal.valueOf(claimedItems.size()), 2, RoundingMode.HALF_UP);

        return claimedItems.stream().collect(Collectors.toMap(
                Item::getClaimedBy,
                item -> item.getPrice().add(extraPerPerson),
                (existing, replacement) -> existing
        ));
    }
}