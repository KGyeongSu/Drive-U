package com.zerock.driveu.service;

import com.zerock.driveu.dto.ExamScheduleCreateDTO;

public interface ExamScheduleService {
    int register(ExamScheduleCreateDTO dto);       // 등록 후 생성된 scheduleId반환
}
