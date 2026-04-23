package in.sigma.SplitDash.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "bills")
public class Bill {
    @Id
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal tip = BigDecimal.ZERO;

    @Column
    private Instant createdAt = Instant.now();

    @Version
    private Long version;

    public Bill() {}

    public Bill(String name) {
        this.name = name;
    }
}