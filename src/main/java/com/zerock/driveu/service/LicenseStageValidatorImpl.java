package com.zerock.driveu.service;

import com.zerock.driveu.constant.CourseType;
import com.zerock.driveu.domain.enums.ExamType;
import com.zerock.driveu.repository.ExamPassRepository;
import com.zerock.driveu.repository.PracticeLicenseRepository;
import com.zerock.driveu.repository.VideoProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LicenseStageValidatorImpl implements LicenseStageValidator {

    private final PracticeLicenseRepository practiceLicenseRepository;
    private final ExamPassRepository examPassRepository;
    private final VideoProgressRepository videoProgressRepository;

    // ── GET : 종별 무관 게이트 ──

    @Override
    @Transactional(readOnly = true)
    public boolean canEnter(Long userSeq, String memberType, ExamType examType) {
        return switch (examType) {
            case WRITTEN  -> isDuCompleted(userSeq, memberType);              // 학과 ← 교통안전교육
            case FUNCTION -> isWrittenPassedAny(userSeq, memberType);         // 기능 ← 학과 합격(종별 불문)
            case DRIVE    -> isFunctionPassedAny(userSeq, memberType)         // 도로주행 ← 기능 합격(종별 불문)
                    && hasAnyValidPracticeLicense(userSeq, memberType); //          + 유효 연습면허(종별 불문)
        };
    }

    @Override
    public void validateEnter(Long userSeq, String memberType, ExamType examType) {
        if (!canEnter(userSeq, memberType, examType)) {
            throw new IllegalStateException(examType + " 진입 자격 미충족");
        }
    }

    // ── POST : 종별 일치 게이트 ──

    @Override
    @Transactional(readOnly = true)
    public boolean canApply(Long userSeq, String memberType, ExamType examType, String licenseType) {
        return switch (examType) {
            case WRITTEN  -> isDuCompleted(userSeq, memberType);                          // 학과는 종별 개념 없음
            case FUNCTION -> isWrittenPassed(userSeq, memberType, licenseType);           // 기능 ← 같은 종별 학과 합격
            case DRIVE    -> isFunctionPassed(userSeq, memberType, licenseType)           // 도로주행 ← 같은 종별 기능 합격
                    && hasValidPracticeLicense(userSeq, memberType, licenseType);   //          + 같은 종별 연습면허
        };
    }

    @Override
    public void validateApply(Long userSeq, String memberType, ExamType examType, String licenseType) {
        if (!canApply(userSeq, memberType, examType, licenseType)) {
            throw new IllegalStateException(examType + "(" + licenseType + ") 신청 자격 미충족");
        }
    }

    // ── 연습면허 ──

    @Override
    @Transactional(readOnly = true)
    public boolean canEnterPracticeLicense(Long userSeq, String memberType) {
        return isFunctionPassedAny(userSeq, memberType);                                  // 기능 합격(종별 불문)
    }

    @Override
    public void validateEnterPracticeLicense(Long userSeq, String memberType) {
        if (!canEnterPracticeLicense(userSeq, memberType)) {
            throw new IllegalStateException("연습면허 발급 진입 자격 미충족(기능 합격 필요)");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canIssuePracticeLicense(Long userSeq, String memberType, String licenseType) {
        return isFunctionPassed(userSeq, memberType, licenseType);                        // 같은 종별 기능 합격
    }

    @Override
    public void validateIssuePracticeLicense(Long userSeq, String memberType, String licenseType) {
        if (!canIssuePracticeLicense(userSeq, memberType, licenseType)) {
            throw new IllegalStateException("연습면허(" + licenseType + ") 발급 자격 미충족");
        }
    }

    // ── seam : 종별 무관 ──

    private boolean isDuCompleted(Long userSeq, String memberType) {

        return videoProgressRepository
                .existsByUserSeqAndMemberTypeAndCourse_CourseTypeAndFinalCompletedYn
                        (userSeq, memberType, CourseType.DU, "Y");
    }

    private boolean isWrittenPassedAny(Long userSeq, String memberType) {
        // 학과 합격(종별 불문) — exam_pass 에 WRITTEN 행이 하나라도 있으면 합격
        return examPassRepository
                .existsByUserSeqAndMemberTypeAndExamType(userSeq, memberType, ExamType.WRITTEN);
    }

    private boolean isFunctionPassedAny(Long userSeq, String memberType) {
        // 기능 합격(종별 불문) — exam_pass 에 FUNCTION 행이 하나라도 있으면 합격
        return examPassRepository
                .existsByUserSeqAndMemberTypeAndExamType(userSeq, memberType, ExamType.FUNCTION);
    }

    private boolean hasAnyValidPracticeLicense(Long userSeq, String memberType) {
        return practiceLicenseRepository
                .existsByUserSeqAndMemberTypeAndStatusAndExpiryDateAfter(
                        userSeq, memberType, "ISSUED", LocalDate.now());
    }

    // ── seam : 종별 일치 ──

    private boolean isWrittenPassed(Long userSeq, String memberType, String licenseType) {
        // 같은 종별 학과 합격 — exam_pass 에 (WRITTEN, 해당 종별) 행이 있으면 합격
        return examPassRepository
                .existsByUserSeqAndMemberTypeAndExamTypeAndLicenseType(
                        userSeq, memberType, ExamType.WRITTEN, licenseType);
    }

    private boolean isFunctionPassed(Long userSeq, String memberType, String licenseType) {
        // 같은 종별 기능 합격 — exam_pass 에 (FUNCTION, 해당 종별) 행이 있으면 합격
        return examPassRepository
                .existsByUserSeqAndMemberTypeAndExamTypeAndLicenseType(
                        userSeq, memberType, ExamType.FUNCTION, licenseType);
    }

    private boolean hasValidPracticeLicense(Long userSeq, String memberType, String licenseType) {
        return practiceLicenseRepository
                .existsByUserSeqAndMemberTypeAndLicenseTypeAndStatusAndExpiryDateAfter(
                        userSeq, memberType, licenseType, "ISSUED", LocalDate.now());
    }
}