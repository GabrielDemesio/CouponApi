package project.CouponApi.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(
        name = "coupons",
        uniqueConstraints = @UniqueConstraint(name = "unique_code", columnNames = "code")
)
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "code", nullable = false, length = 6, updatable = false)
    @NotBlank
    @Size(min =6, max = 6)
    @Pattern(regexp = "^[A-Z0-9]{6}$")
    private String code;

    @Column(name = "description", nullable = false, length = 100)
    @NotBlank
    private String description;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin(value = "0.5")
    private BigDecimal discountValue;

    @Column(name = "expiration_date", nullable = false)
    @NotNull
    private LocalDateTime expirationDate;

    @Column(name = "published", nullable = false)
    private Boolean published = false;

    @Column(name = "published_at")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime publishedAt;

    private Boolean redeemed = false;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status;

    public enum Status {
        ACTIVE,
        INACTIVE,
        DELETED
    }
}
