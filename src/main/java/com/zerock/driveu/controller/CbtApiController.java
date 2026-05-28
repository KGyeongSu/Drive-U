package com.zerock.driveu.controller;

import com.zerock.driveu.dto.CbtQuestionDTO;
import com.zerock.driveu.service.CbtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cbt")
@RequiredArgsConstructor
public class CbtApiController {

    private final CbtService cbtService;

    @GetMapping("/questions/random")
    public List<CbtQuestionDTO> getRandomQuestions(
            @RequestParam(defaultValue = "40") int count
    ) {
        return cbtService.getRandomQuestions(count);
    }
}