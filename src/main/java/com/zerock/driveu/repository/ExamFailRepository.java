package com.zerock.driveu.repository;

import com.zerock.driveu.domain.ExamFail;
import com.zerock.driveu.domain.enums.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamFailRepository extends JpaRepository<ExamFail, Long> {

    // 명단 렌더링용: 그 신청건에 해당하는 불합격행이 있는지 꺼내서 examFailId 추출
    Optional<ExamFail> findByUserSeqAndMemberTypeAndExamTypeAndLicenseType(
            Long userSeq, String memberType, ExamType examType, String licenseType);
}