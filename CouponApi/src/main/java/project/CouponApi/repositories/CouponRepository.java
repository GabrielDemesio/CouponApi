package project.CouponApi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.CouponApi.entities.CouponEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, String> {
    Boolean existsByCode(String code);
    Optional<CouponEntity> findById(String id);
    List<CouponEntity> findByDeletedFalse();
}
