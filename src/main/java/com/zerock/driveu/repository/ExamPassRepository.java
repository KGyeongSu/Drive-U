package com.zerock.driveu.repository;

import com.zerock.driveu.domain.ExamPass;
import com.zerock.driveu.domain.enums.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamPassRepository extends JpaRepository<ExamPass, Long> {

    // 종별 무관 (GET용) — isWrittenPassedAny / isFunctionPassedAny
    // examType 만 WRITTEN / FUNCTION
    boolean existsByUserSeqAndMemberTypeAndExamType(
            Long userSeq, String memberType, ExamType examType);

    // 종별 일치 (POST용) — isWrittenPassed / isFunctionPassed
    boolean existsByUserSeqAndMemberTypeAndExamTypeAndLicenseType(
            Long userSeq, String memberType, ExamType examType, String licenseType);
}