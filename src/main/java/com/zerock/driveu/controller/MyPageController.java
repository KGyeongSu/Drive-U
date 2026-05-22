package com.zerock.driveu.controller;

import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.dto.MyPageDTO;
import com.zerock.driveu.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/drive-u")
@RequiredArgsConstructor
@Log4j2
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/myPage")
    public String myPage(
            @AuthenticationPrincipal AuthUserDTO localUser,  // 로컬 세션 출입증 검사
            @AuthenticationPrincipal OAuth2User socialUser,  // 소셜 세션 출입증 검사
            Model model
    ) {

        // 서비스단에 두 출입증을 다 던져서 가공된 DTO를 받아옵니다.
        MyPageDTO myPageInfo = myPageService.getMyPageInfo(localUser, socialUser);

        //타임리프 화면에서 쓸 수 있도록 "user"라는 이름으로 DTO를 배달통(Model)에 담습니다.
        model.addAttribute("user", myPageInfo);

        return "drive-u/myPage";
    }

    @PostMapping("/updateAddress")
    @ResponseBody
    public String updateAddress(@AuthenticationPrincipal AuthUserDTO authUser,
                                @RequestParam("address") String address) {

        if (authUser == null || authUser.getUsername() == null) {
            return "FAIL: 로그인 세션이 만료되었습니다.";
        }

        try {
            myPageService.updateAddress(authUser.getUsername(), address);
            return "SUCCESS";
        } catch (Exception e) {
            return "FAIL: " + e.getMessage();
        }
    }

}