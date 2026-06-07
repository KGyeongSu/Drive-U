package com.zerock.driveu.service;

import com.zerock.driveu.domain.*;
import com.zerock.driveu.domain.enums.ApplicationStatus;
import com.zerock.driveu.domain.enums.ExamType;
import com.zerock.driveu.dto.ExamCandidateDTO;
import com.zerock.driveu.dto.ExamPassRequestDTO;
import com.zerock.driveu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamPassService {

    private final ApplicationRepository applicationRepository;
    private final ExamPassRepository examPassRepository;
    private final MemberRepository memberRepository;
    private final SocialMemberRepository socialMemberRepository;
    private final ExamFailRepository examFailRepository;


    @Transactional
    public boolean process(ExamPassRequestDTO dto) {

        // 1. 신청건 조회 (없으면 예외)
        Application application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("신청건 없음: " + dto.getApplicationId()));

        // 2. 결제완료(COMPLETED) 신청건만 합격처리 대상
        if (application.getStatus() != ApplicationStatus.COMPLETED) {
            throw new IllegalStateException("결제완료된 신청건만 합격처리 가능");
        }

        ExamType examType = application.getExamType();
        String licenseType = application.getLicenseType();
        Long score = dto.getScore();

        // 3. 합격 판정 (서버가 한다 — 클라이언트 안 믿음)
        if (!examType.isPassed(licenseType, score)) {
            // 불합격 → exam_fail 에 기록 (화면 새로고침해도 빨간표시 유지됨)

            // 중복 불합격 방지 (합격 쪽 already 가드의 미러)
            // 같은 회원·시험·종별 불합격행이 이미 있으면 또 넣지 않음 → 행 0~1개 유지
            boolean alreadyFailed = examFailRepository
                    .findByUserSeqAndMemberTypeAndExamTypeAndLicenseType(
                            application.getUserSeq(),
                            application.getMemberType(),
                            examType,
                            licenseType)
                    .isPresent();
            if (alreadyFailed) {
                return false;   // 이미 불합격 기록 있음 → 그대로 둠
            }

            ExamFail examFail = ExamFail.builder()
                    .userSeq(application.getUserSeq())
                    .memberType(application.getMemberType())
                    .examType(examType)
                    .licenseType(licenseType)
                    .score(score)
                    .build();
            // failedDate, createdAt 은 @PrePersist 가 자동으로 채움
            examFailRepository.save(examFail);
            return false;
        }

        // 4. 중복 합격 방지 (이미 같은 회원·시험·종별 합격행 있으면 스킵)
        boolean already = examPassRepository
                .existsByUserSeqAndMemberTypeAndExamTypeAndLicenseType(
                        application.getUserSeq(),
                        application.getMemberType(),
                        examType,
                        licenseType);
        if (already) {
            return true;    // 이미 합격 상태 → 그대로 둠
        }

        // 5. 합격 기록 INSERT (정보는 전부 Application에서 복사)
        ExamPass examPass = ExamPass.builder()
                .userSeq(application.getUserSeq())
                .memberType(application.getMemberType())
                .examType(examType)
                .licenseType(licenseType)
                .score(score)
                .build();
        // passedDate, createdAt 은 @PrePersist 가 자동으로 오늘 날짜 채움

        examPassRepository.save(examPass);
        return true;
    }

    @Transactional(readOnly = true)
    public List<ExamCandidateDTO> getCandidates() {
        return applicationRepository
                .findByStatus(ApplicationStatus.COMPLETED)
                .stream()
                .map(app -> {
                    // 이 신청건에 딱 맞는 합격행이 있나? 있으면 examPassId, 없으면 null
                    Long examPassId = examPassRepository
                            .findByUserSeqAndMemberTypeAndExamTypeAndLicenseType(
                                    app.getUserSeq(),
                                    app.getMemberType(),
                                    app.getExamType(),
                                    app.getLicenseType())
                            .map(ExamPass::getExamPassId)   // 있으면 PK 꺼냄
                            .orElse(null);                  // 없으면 null (미합격)

                    // 불합격행이 있나? 있으면 examFailId, 없으면 null
                    Long examFailId = examFailRepository
                            .findByUserSeqAndMemberTypeAndExamTypeAndLicenseType(
                                    app.getUserSeq(),
                                    app.getMemberType(),
                                    app.getExamType(),
                                    app.getLicenseType())
                            .map(ExamFail::getExamFailId)   // 있으면 PK 꺼냄
                            .orElse(null);                  // 없으면 null (불합격 아님)

                    return ExamCandidateDTO.from(app, examPassId, examFailId,
                            resolveName(app.getUserSeq(), app.getMemberType()));
                })
                .toList();
    }

    @Transactional
    public void cancel(Long examPassId) {

        //    화면에서 본 합격건이 서버에 실제로 있는지 확인하고 지운다.
        //    이미 지워졌거나 잘못된 id면 여기서 막힘.
        ExamPass examPass = examPassRepository.findById(examPassId)
                .orElseThrow(() -> new IllegalArgumentException("없는 합격건: " + examPassId));

        examPassRepository.delete(examPass);
    }
    @Transactional
    public void cancelFail(Long examFailId) {

        // 화면에서 본 불합격건이 서버에 실제로 있는지 확인하고 지운다.
        // 지우면 점수 input 이 다시 열려서 재입력 가능해짐.
        ExamFail examFail = examFailRepository.findById(examFailId)
                .orElseThrow(() -> new IllegalArgumentException("없는 불합격건: " + examFailId));

        examFailRepository.delete(examFail);
    }

    private String resolveName(Long userSeq, String memberType) {
        if ("SOCIAL".equals(memberType)) {
            return socialMemberRepository.findById(userSeq)
                    .map(SocialMember::getName)
                    .orElse("(탈퇴회원)");
        }
        return memberRepository.findById(userSeq)
                .map(Member::getName)
                .orElse("(탈퇴회원)");
    }
}