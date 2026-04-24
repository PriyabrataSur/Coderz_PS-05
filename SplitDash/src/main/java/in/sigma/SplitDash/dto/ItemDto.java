package in.sigma.SplitDash.dto;

import in.sigma.SplitDash.entity.ItemStatus;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private ItemStatus status;
    private String claimedBy;

    public ItemDto() {}

    public ItemDto(Long id, String name, BigDecimal price, ItemStatus status, String claimedBy) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = status;
        this.claimedBy = claimedBy;
    }
}
