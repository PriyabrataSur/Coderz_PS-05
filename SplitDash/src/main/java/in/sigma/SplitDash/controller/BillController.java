package in.sigma.SplitDash.controller;

import in.sigma.SplitDash.dto.ItemDto;
import in.sigma.SplitDash.entity.Bill;
import in.sigma.SplitDash.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createBill(@RequestBody Map<String, String> request) {
        String billName = request.getOrDefault("name", "Dinner Bill");
        String sessionId = billService.createBill(billName);
        return ResponseEntity.ok(Map.of("sessionId", sessionId));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<Bill> getBill(@PathVariable String sessionId) {
        Bill bill = billService.getBill(sessionId);
        return bill != null ? ResponseEntity.ok(bill) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{sessionId}/items")
    public ResponseEntity<List<ItemDto>> getItems(@PathVariable String sessionId) {
        List<ItemDto> items = billService.getItems(sessionId);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{sessionId}/items")
    public ResponseEntity<Void> addItems(@PathVariable String sessionId, @RequestBody List<ItemDto> items) {
        billService.addItems(sessionId, items);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sessionId}/items/{itemId}/claim")
    public ResponseEntity<ItemDto> claimItem(@PathVariable String sessionId,
                                             @PathVariable Long itemId,
                                             @RequestParam String claimedBy) {
        try {
            ItemDto item = billService.claimItem(sessionId, itemId, claimedBy);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{sessionId}/progress")
    public ResponseEntity<BigDecimal> getProgress(@PathVariable String sessionId) {
        BigDecimal progress = billService.getClaimedPercentage(sessionId);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/{sessionId}/shares")
    public ResponseEntity<Map<String, BigDecimal>> calculateShares(@PathVariable String sessionId,
                                                                   @RequestParam(defaultValue = "8.875") BigDecimal taxPercent,
                                                                   @RequestParam(defaultValue = "20") BigDecimal tipPercent) {
        Map<String, BigDecimal> shares = billService.calculateShares(sessionId, taxPercent, tipPercent);
        return ResponseEntity.ok(shares);
    }
}