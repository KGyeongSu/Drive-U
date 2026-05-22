package com.zerock.driveu.controller;

import com.zerock.driveu.domain.Payment;
import com.zerock.driveu.domain.enums.PaymentStatus;
import com.zerock.driveu.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 프론트에서 결제 완료 후 검증 요청
    // 요청 body: { "paymentId": "ORD_..." }
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> body) {

        String paymentId = body.get("paymentId");
        log.info("결제 검증 요청: paymentId={}", paymentId);

        Payment payment = paymentService.verifyPayment(paymentId);

        boolean success = payment.getStatus() == PaymentStatus.PAID;

        return ResponseEntity.ok(Map.of(
                "success", success,
                "status", payment.getStatus().name()
        ));
    }
}