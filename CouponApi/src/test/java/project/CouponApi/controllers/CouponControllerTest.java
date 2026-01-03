package project.CouponApi.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import project.CouponApi.entities.CouponEntity;
import project.CouponApi.entities.CouponEntity.Status;
import project.CouponApi.services.CouponService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponControllerTest {

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponController couponController;

    private CouponEntity sample() {
        CouponEntity c = new CouponEntity();
        c.setId("id-1");
        c.setCode("ABC123");
        c.setDescription("Cupom teste");
        c.setDiscountValue(new BigDecimal("1.00"));
        c.setExpirationDate(LocalDateTime.now().plusDays(1));
        c.setPublished(false);
        c.setRedeemed(false);
        c.setStatus(Status.ACTIVE);
        return c;
    }

    @Test
    void shouldReturnListFromService() {
        when(couponService.findAll()).thenReturn(List.of(sample()));

        List<CouponEntity> result = couponController.findAll();

        assertEquals(1, result.size());
        assertEquals("ABC123", result.get(0).getCode());
    }

    @Test
    void shouldCreateCouponAndReturnCreatedStatus() {
        CouponEntity input = sample();
        when(couponService.createCoupon(any(CouponEntity.class))).thenReturn(input);

        ResponseEntity<CouponEntity> response = couponController.createCoupon(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ABC123", response.getBody().getCode());
    }

    @Test
    void shouldDeleteById() {
        ResponseEntity<String> response = couponController.deleteById("id-1");

        verify(couponService).deleteById("id-1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cupom deletado com sucesso.", response.getBody());
    }
}
