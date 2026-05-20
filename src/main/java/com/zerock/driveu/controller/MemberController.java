package com.zerock.driveu.controller;

import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.dto.SocialUserDTO;
import com.zerock.driveu.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository; // [추가]
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/drive-u/login")
    public String login() {
        return "drive-u/login";
    }

    @GetMapping("/login/signUp")
    public String signUp(HttpSession session, Model model) {
        SocialUserDTO socialUser = (SocialUserDTO) session.getAttribute("socialUser");
        if (socialUser != null) {
            model.addAttribute("socialUser", socialUser);
        }
        return "drive-u/login/signUp";
    }

    @GetMapping("/drive-u/myPage")
    public String myPage() {
        return "drive-u/myPage";
    }

    @PostMapping("/login/signUp")
    public String registerMember(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "pwd", required = false) String pwd,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            HttpServletRequest request) {

        log.info("--- [Controller] 회원가입 요청 접수 ---");
        HttpSession session = request.getSession();
        SocialUserDTO socialUser = (SocialUserDTO) session.getAttribute("socialUser");

        // 1. 서비스에서 DB 저장
        String loginUsername = memberService.registerMember(id, pwd, name, email, phone, socialUser);

        if (socialUser != null) {
            session.removeAttribute("socialUser");
        }

        // 2. 인증 객체 생성
        AuthUserDTO authUser = new AuthUserDTO(
                loginUsername,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                email,
                name
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(
                authUser, null, authUser.getAuthorities()
        );

        // 3. SecurityContext를 생성하고 인증 객체를 담음
        SecurityContext sc = SecurityContextHolder.createEmptyContext();
        sc.setAuthentication(auth);
        SecurityContextHolder.setContext(sc);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

        return "redirect:/";
    }
}