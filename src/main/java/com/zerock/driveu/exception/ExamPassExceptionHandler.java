package com.zerock.driveu.exception;

import com.zerock.driveu.controller.AdminExamPassController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// AdminExamPassController 에서 난 예외만 잡는다. (assignableTypes 로 범위 한정)
@RestControllerAdvice(assignableTypes = AdminExamPassController.class)
public class ExamPassExceptionHandler {

    // 없는 합격건 취소 / 잘못된 신청건 등 → 400 + JSON
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest()                                  // 400
                .body(Map.of("message", e.getMessage()));
    }

    // 결제 안 된 신청건 합격처리 시도 등 상태 위반 → 409 + JSON
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)                   // 409
                .body(Map.of("message", e.getMessage()));
    }
}