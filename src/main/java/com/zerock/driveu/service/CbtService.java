package com.zerock.driveu.service;


import com.zerock.driveu.dto.CbtQuestionDTO;
import com.zerock.driveu.dto.CbtSubmitRequestDTO;
import com.zerock.driveu.dto.CbtSubmitResultDTO;

import java.util.List;

public interface CbtService {

    List<CbtQuestionDTO> getRandomQuestions(int count);
    CbtSubmitResultDTO submitCbt(CbtSubmitRequestDTO requestDTO);
}