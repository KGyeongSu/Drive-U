package com.zerock.driveu.controller;

import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.dto.MemberDTO;
import com.zerock.driveu.dto.SocialUserDTO;
import com.zerock.driveu.repository.MemberRepository;
import com.zerock.driveu.repository.SocialMemberRepository;
import com.zerock.driveu.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;
    // 필드 2개 추가(경수)
    private final MemberRepository memberRepository;
    private final SocialMemberRepository socialMemberRepository;

    // [핵심] 모든 요청마다 세션에서 socialUser를 꺼내 모델에 자동으로 담아줍니다.
    @ModelAttribute("socialUser")
    public SocialUserDTO getSocialUser(HttpSession session) {
        return (SocialUserDTO) session.getAttribute("socialUser");
    }

    @GetMapping("/drive-u/login")
    public String login() {return "drive-u/loginForm"; }

    @GetMapping("/login/signUp")
    public String signUp(HttpSession session, Model model) {
        SocialUserDTO socialUser = (SocialUserDTO) session.getAttribute("socialUser");

        MemberDTO memberDTO = new MemberDTO();
        if (socialUser != null) {
            memberDTO.setName(socialUser.getName());
            memberDTO.setEmail(socialUser.getEmail());
            memberDTO.setPhone(socialUser.getPhone());
        }

        model.addAttribute("memberDTO", memberDTO);
        return "drive-u/login/signup";
    }

    @PostMapping("/login/signUp")
    public String registerMember(
            @Valid MemberDTO memberDTO,
            BindingResult bindingResult,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        SocialUserDTO socialUser = (SocialUserDTO) session.getAttribute("socialUser");

        // 소셜 가입이 아닐 때(즉, 아이디를 입력받는 상황일 때)만 중복 체크
        if (socialUser == null && memberService.checkIdDuplicate(memberDTO.getId())) {
            bindingResult.rejectValue("id", "duplicate", "이미 사용 중인 아이디입니다.");
        }

        // 1. 시스템(일반+소셜) 내에 이메일 중복이 있는지 먼저 확인
        boolean isEmailDuplicate = memberRepository.existsByEmail(memberDTO.getEmail())
                || socialMemberRepository.existsByEmail(memberDTO.getEmail());

        if (isEmailDuplicate) {
            if (socialUser == null) {
                //일반 가입자는 email이 중복일 경우  가입을 완전히 막아버림
                bindingResult.rejectValue("email", "duplicate", "이미 존재하는 이메일입니다.");
            } else {
                // 소셜는 그냥 튕겨내지 않고, 이미 해당 이메일로 일반 계정이 있으니 로컬 로그인을 권유
                bindingResult.rejectValue("email", "duplicate", "해당 이메일로 가입된 일반 계정이 이미 존재합니다. 일반 로그인을 이용해 주세요.");
            }
        }

        // 유효성 검사
        if (socialUser == null) {
            // 로컬 회원가입 모든 에러Id, Email 중복 및 @Valid 기본 에러 전체 체크
            if (bindingResult.hasErrors()) {
                return "drive-u/login/signup";
            }
        } else {
            // 소셜 회원가입 유효성  name, phone, email 중복체크
            if (bindingResult.hasFieldErrors("name") ||
                    bindingResult.hasFieldErrors("phone") ||
                    bindingResult.hasFieldErrors("email")) {
                return "drive-u/login/signup";
            }
        }

        // 2. 서비스 로직 호출
        String loginUsername = memberService.registerMember(memberDTO, socialUser);

        if (socialUser != null) {
            session.removeAttribute("socialUser");
        }

        // 회원 seq 조회(경수추가)
        Long seq;
        String memberType;
        if (socialUser != null) {
            seq = socialMemberRepository.findBySocialKey(loginUsername).orElseThrow().getSeq();
            memberType = "SOCIAL";
        } else {
            seq = memberRepository.findById(loginUsername).orElseThrow().getSeq();
            memberType = "MEMBER";
        }
        // 3. 인증 객체 생성
        AuthUserDTO authUser = new AuthUserDTO(
                loginUsername,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                memberDTO.getEmail(),
                memberDTO.getName(),
                memberDTO.getPhone(),
                (socialUser != null),
                seq,
                memberType
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(
                authUser, null, authUser.getAuthorities()
        );

        SecurityContext sc = SecurityContextHolder.createEmptyContext();
        sc.setAuthentication(auth);
        SecurityContextHolder.setContext(sc);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

        return "redirect:/";
    }

    // 아이디 중복 확인 요청을 받는 컨트롤러 메서드
    @GetMapping("/login/checkId")
    @ResponseBody // HTML이 아니라 결과값(true/false)만 반환하겠다는 선언
    public boolean checkId(@RequestParam("id") String id) {
        return memberService.checkIdDuplicate(id);
    }

    // 이메일 중복 확인 요청을 받는 컨트롤러 메서드
    @GetMapping("/login/checkEmail")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email) {
        String trimmedEmail = (email != null) ? email.trim() : "";

        // 일반 회원 테이블에서 검색
        boolean isLocalExist = memberRepository.existsByEmail(trimmedEmail);

        //소셜 회원 테이블에서 검색
        boolean isSocialExist = socialMemberRepository.existsByEmail(trimmedEmail);

        //둘 중 하나라도 이미 존재한다면 가입 불가 처리
        if (isLocalExist || isSocialExist) {
            return "DUPLICATE";
        }

        return "AVAILABLE";
    }
}