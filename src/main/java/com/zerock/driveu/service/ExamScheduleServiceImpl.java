package com.zerock.driveu.service;

import com.zerock.driveu.constant.ExamConstants;
import com.zerock.driveu.domain.ExamSchedule;
import com.zerock.driveu.domain.TestCenter;
import com.zerock.driveu.domain.enums.ExamType;
import com.zerock.driveu.dto.ExamScheduleCreateDTO;
import com.zerock.driveu.repository.ExamScheduleRepository;
import com.zerock.driveu.repository.TestCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamScheduleServiceImpl implements ExamScheduleService {

    private final ExamScheduleRepository examScheduleRepository;
    private final TestCenterRepository testCenterRepository;

    @Transactional
    public int register(ExamScheduleCreateDTO dto) {

        // 1. 시험장 조회 (ID -> 엔티티)
        TestCenter testCenter = testCenterRepository.findById(dto.getTestCenterId())
                .orElseThrow(() -> new IllegalArgumentException("시험장 없음: " + dto.getTestCenterId()));

        // 2. 종별 조건부 검증 + 정규화
        String licenseType = resolveLicenseType(dto.getExamType(), dto.getLicenseType());

        // 3. DTO -> Entity (시간마다 한 건씩)
        List<ExamSchedule> schedules = new ArrayList<>();
        for (LocalTime examTime : dto.getExamTimes()) {
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

        // 4. 한 번에 저장 후 개수 반환
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
}
