package com.zerock.driveu.service;

import com.zerock.driveu.constant.CourseType;
import com.zerock.driveu.domain.enums.ExamType;
import com.zerock.driveu.domain.enums.StageStatus;
import com.zerock.driveu.dto.ProcessStageDTO;
import com.zerock.driveu.repository.ExamPassRepository;
import com.zerock.driveu.repository.PracticeLicenseRepository;
import com.zerock.driveu.repository.VideoProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 면허취득절차 로드맵의 "이 응시생이 어느 단계까지 왔는지"를 계산.
 * 게이트(자격 차단)는 LicenseStageValidator 책임이고,
 * 여기는 화면 표시용 진행 현황만 담당한다.
 */
@Service
@RequiredArgsConstructor
public class ProcessStatusService {

    private final VideoProgressRepository videoProgressRepository;
    private final ExamPassRepository examPassRepository;
    private final PracticeLicenseRepository practiceLicenseRepository;

    /**
     * 화면 카드 매칭용 — key("DU","WRITTEN"...) → StageStatus.
     * html 에서 ${statusMap['WRITTEN']} 처럼 단계별로 바로 꺼내 쓰기 위함.
     */
    @Transactional(readOnly = true)
    public Map<String, StageStatus> getStatusMap(Long userSeq, String memberType) {
        Map<String, StageStatus> map = new LinkedHashMap<>();
        for (ProcessStageDTO stage : getRoadmap(userSeq, memberType)) {
            map.put(stage.getKey(), stage.getStatus());
        }
        return map;
    }

    @Transactional(readOnly = true)
    public ProcessStageDTO getCurrentStage(Long userSeq, String memberType) {
        for (ProcessStageDTO stage : getRoadmap(userSeq, memberType)) {
            if (stage.getStatus() == StageStatus.CURRENT) {
                return stage;
            }
        }
        return null;   // 전 단계 완료
    }

    @Transactional(readOnly = true)
    public List<ProcessStageDTO> getRoadmap(Long userSeq, String memberType) {

        // 1) 각 단계 통과(done) 여부 — 단계 순서대로
        boolean duDone       = isDuCompleted(userSeq, memberType);
        boolean writtenDone  = isExamPassed(userSeq, memberType, ExamType.WRITTEN);
        boolean functionDone = isExamPassed(userSeq, memberType, ExamType.FUNCTION);
        boolean licenseDone  = hasValidPracticeLicense(userSeq, memberType);
        boolean driveDone    = isExamPassed(userSeq, memberType, ExamType.DRIVE);

        boolean[] done = { duDone, writtenDone, functionDone, licenseDone, driveDone };

        String[] keys   = { "DU", "WRITTEN", "FUNCTION", "PRACTICE_LICENSE", "DRIVE", "LICENSE_ISSUE" };
        String[] titles = { "교통안전교육", "학과시험", "기능시험", "연습면허 발급", "도로주행", "운전면허증 발급" };
        String[] links  = {
                "/drive-u/du",
                "/drive-u/process/wApply1",
                "/drive-u/process/fApply1",
                "/drive-u/process/pLicense1",
                "/drive-u/process/dApply1",
                "/drive-u/card"
        };

        // 2) CURRENT = done이 처음 끊기는 칸. 그 앞은 DONE, 뒤는 LOCKED.
        int currentIdx = firstNotDone(done);   // 전부 done이면 -1

        // 3) 단계별 status 조립
        List<ProcessStageDTO> roadmap = new ArrayList<>();
        for (int i = 0; i < done.length; i++) {
            StageStatus status;
            if (done[i]) {
                status = StageStatus.DONE;
            } else if (i == currentIdx) {
                status = StageStatus.CURRENT;
            } else {
                status = StageStatus.LOCKED;
            }

            roadmap.add(ProcessStageDTO.builder()
                    .key(keys[i])
                    .title(titles[i])
                    .status(status)
                    .linkUrl(links[i])
                    .build());
        }
        // 6칸(면허 발급): 도로주행(마지막 done)이 끝났으면 CURRENT, 아니면 LOCKED
        StageStatus issueStatus = driveDone ? StageStatus.CURRENT : StageStatus.LOCKED;
        roadmap.add(ProcessStageDTO.builder()
                .key(keys[5]).title(titles[5]).status(issueStatus).linkUrl(links[5]).build());

        return roadmap;
    }

    /** done 배열에서 처음으로 false인 인덱스. 전부 true면 -1. */
    private int firstNotDone(boolean[] done) {
        for (int i = 0; i < done.length; i++) {
            if (!done[i]) return i;
        }
        return -1;
    }

    // ── 단계별 통과 판정 (validator의 seam과 동일 기준) ──

    private boolean isDuCompleted(Long userSeq, String memberType) {
        return videoProgressRepository
                .existsByUserSeqAndMemberTypeAndCourse_CourseTypeAndFinalCompletedYn(
                        userSeq, memberType, CourseType.DU, "Y");
    }

    private boolean isExamPassed(Long userSeq, String memberType, ExamType examType) {
        return examPassRepository
                .existsByUserSeqAndMemberTypeAndExamType(userSeq, memberType, examType);
    }

    private boolean hasValidPracticeLicense(Long userSeq, String memberType) {
        return practiceLicenseRepository
                .existsByUserSeqAndMemberTypeAndStatusAndExpiryDateAfter(
                        userSeq, memberType, "ISSUED", LocalDate.now());
    }
}