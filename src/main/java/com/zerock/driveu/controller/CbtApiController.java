package com.zerock.driveu.controller;

import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.dto.CbtQuestionDTO;
import com.zerock.driveu.dto.CbtSubmitRequestDTO;
import com.zerock.driveu.dto.CbtSubmitResultDTO;
import com.zerock.driveu.service.CbtService;
import com.zerock.driveu.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cbt")
@RequiredArgsConstructor
public class CbtApiController {

    private final CbtService cbtService;
    private final EligibilityService eligibilityService;

    @GetMapping("/questions/random")
    public List<CbtQuestionDTO> getRandomQuestions(
            @RequestParam(defaultValue = "40") int count
    ) {
        return cbtService.getRandomQuestions(count);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitCbt(
            @RequestBody CbtSubmitRequestDTO requestDTO,
            @AuthenticationPrincipal AuthUserDTO authUser
    ) {
        if (authUser == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        boolean canUseCbt =
                eligibilityService.canUseCbt(
                        authUser.getSeq(),
                        authUser.getMemberType()
                );

        if (!canUseCbt) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("필수 교통안전교육 이수와 필기시험 원서접수 완료 후 CBT를 이용할 수 있습니다.");
        }

        CbtSubmitResultDTO resultDTO = cbtService.submitCbt(requestDTO);

        return ResponseEntity.ok(resultDTO);
    }
}