package com.zerock.driveu.service;

import com.zerock.driveu.constant.ExamConstants;
import com.zerock.driveu.domain.ExamSchedule;
import com.zerock.driveu.domain.TestCenter;
import com.zerock.driveu.domain.enums.ExamType;
import com.zerock.driveu.dto.ExamScheduleCreateDTO;
import com.zerock.driveu.dto.ExamScheduleViewDTO;
import com.zerock.driveu.repository.ApplicationRepository;
import com.zerock.driveu.repository.ExamScheduleRepository;
import com.zerock.driveu.repository.TestCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamScheduleServiceImpl implements ExamScheduleService {

    private final ExamScheduleRepository examScheduleRepository;
    private final TestCenterRepository testCenterRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public int register(ExamScheduleCreateDTO dto) {

        TestCenter testCenter = testCenterRepository.findById(dto.getTestCenterId())
                .orElseThrow(() -> new IllegalArgumentException("시험장 없음: " + dto.getTestCenterId()));

        String licenseType = resolveLicenseType(dto.getExamType(), dto.getLicenseType());

        List<ExamSchedule> schedules = new ArrayList<>();
        for (LocalTime examTime : dto.getExamTimes()) {

            // 중복 검사 — 이미 같은 일정 있으면 건너뜀
            boolean exists = (licenseType == null)
                    ? examScheduleRepository.existsByExamTypeAndLicenseTypeIsNullAndTestCenterAndExamDateAndExamTime(
                    dto.getExamType(), testCenter, dto.getExamDate(), examTime)
                    : examScheduleRepository.existsByExamTypeAndLicenseTypeAndTestCenterAndExamDateAndExamTime(
                    dto.getExamType(), licenseType, testCenter, dto.getExamDate(), examTime);

            if (exists) continue;   // 이미 있으면 INSERT 안 함

            ExamSchedule schedule = ExamSchedule.builder()
                    .examType(dto.getExamType())
                    .licenseType(licenseType)
                    .testCenter(testCenter)
                    .examDate(dto.getExamDate())
                    .examTime(examTime)
                    .maxCount(dto.getMaxCount())
                    .build();
            schedules.add(schedule);
        }

        examScheduleRepository.saveAll(schedules);
        return schedules.size();
    }

    // 시험 종류별 종별 규칙 (학과=null, 기능/도로 = 목록에 있어야)
    private String resolveLicenseType(ExamType examType, String licenseType){
        if(examType == ExamType.WRITTEN){
            return null;        // 학과는 일정에 종별 안 담음.
        }
        var allowed = (examType == ExamType.FUNCTION)
                ? ExamConstants.FUNCTION_LICENSE_TYPES : ExamConstants.DRIVE_LICENSE_TYPES;
        if(licenseType == null || !allowed.contains(licenseType)) {
            throw new IllegalArgumentException("해당 시험의 응시 종별이 아닙니다: " + licenseType);
        }
        return licenseType;
    }

    @Transactional(readOnly = true)
    public Map<LocalDate, Integer> getMonthlyCounts(Long testCenterId, int year, int month){

        // 1. 시험장 조회
        TestCenter testCenter = testCenterRepository.findById(testCenterId)
                .orElseThrow(() -> new IllegalArgumentException("시험장 없음: " + testCenterId));

        // 2. 그 달의 1일 ~ 말일 계산
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // 3. 시험장 + 날짜 범위로 그 달 일정 전부 조회
        List<ExamSchedule> schedules =
                examScheduleRepository.findByTestCenterAndExamDateBetween(testCenter, start, end);

        // 4. 날짜별로 건수 집계 (날짜 -> 개수)
        return schedules.stream()
                .collect(Collectors.groupingBy(
                        ExamSchedule::getExamDate,
                        Collectors.summingInt(s -> 1)
                ));
    }

    @Transactional(readOnly = true)
    public List<ExamScheduleViewDTO> getWeeklySchedules(Long testCenterId, LocalDate weekStart) {

        TestCenter testCenter = testCenterRepository.findById(testCenterId)
                .orElseThrow(() -> new IllegalArgumentException("시험장 없음: " + testCenterId));

        // 월요일(weekStart) ~ 토요일(+5일). 일요일은 휴무라 제외
        LocalDate weekEnd = weekStart.plusDays(5);

        List<ExamSchedule> schedules = examScheduleRepository
                .findByTestCenterAndExamDateBetween(testCenter, weekStart, weekEnd);

        // 엔티티 -> ViewDTO 변환 (LAZY testCenter 안 건드림 → 직렬화 안전 + scheduleId 포함)
        return schedules.stream()
                .map(ExamScheduleViewDTO::from)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<ExamScheduleViewDTO> getSlotSchedules(Long testCenterId, LocalDate examDate, LocalTime examTime) {

        TestCenter testCenter = testCenterRepository.findById(testCenterId)
                .orElseThrow(() -> new IllegalArgumentException("시험장 없음: " + testCenterId));

        List<ExamSchedule> schedules = examScheduleRepository
                .findByTestCenterAndExamDateAndExamTime(testCenter, examDate, examTime);

        return schedules.stream()
                .map(ExamScheduleViewDTO::from)
                .toList();
    }
    @Transactional
    public void delete(Long scheduleId) {
        if (!examScheduleRepository.existsById(scheduleId)) {
            throw new IllegalArgumentException("일정 없음: " + scheduleId);
        }
        // 이 일정으로 신청한 사람이 있으면 삭제 막기
        if (applicationRepository.existsByExamScheduleScheduleId(scheduleId)) {
            throw new IllegalStateException("이미 신청자가 있어 삭제할 수 없습니다.");
        }
        examScheduleRepository.deleteById(scheduleId);
    }
}
