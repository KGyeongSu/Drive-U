package com.zerock.driveu.controller;

import com.zerock.driveu.domain.LicenseApplication.ApplicationType;
import com.zerock.driveu.domain.LicenseApplication;
import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.dto.PaymentRequestDTO;
import com.zerock.driveu.service.LicenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/drive-u/card")
@RequiredArgsConstructor
public class LicenseCardController {

    private final LicenseService licenseService;

    @Value("${iamport.imp-code}")
    private String impCode;


    @GetMapping("/{type}/step1")
    public String step1(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "drive-u/card/step1";
    }

    @GetMapping("/{type}/step2")
    public String step2(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "drive-u/card/step2";
    }


    @GetMapping("/{type}/step3")
    public String step3(@PathVariable String type,
                        @AuthenticationPrincipal AuthUserDTO authUser,
                        Model model) {

        Long userSeq = authUser.getSeq();
        boolean isEligible = licenseService.checkLicenseEligibility(userSeq, type);

        if (!isEligible) {
            return "redirect:/drive-u/card/" + type + "/fail";
        }

        model.addAttribute("impCode", impCode);
        model.addAttribute("type", type);
        return "drive-u/card/step3";
    }

    @PostMapping("/{type}/complete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> completeProcess(
            @PathVariable String type,
            @AuthenticationPrincipal AuthUserDTO authUser,
            @RequestBody PaymentRequestDTO dto) {

        ApplicationType appType = ApplicationType.valueOf(type.toUpperCase());

        if (licenseService.isAlreadyApplied(authUser.getSeq(), appType)) {
            log.warn(">>> [중복 신청 시도] 유저 {}가 이미 {} 타입을 신청했습니다.", authUser.getSeq(), appType);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "이미 해당 면허를 신청하셨습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            LicenseApplication app = licenseService.registerApplication(
                    authUser.getSeq(),
                    authUser.getMemberType(),
                    appType,
                    dto.getReceiveLocation(),
                    dto.getReceiveDate()
            );

            licenseService.savePayment(
                    authUser.getSeq(),
                    dto.getMerchant_uid(),
                    dto.getImp_uid(),
                    dto.getAmount(),
                    app.getId()
            );

            Map<String, String> response = new HashMap<>();
            response.put("redirectUrl", "/drive-u/card/" + type + "/success");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error(">>> [결제 처리 중 에러 발생]: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/{type}/success")
    public String successPage(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "drive-u/card/success";
    }

    @GetMapping("/{type}/check-duplicate")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(
            @PathVariable String type,
            @AuthenticationPrincipal AuthUserDTO authUser) {

        ApplicationType appType = ApplicationType.valueOf(type.toUpperCase());
        boolean isApplied = licenseService.isAlreadyApplied(authUser.getSeq(), appType);

        Map<String, Boolean> result = new HashMap<>();
        result.put("isApplied", isApplied);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{type}/check")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkEligibility(
            @PathVariable String type,
            @AuthenticationPrincipal AuthUserDTO authUser) {

        boolean isEligible = licenseService.checkLicenseEligibility(authUser.getSeq(), type);
        Map<String, Boolean> response = new HashMap<>();
        response.put("eligible", isEligible);
        return ResponseEntity.ok(response);
    }


    @GetMapping(value = "", produces = "text/html")
    public String card(Model model) {
        model.addAttribute("type", "new");
        return "drive-u/card";
    }


    @GetMapping(value = "/re", produces = "text/html")
    public String cardRe(Model model) {
        model.addAttribute("type", "reissue");
        return "drive-u/card/re";
    }

    @GetMapping(value = "/up", produces = "text/html")
    public String cardUp(Model model) {
        model.addAttribute("type", "renewal");
        return "drive-u/card/up";
    }
}