package com.zerock.driveu.repository;

import com.zerock.driveu.domain.Application;
import com.zerock.driveu.domain.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByMerchantUid(String merchantUid);

    Optional<Application> findByUserSeqAndMemberTypeAndExamSchedule_ScheduleIdAndStatus(
            Long userSeq,
            String memberType,
            Long scheduleId,
            ApplicationStatus status);

    long countByExamSchedule_ScheduleIdAndStatus(
            Long scheduleId, ApplicationStatus status);

    // 회원의 가장 최근 COMPLETED 신청건 조회 (wApply5 표시용)
    Optional<Application> findFirstByUserSeqAndMemberTypeAndStatusOrderByCreatedAtDesc(
            Long userSeq,
            String memberType,
            ApplicationStatus status);

    // Application 엔티티가 ExamSchedule를 schedule 필드로 참조한다면
    boolean existsByExamScheduleScheduleId(Long scheduleId);

    // 특정 달의 시험 접수 개수 (admin metaData)
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    // 결제완료 신청건 전체 (응시자 명단 — 화면에서 시험장·시간대로 필터)
    List<Application> findByStatus(ApplicationStatus status);

}