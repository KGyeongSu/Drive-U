package com.zerock.driveu.repository;

import com.zerock.driveu.domain.LicensePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LicensePaymentRepository extends JpaRepository<LicensePayment, Long> {
    // 중복 결제 검사 -> uid 로 조회 주문번호
    Optional<LicensePayment> findByMerchantUid(String merchantUid);
}