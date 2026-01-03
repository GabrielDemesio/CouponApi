package project.CouponApi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.CouponApi.entities.CouponEntity;
import project.CouponApi.services.CouponService;

import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public List<CouponEntity> findAll() {
        return couponService.findAll();
    }

    @GetMapping("/{id}")
    public CouponEntity findById(@PathVariable String id) {
        return couponService.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        couponService.deleteById(id);
        return ResponseEntity.ok("Cupom deletado com sucesso.");
    }

    @PostMapping
    public ResponseEntity<CouponEntity> createCoupon(@RequestBody CouponEntity couponEntity) {
        CouponEntity created = couponService.createCoupon(couponEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
