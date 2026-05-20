package com.zerock.driveu.config.handler;

import com.zerock.driveu.domain.SocialMember;
import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.dto.SocialUserDTO;
import com.zerock.driveu.repository.SocialMemberRepository;
import com.zerock.driveu.service.SocialUserOAuth2Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@Log4j2
@RequiredArgsConstructor
public class SuccessHandler implements AuthenticationSuccessHandler {

    private final SocialMemberRepository socialMemberRepository;
    private final SocialUserOAuth2Service socialUserOAuth2Service;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        org.springframework.security.core.Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        SocialUserDTO socialUserDTO = socialUserOAuth2Service.getSocialUserdto(provider, oAuth2User);

        String searchKey = socialUserDTO.getSocialKey();

        log.info("▶ [SuccessHandler] 검증 키: " + searchKey);

        Optional<SocialMember> result = socialMemberRepository.findBySocialKey(searchKey);

        if (result.isPresent()) {
            log.info("▶ [SuccessHandler] 기존 회원 로그인 성공");
            SocialMember socialMember = result.get();

            AuthUserDTO authUserDTO = new AuthUserDTO(
                    socialMember.getSocialKey(),
                    "1111",
                    List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")),
                    socialMember.getEmail(),
                    socialMember.getName(),
                    oAuth2User.getAttributes()
            );

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(authUserDTO, null, authUserDTO.getAuthorities())
            );

            response.sendRedirect("/");
        } else {
            log.info("▶ [SuccessHandler] 신규 회원 감지 -> 가입 폼 이동");
            request.getSession().setAttribute("socialUser", socialUserDTO);
            response.sendRedirect("/login/signUp");
        }
    }
}