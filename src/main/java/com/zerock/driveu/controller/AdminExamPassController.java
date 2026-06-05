package com.zerock.driveu.controller;

import com.zerock.driveu.dto.ExamCandidateDTO;
import com.zerock.driveu.dto.ExamPassRequestDTO;
import com.zerock.driveu.service.ExamPassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/drive-u/admin/examPass")
@RequiredArgsConstructor
public class AdminExamPassController {

    private final ExamPassService examPassService;

    // 특정 시험일정의 응시자 명단 조회
    @GetMapping("/candidates")
    public ResponseEntity<List<ExamCandidateDTO>> candidates() {
        return ResponseEntity.ok(examPassService.getCandidates());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> process(@Valid @RequestBody ExamPassRequestDTO dto) {

        boolean passed = examPassService.process(dto);

        return ResponseEntity.ok(Map.of(
                "passed", passed,
                "message", passed ? "합격 처리됐습니다." : "불합격 (기준 점수 미달)"
        ));
    }

    @DeleteMapping("/{examPassId}")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable Long examPassId) {

        examPassService.cancel(examPassId);   // 없는 id면 Service가 예외 던짐

        return ResponseEntity.ok(Map.of(
                "message", "합격이 취소됐습니다."
        ));
    }
}