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

    @Override
    @Transactional(readOnly = true)
    public boolean checkLicenseEligibility(Long userSeq, String licenseType) {

        String typeToConvert = licenseType.toUpperCase();
        if ("UP".equals(typeToConvert)) typeToConvert = "RENEWAL";
        if ("RE".equals(typeToConvert)) typeToConvert = "REISSUE";

        ApplicationType appType;
        try {
            appType = ApplicationType.valueOf(typeToConvert);
        } catch (IllegalArgumentException e) {
            return false;
        }

        boolean result = false;
        switch (appType) {
            case NEW:
                result = checkNewEligibility(userSeq, licenseType);
                break;
            case REISSUE:
                result = checkReissueEligibility(userSeq);
                break; 
            case RENEWAL:
                result = checkRenewalEligibility(userSeq);
                break;
            default:
                return false;
        }

        return result;
    }

    private boolean checkNewEligibility(Long userSeq, String licenseType) {
        Optional<ExternalLicenseStatus> statusOp = externalRepository.findByUserSeqAndLicenseType(userSeq, licenseType);
        boolean passed = statusOp.map(s -> s.isWrittenPassed() && s.isFunctionPassed() && s.isDrivingPassed()).orElse(false);
        return passed;
    }

    private boolean checkReissueEligibility(Long userSeq) {
        boolean exists = userLicenseRepository.findByUserSeqAndStatus(userSeq, UserLicense.LicenseStatus.VALID).isPresent();
        return exists;
    }

    private boolean checkRenewalEligibility(Long userSeq) {
        return userLicenseRepository.findByUserSeqAndStatus(userSeq, UserLicense.LicenseStatus.VALID)
                .map(license -> {
                    LocalDate now = LocalDate.now();
                    LocalDate threeMonthsLater = now.plusMonths(3);
                    boolean isEligible = license.getExpiryDate().isAfter(now) && license.getExpiryDate().isBefore(threeMonthsLater);

                    return isEligible;
                })
                .orElseGet(() -> {
                    return false;
                });
    }

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
        if (dto.getReceiveDate() == null || dto.getReceiveDate().trim().isEmpty()) {
            throw new IllegalArgumentException("만료일 데이터가 전달되지 않았습니다.");
        }
        return this.registerApplication(dto.getUserSeq(), dto.getUserType(), dto.getType(), dto.getReceiveLocation(), dto.getReceiveDate());
    }

    @Override
    public void savePayment(Long userSeq, String merchantUid, String impUid, int amount, Long applicationId) {
        LicenseApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청서를 찾을 수 없습니다."));
        LicensePayment payment = LicensePayment.builder()
                .licenseApplication(application)
                .merchantUid(merchantUid)
                .impUid(impUid)
                .userSeq(userSeq)
                .amount(amount)
                .status("PAID")
                .build();
        paymentRepository.save(payment);
    }

    @Override
    public boolean isAlreadyApplied(Long userSeq, ApplicationType type) {
        return applicationRepository.existsByUserSeqAndType(userSeq, type);
    }
}