package com.zerock.driveu.service;

import com.zerock.driveu.domain.ExternalLicenseStatus;
import com.zerock.driveu.domain.LicenseApplication;
import com.zerock.driveu.domain.LicenseApplication.ApplicationType;
import com.zerock.driveu.domain.LicensePayment;
import com.zerock.driveu.domain.UserLicense;
import com.zerock.driveu.dto.LicenseDTO;
import com.zerock.driveu.repository.ExternalLicenseStatusRepository;
import com.zerock.driveu.repository.LicenseApplicationRepository;
import com.zerock.driveu.repository.LicensePaymentRepository;
import com.zerock.driveu.repository.UserLicenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class LicenseServiceImpl implements LicenseService {

    private final ExternalLicenseStatusRepository externalRepository;
    private final LicenseApplicationRepository applicationRepository;
    private final LicensePaymentRepository paymentRepository;
    private final UserLicenseRepository userLicenseRepository;

    // 자격 요건 조회 로직
    @Override
    @Transactional(readOnly = true)
    public boolean checkLicenseEligibility(Long userSeq, String licenseType) {
        // 1. String -> Enum 변환
        ApplicationType appType;
        try {
            appType = ApplicationType.valueOf(licenseType.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 면허 타입: {}", licenseType);
            return false;
        }

        // 2. 타입별 검증 로직 분기
        switch (appType) {
            case NEW:
                return checkNewEligibility(userSeq, licenseType);
            case REISSUE:
                return checkReissueEligibility(userSeq);
            case RENEWAL:
                return checkRenewalEligibility(userSeq);
            default:
                log.warn("지원하지 않는 타입: {}", appType);
                return false;
        }
    }

    // 신규 면허 검증
    private boolean checkNewEligibility(Long userSeq, String licenseType) {
        Optional<ExternalLicenseStatus> statusOp = externalRepository.findByUserSeqAndLicenseType(userSeq, licenseType);
        return statusOp.map(s -> s.isWrittenPassed() && s.isFunctionPassed() && s.isDrivingPassed()).orElse(false);
    }

    //재발급 검증 (이미 발급받은 면허가 있는지 확인)
    private boolean checkReissueEligibility(Long userSeq) {
        // DB에서 현재 소지 중인 유효한 면허가 있는지 확인하는 로직
        return userLicenseRepository.findByUserSeqAndStatus(userSeq, UserLicense.LicenseStatus.VALID).isPresent();
    }

    // 갱신 검증 (유효기간이 만료 임박인지 확인)
    private boolean checkRenewalEligibility(Long userSeq) {

        return userLicenseRepository.findByUserSeqAndStatus(userSeq, UserLicense.LicenseStatus.VALID)
                .map(license -> license.getExpiryDate().isBefore(LocalDate.now().plusMonths(3))) // 만료 3개월 전
                .orElse(false);
    }

    // 신청서 저장 로직
    @Override
    public LicenseApplication registerApplication(Long userSeq, String userType, ApplicationType type, String receiveLocation, String receiveDate) {

        LocalDate parsedDate = LocalDate.parse(receiveDate);

        LicenseApplication application = LicenseApplication.builder()
                .userSeq(userSeq)
                .userType(userType)
                .type(type)
                .receiveLocation(receiveLocation)
                .receiveDate(parsedDate)
                .build();

        return applicationRepository.save(application);
    }

    @Override
    public LicenseApplication registerApplication(LicenseDTO dto) {
        log.info(">>> [DEBUG] 넘어온 DTO 확인: {}", dto);

        // 1. 여기서 확실하게 검증합니다.
        if (dto.getReceiveDate() == null || dto.getReceiveDate().trim().isEmpty()) {
            log.error(">>> [ERROR] 만료일 데이터가 null입니다. DTO: {}", dto);
            throw new IllegalArgumentException("만료일(expiryDate) 데이터가 전달되지 않았습니다.");
        }

        // 2. 5개 인자를 받는 메서드를 호출할 때, DTO에서 꺼낸 값을 그대로 넣습니다.
        return this.registerApplication(
                dto.getUserSeq(),
                dto.getUserType(),
                dto.getType(),
                dto.getReceiveLocation(),
                dto.getReceiveDate()
        );
    }

    @Override
    public void savePayment(Long userSeq, String merchantUid, String impUid, int amount, Long applicationId) {
        // 1. 결제와 매핑될 신청서 찾기
        LicenseApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청서를 찾을 수 없습니다."));

        // 2. 결제 엔티티 생성
        LicensePayment payment = LicensePayment.builder()
                .licenseApplication(application)
                .merchantUid(merchantUid)
                .impUid(impUid)
                .userSeq(userSeq)
                .amount(amount)
                .status("PAID")
                .build();

        // 3. DB 저장
        paymentRepository.save(payment);
        log.info("결제 정보 저장 완료 - 주문번호: {}", merchantUid);
    }

    @Override
    public boolean isAlreadyApplied(Long userSeq, ApplicationType type) {
        return applicationRepository.existsByUserSeqAndType(userSeq, type);
    }
}