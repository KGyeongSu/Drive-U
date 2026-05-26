package com.zerock.driveu.controller;

import com.zerock.driveu.dto.QuizSubmitDTO;
import com.zerock.driveu.dto.QuizSubmitResultDTO;
import com.zerock.driveu.service.DuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drive-u/du/quiz")
@RequiredArgsConstructor
public class DuQuizController {

    private final DuService duService;

    @PostMapping("/submit")
    public QuizSubmitResultDTO submitQuiz(@RequestBody QuizSubmitDTO dto) {
        return duService.submitQuiz(dto);
    }
}