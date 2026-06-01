package com.zerock.driveu.service;

import com.zerock.driveu.client.PortoneClient;
import com.zerock.driveu.constant.ExamConstants;
import com.zerock.driveu.domain.PracticeLicense;
import com.zerock.driveu.dto.portone.PaymentResponse;
import com.zerock.driveu.repository.PracticeLicenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PracticeLicenseService {

    private final PracticeLicenseRepository practiceLicenseRepository;
    private final PortoneClient portoneClient;

    // 연습면허 발급 수수료 (합의: 4,000원 / 테스트 단계: 1000원)
    private static final int PRACTICE_LICENSE_FEE = 1000;

    // pLicense3 진입 시점 — 신청건(PracticeLicense) 생성, WAITING_PAYMENT
    @Transactional
    public PracticeLicense createApplication(Long userSeq, String memberType, String licenseType) {

        // 종별 방어: 연습면허는 1종보통/2종보통만 (1종대형·2종소형은 기능합격으로 종결)
        if (!ExamConstants.PRACTICE_LICENSE_TYPES.contains(licenseType)) {
            throw new IllegalArgumentException("연습면허 발급 불가 종별: " + licenseType);
        }

        // 기존 미결제건 정리 (있으면 CANCELLED)
        practiceLicenseRepository
                .findFirstByUserSeqAndMemberTypeAndStatusOrderByCreatedAtDesc(
                        userSeq, memberType, "WAITING_PAYMENT")
                .ifPresent(existing -> {
                    log.info("기존 미결제 연습면허건 취소: id={}", existing.getPracticeLicenseId());
                    existing.markAsCancelled();
                });

        PracticeLicense entity = PracticeLicense.builder()
                .userSeq(userSeq)
                .memberType(memberType)
                .licenseType(licenseType)
                .fee(PRACTICE_LICENSE_FEE)
                .status("WAITING_PAYMENT")
                .merchantUid(generateMerchantUid())
                .build();

        return practiceLicenseRepository.save(entity);
    }

    // 주문번호 생성 (PaymentService와 동일 포맷, 접두사만 PL_)
    private String generateMerchantUid() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8);
        return "PL_" + timestamp + "_" + random;
    }

    // verifyPayment 복제 — Application 대신 PracticeLicense 대상
    @Transactional
    public PracticeLicense verifyPayment(String paymentId) {

        // 1. 우리 DB에서 신청건 조회 (paymentId == 우리 merchantUid)
        PracticeLicense entity = practiceLicenseRepository.findByMerchantUid(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("연습면허 신청건 없음: " + paymentId));

        // 2. 포트원에 직접 조회 (서버끼리 — 프론트 값 안 믿음)
        PaymentResponse portoneResponse = portoneClient.getPayment(paymentId);
        int portoneAmount = portoneResponse.getAmount().getTotal();

        // 3. 검증 — 금액 일치 && status PAID
        boolean amountMatched = (portoneAmount == entity.getFee());
        boolean statusPaid = "PAID".equals(portoneResponse.getStatus());

        if (amountMatched && statusPaid) {
            // 검증 성공 → 발급 확정 (번호 생성 + 발급일 + 유효기간)
            String licenseNumber = generateLicenseNumber();
            entity.markAsIssued(licenseNumber, LocalDate.now());
            log.info("연습면허 발급 성공: paymentId={}, 면허번호={}", paymentId, licenseNumber);
        } else {
            // 실패도 예외 X — 이력 보존 (verifyPayment와 동일 방침)
            entity.markAsFailed();
            log.error("연습면허 결제 검증 실패: paymentId={}, 금액일치={}, 상태정상={}",
                    paymentId, amountMatched, statusPaid);
        }

        // 변경감지로 UPDATE 자동 발생 (이미 영속 상태라 save 불필요하지만 명시)
        return entity;
    }

    // 연습면허 번호 생성 (예: PL-20260528-3F2A)
    private String generateLicenseNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "PL-" + date + "-" + random;
    }
}