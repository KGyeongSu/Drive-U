package com.zerock.driveu.repository;

import com.zerock.driveu.domain.Application;
import com.zerock.driveu.domain.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByMerchantUid(String merchantUid);

    Optional<Application> findByMemberIdAndExamSchedule_ScheduleIdAndStatus(
            Long memberId,
            Long scheduleId,
            ApplicationStatus status);

    long countByExamSchedule_ScheduleIdAndStatus(
            Long scheduleId, ApplicationStatus status);

    // 회원의 가장 최근 COMPLETED 신청건 조회 (wApply5 표시용)
    Optional<Application> findFirstByMemberIdAndStatusOrderByCreatedAtDesc(
            Long memberId, ApplicationStatus status);

}