package com.zerock.driveu.repository;

import com.zerock.driveu.domain.PracticeLicense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PracticeLicenseRepository extends JpaRepository<PracticeLicense, Long> {

    // 1. 결제 검증용 — 포트원 주문번호로 신청건 조회
    Optional<PracticeLicense> findByMerchantUid(String merchantUid);

    // 2. 발급완료 화면용 — 회원의 가장 최근 발급(ISSUED)건
    Optional<PracticeLicense> findFirstByUserSeqAndMemberTypeAndStatusOrderByCreatedAtDesc(
            Long userSeq,
            String memberType,
            String status);
}