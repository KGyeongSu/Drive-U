package com.zerock.driveu.service;


import com.zerock.driveu.dto.CbtQuestionDTO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface CbtService {

    List<CbtQuestionDTO> getRandomQuestions(int count);

    // MultipartFile을 받아 시즌 정보와 함께 처리 (육상우)
    void uploadQuestionsFromCsv(MultipartFile file, String sourceName, LocalDate effectiveDate);
}