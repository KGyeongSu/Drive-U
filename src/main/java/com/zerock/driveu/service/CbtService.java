package com.zerock.driveu.service;


import com.zerock.driveu.dto.CbtQuestionDTO;

import java.util.List;

public interface CbtService {

    List<CbtQuestionDTO> getRandomQuestions(int count);
}