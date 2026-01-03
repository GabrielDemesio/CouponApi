package project.CouponApi;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.CouponApi.entities.CouponEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CouponEntityValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private CouponEntity validCoupon() {
        CouponEntity coupon = new CouponEntity();
        coupon.setCode("ABC123");
        coupon.setDescription("Cupom válido");
        coupon.setDiscountValue(new BigDecimal("1.00"));
        coupon.setExpirationDate(LocalDateTime.now().plusDays(1));
        coupon.setPublished(false);
        coupon.setRedeemed(false);
        coupon.setStatus(CouponEntity.Status.ACTIVE);
        return coupon;
    }

    @Test
    void shouldFailWhenCodeIsNotSixAlphanumericCharacters() {
        CouponEntity coupon = validCoupon();
        coupon.setCode("ABC1");

        Set<ConstraintViolation<CouponEntity>> violations = validator.validate(coupon);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")));
    }

    @Test
    void shouldFailWhenDiscountIsLessThanMinimum() {
        CouponEntity coupon = validCoupon();
        coupon.setDiscountValue(new BigDecimal("0.3"));

        Set<ConstraintViolation<CouponEntity>> violations = validator.validate(coupon);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("discountValue")));
    }
}
