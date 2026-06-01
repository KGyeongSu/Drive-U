package com.zerock.driveu.controller;

import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CbtPageController {

    private final EligibilityService eligibilityService;

    @GetMapping("/drive-u/cbt")
    public String cbtPage( @AuthenticationPrincipal AuthUserDTO authUser,
                           Model model) {

        System.out.println("========== CBT 컨트롤러 진입 ==========");

        Long userSeq = authUser.getSeq();
        String memberType = authUser.getMemberType();

        if (!eligibilityService.canUseCbt(userSeq, memberType)) {
            model.addAttribute("message", "교통안전교육을 모두 이수해야 CBT 문제은행을 이용할 수 있습니다.");
            return "redirect:/drive-u/du";
        }

        return "drive-u/cbt";
    }
}