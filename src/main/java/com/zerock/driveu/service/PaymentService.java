package com.zerock.driveu.service;

import com.zerock.driveu.client.PortoneClient;
import com.zerock.driveu.domain.Application;
import com.zerock.driveu.domain.ExamSchedule;
import com.zerock.driveu.domain.Payment;
import com.zerock.driveu.domain.enums.ApplicationStatus;
import com.zerock.driveu.domain.enums.ExamType;
import com.zerock.driveu.domain.enums.PaymentStatus;
import com.zerock.driveu.dto.ApplySessionDTO;
import com.zerock.driveu.dto.portone.PaymentResponse;
import com.zerock.driveu.repository.ApplicationRepository;
import com.zerock.driveu.repository.ExamScheduleRepository;
import com.zerock.driveu.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ApplicationRepository applicationRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final PaymentRepository paymentRepository;
    private final PortoneClient portoneClient;

    // wApply4 진입 시점에 호출됨
    // 같은 회원·같은 시험에 미결제건 있으면 CANCELLED 처리 후 새 신청건 생성
    @Transactional
    public Application createApplication(ApplySessionDTO dto, Long userSeq, String memberType) {

        // 1. 시험 일정 조회 (FK 매핑용)
        ExamSchedule schedule = examScheduleRepository.findById(dto.getExamScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("시험 일정 없음: " + dto.getExamScheduleId()));

        // 2. 기존 미결제건 정리 (있으면 CANCELLED)
        applicationRepository
                .findByUserSeqAndMemberTypeAndExamSchedule_ScheduleIdAndStatus(
                        userSeq,
                        memberType,
                        dto.getExamScheduleId(),
                        ApplicationStatus.WAITING_PAYMENT
                )
                .ifPresent(existing -> {
                    log.info("기존 미결제건 취소 처리: applicationId={}", existing.getApplicationId());
                    existing.markAsCancelled();
                });

        // 3. 새 Application 생성
        String merchantUid = generateMerchantUid();
        ExamType examType = dto.getExamType();
        //결제금액
        int fee = examType.getFee();

        Application application = Application.builder()
                .userSeq(userSeq)
                .memberType(memberType)
                .examSchedule(schedule)
                .examType(examType)
                .licenseType(dto.getLicenseType())
                .contactPhone(dto.getContactPhone())
                .contactEmail(dto.getContactEmail())
                .fee(fee)
                .status(ApplicationStatus.WAITING_PAYMENT)
                .merchantUid(merchantUid)
                .build();

        return applicationRepository.save(application);
    }

    // 주문번호 생성: ORD_yyyyMMddHHmmss_랜덤8자리
    private String generateMerchantUid() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8);
        return "ORD_" + timestamp + "_" + random;
    }


    // 프론트가 결제 완료 후 호출. paymentId만 받아서 검증 + 저장.
    @Transactional
    public Payment verifyPayment(String paymentId) {

        // 1. 우리 DB에서 Application 조회 (V2 paymentId == 우리 merchantUid)
        Application application = applicationRepository.findByMerchantUid(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("신청건 없음: " + paymentId));

        // 2. 포트원에 결제 정보 조회 (★ 검증의 핵심 — 프론트 값 안 믿고 서버끼리 직통)
        PaymentResponse portoneResponse = portoneClient.getPayment(paymentId);

        // 3. 포트원 응답에서 필요한 값 추출
        int portoneAmount = portoneResponse.getAmount().getTotal();

        // 포트원은 UTC(OffsetDateTime)로 보냄 → 한국 시간(LocalDateTime)으로 변환
        LocalDateTime paidAt = portoneResponse.getPaidAt() != null
                ? portoneResponse.getPaidAt()
                  .atZoneSameInstant(ZoneId.of("Asia/Seoul"))
                  .toLocalDateTime()
                : null;

        // 4. Payment 객체 미리 생성 (status는 일단 READY, 아래 검증 후에 PAID/FAILED로 갱신)
        //    → status 필드가 @Column(nullable = false)라 build 시점에 null이면 안 됨
        Payment payment = Payment.builder()
                .application(application)
                .merchantUid(paymentId)
                .amount(portoneAmount)
                .payMethod(portoneResponse.getMethod() != null
                        ? portoneResponse.getMethod().getType() : null)
                .pgProvider(portoneResponse.getChannel() != null
                        ? portoneResponse.getChannel().getPgProvider() : null)
                .status(PaymentStatus.READY)
                .build();

        // 5. 검증 — 두 가지 조건 모두 만족해야 PAID
        //    (1) 금액 일치 : 포트원이 받은 돈 == 우리 DB에 박힌 응시료
        //    (2) 상태 정상 : 포트원이 "PAID"라고 응답 (취소/실패 아님)
        boolean amountMatched = (portoneAmount == application.getFee());
        boolean statusPaid = "PAID".equals(portoneResponse.getStatus());

        if (amountMatched && statusPaid) {
            // 검증 성공 → Payment PAID, Application COMPLETED
            payment.markAsPaid(paidAt);
            application.markAsCompleted();
            log.info("결제 검증 성공: paymentId={}, amount={}", paymentId, portoneAmount);
        } else {
            // 검증 실패 → Payment FAILED, Application FAILED
            // 예외 던지지 않음 (실패도 정상 비즈니스 결과 → 이력 보존)
            payment.markAsFailed();
            application.markAsFailed();
            log.error("결제 검증 실패: paymentId={}, 금액일치={}, 상태정상={}",
                    paymentId, amountMatched, statusPaid);
        }

        // 6. Payment 저장 → 트랜잭션 끝날 때 Application 변경 감지로 UPDATE도 자동 발생
        return paymentRepository.save(payment);
    }
}