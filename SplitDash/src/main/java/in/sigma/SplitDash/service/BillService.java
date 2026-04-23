package in.sigma.SplitDash.service;

import in.sigma.SplitDash.dto.ItemDto;
import in.sigma.SplitDash.entity.Bill;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BillService {
    String createBill(String billName);
    Bill getBill(String sessionId);
    List<ItemDto> getItems(String sessionId);
    ItemDto claimItem(String sessionId, Long itemId, String claimedBy);
    void addItems(String sessionId, List<ItemDto> itemDtos);
    BigDecimal getClaimedPercentage(String sessionId);
    Map<String, BigDecimal> calculateShares(String sessionId, BigDecimal taxPercent, BigDecimal tipPercent);
}
