package project.CouponApi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.CouponApi.entities.CouponEntity;
import project.CouponApi.entities.CouponEntity.Status;
import project.CouponApi.repositories.CouponRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private CouponEntity baseCoupon;

    @BeforeEach
    void setUp() {
        baseCoupon = new CouponEntity();
        baseCoupon.setCode("Abc-123");
        baseCoupon.setDescription("Cupom de teste");
        baseCoupon.setDiscountValue(new BigDecimal("1.00"));
        baseCoupon.setExpirationDate(LocalDateTime.now().plusDays(1));
        baseCoupon.setPublished(true);
        baseCoupon.setRedeemed(false);
        baseCoupon.setStatus(Status.ACTIVE);
    }

    @Test
    void createCoupon_shouldSanitizeCodeAndSetDefaults() {
        when(couponRepository.existsByCode("ABC123")).thenReturn(false);
        when(couponRepository.save(any(CouponEntity.class))).thenAnswer(invocation -> {
            CouponEntity saved = invocation.getArgument(0);
            saved.setId("generated-id");
            return saved;
        });

        CouponEntity created = couponService.createCoupon(baseCoupon);

        ArgumentCaptor<CouponEntity> captor = ArgumentCaptor.forClass(CouponEntity.class);
        verify(couponRepository).save(captor.capture());
        CouponEntity saved = captor.getValue();

        assertEquals("ABC123", saved.getCode(), "Deve salvar código sanitizado");
        assertTrue(saved.getPublished(), "Deve persistir publicado conforme entrada");
        assertEquals(Status.ACTIVE, saved.getStatus(), "Status inicial deve ser ACTIVE");
        assertFalse(Boolean.TRUE.equals(saved.getDeleted()), "Soft delete deve começar desativado");
        assertEquals("generated-id", created.getId());
    }

    @Test
    void createCoupon_shouldThrowWhenCodeAlreadyExists() {
        when(couponRepository.existsByCode("ABC123")).thenReturn(true);

        var exception = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> couponService.createCoupon(baseCoupon));

        assertTrue(exception.getMessage().contains("Cupom já existe"));
        verify(couponRepository, never()).save(any());
    }

    @Test
    void deleteById_shouldMarkDeletedAndSetStatusDeleted() {
        CouponEntity existing = new CouponEntity();
        existing.setId("id-1");
        existing.setCode("ABC123");
        existing.setDescription("Teste delete");
        existing.setDiscountValue(new BigDecimal("2.00"));
        existing.setExpirationDate(LocalDateTime.now().plusDays(2));
        existing.setPublished(false);
        existing.setRedeemed(false);
        existing.setDeleted(false);
        existing.setStatus(Status.ACTIVE);

        when(couponRepository.findById("id-1")).thenReturn(Optional.of(existing));

        couponService.deleteById("id-1");

        assertTrue(existing.getDeleted(), "Deve marcar como deletado");
        assertEquals(Status.DELETED, existing.getStatus(), "Status deve ser DELETED");
        verify(couponRepository).save(existing);
    }
}
