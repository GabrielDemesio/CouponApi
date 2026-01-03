package project.CouponApi.services;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.CouponApi.entities.CouponEntity;
import project.CouponApi.entities.CouponEntity.Status;
import project.CouponApi.repositories.CouponRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {
    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<CouponEntity> findAll() {
        List<CouponEntity> coupons = couponRepository.findByDeletedFalse();
        coupons.forEach(this::refreshStatus);
        return coupons;
    }

    public CouponEntity findById(String id) {
        CouponEntity coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ("Cupom não encontrado ou já deletado: " + id)));
        refreshStatus(coupon);
        return coupon;
    }

    @Transactional
    public CouponEntity createCoupon(CouponEntity coupon) {
        String code = formatCode(coupon.getCode());
        validateCouponDiscount(coupon.getDiscountValue());
        validateCouponExpirationDate(coupon.getExpirationDate());

        if (couponRepository.existsByCode(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("Cupom já existe: " + code + "ou já deletado." + coupon.getStatus()));
        }

        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCode(code);
        couponEntity.setDescription(coupon.getDescription());
        couponEntity.setDiscountValue(coupon.getDiscountValue());
        couponEntity.setExpirationDate(coupon.getExpirationDate());

        boolean published = Boolean.TRUE.equals(coupon.getPublished());
        couponEntity.setPublished(published);
        couponEntity.setPublishedAt(published ? LocalDateTime.now() : null);
        boolean redeemed = Boolean.TRUE.equals(coupon.getRedeemed());
        couponEntity.setRedeemed(redeemed);
        couponEntity.setDeleted(false);
        couponEntity.setDeletedAt(null);
        couponEntity.setStatus(Status.ACTIVE);
        return couponRepository.save(couponEntity);
    }

    private String formatCode(String code) {
        if (code == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("Código do cupom é obrigatório."));
        }
        String formatted = code.trim().toUpperCase()
                .replaceAll("[^A-Z0-9]", "");
        if (formatted.length() != 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("Código do cupom deve ter 6 caracteres."));
        }
        return formatted;
    }

    private void validateCouponDiscount(BigDecimal discountValue) {
        if (discountValue == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("Valor do desconto é obrigatório."));
        }
        if (discountValue.compareTo(new BigDecimal("0.5")) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("Valor do desconto deve ser no mínimo 0.5."));
        }
    }

    private void validateCouponExpirationDate(LocalDateTime expirationDate) {
        if (expirationDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("Data de expiração é obrigatória."));
        }
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("Data de expiração deve ser posterior à data atual."));
        }
    }

    private void refreshStatus(CouponEntity coupon) {
        if (Boolean.TRUE.equals(coupon.getDeleted())) {
            coupon.setStatus(Status.DELETED);
            return;
        }
        Status newStatus = coupon.getExpirationDate().isBefore(LocalDateTime.now()) ? Status.INACTIVE : Status.ACTIVE;
        if (coupon.getStatus() != newStatus) {
            coupon.setStatus(newStatus);
            couponRepository.save(coupon);
        }
    }
    @Transactional
    public void deleteById(String id) {
        CouponEntity coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ("Cupom não encontrado ou já deletado: " + id)));
        coupon.setDeleted(true);
        coupon.setDeletedAt(LocalDateTime.now());
        coupon.setStatus(Status.DELETED);
        couponRepository.save(coupon);
    }

}
