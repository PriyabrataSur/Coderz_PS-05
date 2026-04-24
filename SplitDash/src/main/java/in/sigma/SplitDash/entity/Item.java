package in.sigma.SplitDash.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
public class Item {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status = ItemStatus.UNCLAIMED;

    @Column
    private String claimedBy;

    @Column
    private Instant claimedAt;

    @Version
    private Long version;

    @Column(name = "last_updated")
    private Instant lastUpdated = Instant.now();

    // Constructors
    public Item() {}

    public Item(String sessionId, String name, BigDecimal price) {
        this.sessionId = sessionId;
        this.name = name;
        this.price = price;
    }

}