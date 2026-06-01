package com.zerock.driveu.controller;

import com.zerock.driveu.domain.Payment;
import com.zerock.driveu.domain.PracticeLicense;
import com.zerock.driveu.domain.enums.PaymentStatus;
import com.zerock.driveu.service.PaymentService;
import com.zerock.driveu.service.PracticeLicenseService;
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
    private final PracticeLicenseService practiceLicenseService;

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

    // 연습면허 발급 결제 검증
    @PostMapping("/verify/practice-license")
    public ResponseEntity<Map<String, Object>> verifyPracticeLicense(@RequestBody Map<String, String> body) {
        String paymentId = body.get("paymentId");
        log.info("연습면허 결제 검증 요청: paymentId={}", paymentId);

        PracticeLicense license = practiceLicenseService.verifyPayment(paymentId);
        boolean success = "ISSUED".equals(license.getStatus());

        return ResponseEntity.ok(Map.of(
                "success", success,
                "status", license.getStatus()
        ));
    }
}