package com.zerock.driveu.controller;

import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.dto.QuizSubmitDTO;
import com.zerock.driveu.dto.QuizSubmitResultDTO;
import com.zerock.driveu.service.DuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public QuizSubmitResultDTO submitQuiz(
            @RequestBody QuizSubmitDTO dto,
            @AuthenticationPrincipal AuthUserDTO authUser
    ) {
        System.out.println("========== DU QUIZ SUBMIT 진입 ==========");
        System.out.println("authUser = " + authUser);
        if (authUser == null) {
            return QuizSubmitResultDTO.builder()
                    .passed(false)
                    .totalCount(0)
                    .correctCount(0)
                    .wrongCount(0)
                    .message("로그인이 필요합니다.")
                    .build();
        }

        Long userSeq = authUser.getSeq();
        String memberType = authUser.getMemberType();

        return duService.submitQuiz(dto, userSeq, memberType);
    }
}