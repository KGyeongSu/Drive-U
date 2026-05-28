package com.zerock.driveu.controller;

import com.zerock.driveu.config.PortoneProperties;
import com.zerock.driveu.constant.ExamConstants;
import com.zerock.driveu.constant.SessionConst;
import com.zerock.driveu.domain.PracticeLicense;
import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.repository.PracticeLicenseRepository;
import com.zerock.driveu.service.PracticeLicenseService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/drive-u/process")
@RequiredArgsConstructor
public class PracticeLicenseController {

    private final PracticeLicenseService practiceLicenseService;
    private final PracticeLicenseRepository practiceLicenseRepository;
    private final PortoneProperties portoneProperties;

    // ── pLicense1 : 자격검증 + 안내 ──
    @GetMapping("/pLicense1")
    public String pLicense1() {
        // TODO: validator 호출 자리 (보류 — w/f/d와 한꺼번에 적용 예정)
        // practiceLicenseValidator.validate(userSeq, memberType);
        return "drive-u/process/pLicense1";
    }

    // ── pLicense2 : 정보확인 (회원정보 표시 + 종별 선택) ──
    @GetMapping("/pLicense2")
    public String pLicense2(@AuthenticationPrincipal AuthUserDTO authUser, Model model) {

        Map<String, Object> member = new HashMap<>();
        member.put("name", authUser.getRealName());

        model.addAttribute("member", member);

        // TODO: (임시: 종별 직접 선택 (1종보통/2종보통))
        // → validator 붙일 때 기능시험 합격 종별 자동 주입으로 교체 예정 (선택 제거)
        model.addAttribute("licenseTypes", ExamConstants.PRACTICE_LICENSE_TYPES);

        return "drive-u/process/pLicense2";
    }

    @PostMapping("/pLicense2")
    public String pLicense2Submit(@RequestParam String licenseType,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        // 종별 방어: 허용 목록에 없으면 되돌림
        if (!ExamConstants.PRACTICE_LICENSE_TYPES.contains(licenseType)) {
            redirectAttributes.addFlashAttribute("errorMessage", "연습면허 발급 가능 종별이 아닙니다.");
            return "redirect:/drive-u/process/pLicense2";
        }

        // 세션에 종별 저장 (wApply의 APPLY_DATA와 별도 키)
        session.setAttribute(SessionConst.PRACTICE_LICENSE_TYPE, licenseType);

        return "redirect:/drive-u/process/pLicense3";
    }

    // ── pLicense3 : 결제 (PracticeLicense INSERT → 포트원 결제창) ──
    @GetMapping("/pLicense3")
    public String pLicense3(@AuthenticationPrincipal AuthUserDTO authUser,
                            HttpSession session, Model model) {

        // 직접 진입 방어 (종별 세션 없으면 처음으로)
        String licenseType = (String) session.getAttribute(SessionConst.PRACTICE_LICENSE_TYPE);
        if (licenseType == null) {
            return "redirect:/drive-u/process/pLicense1";
        }

        Long userSeq = authUser.getSeq();
        String memberType = authUser.getMemberType();

        // PracticeLicense INSERT (기존 미결제건 있으면 CANCELLED 처리됨)
        PracticeLicense entity =
                practiceLicenseService.createApplication(userSeq, memberType, licenseType);

        // 결제창 호출용 데이터
        model.addAttribute("licenseType", licenseType);
        model.addAttribute("fee", entity.getFee());
        model.addAttribute("merchantUid", entity.getMerchantUid());
        model.addAttribute("storeId", portoneProperties.getStoreId());
        model.addAttribute("channelKey", portoneProperties.getChannelKey());

        // 포트원 결제창 표시용 고객정보 (회원정보 그대로 — 연락처/이메일 컬럼 없음)
        model.addAttribute("customerName", authUser.getRealName());
        model.addAttribute("customerPhone", authUser.getPhone());
        model.addAttribute("customerEmail", authUser.getEmail());

        return "drive-u/process/pLicense3";
    }

    // ── pLicense4 : 발급완료 (면허증 출력 + 세션 클리어) ──
    @GetMapping("/pLicense4")
    public String pLicense4(@AuthenticationPrincipal AuthUserDTO authUser,
                            HttpSession session, Model model) {

        Long userSeq = authUser.getSeq();
        String memberType = authUser.getMemberType();

        // 가장 최근 발급(ISSUED)건 조회
        PracticeLicense license = practiceLicenseRepository
                .findFirstByUserSeqAndMemberTypeAndStatusOrderByCreatedAtDesc(
                        userSeq, memberType, "ISSUED")
                .orElseThrow(() -> new IllegalStateException("발급된 연습면허 없음"));

        model.addAttribute("license", license);
        model.addAttribute("memberName", authUser.getRealName());

        // 세션 클리어 (재진입 방어)
        session.removeAttribute(SessionConst.PRACTICE_LICENSE_TYPE);

        return "drive-u/process/pLicense4";
    }
}