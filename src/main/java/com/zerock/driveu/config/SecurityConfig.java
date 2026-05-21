package com.zerock.driveu.config;

import com.zerock.driveu.config.handler.SuccessHandler;
import com.zerock.driveu.service.CustomUserDetailsService;
import com.zerock.driveu.service.SocialLoginProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SocialLoginProcessorService socialLoginProcessorService;
    private final CustomUserDetailsService customUserDetailsService;
    private final SuccessHandler successHandler;

    @Bean //이게 회원가입 하고 비밀번호 암호화처리
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                //비로그인 유저
                        .requestMatchers(
                                "/drive-u",
                                "/drive-u/login",
                                "/drive-u/lVideo",
                                "/drive-u/cbt",
                                "/drive-u/Driving",
                                "/drive-u/process",
                                "/drive-u/process/checking",
                                "/drive-u/userInfo",
                                "/drive-u/userInfo/law",
                                "/drive-u/userInfo/location",
                                "/login/signUp",
                                "/css/**", "/js/**", "/images/**" // 정적 리소스도 꼭 포함하세요!
                        ).permitAll()
                // 관리자 권한
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 로그인 유저는 아무거나
                .anyRequest().authenticated()
        )
                //내가 만든 로그인 페이지
                .formLogin(form -> form
                        .loginPage("/drive-u/login")
                        .loginProcessingUrl("/drive-u/login")
                        .usernameParameter("id")             //HTML input id name 매칭
                        .passwordParameter("pwd")            //HTML input pwd name 매칭
                        .permitAll()
                )
                //소셜 로그인도 이 페이지 사용하게 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/drive-u/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(socialLoginProcessorService)
                        )
                        .successHandler(successHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/drive-u/logout")
                        .logoutSuccessUrl("/drive-u/login")//로그아웃 시 로그인 페이지 이동
                        .invalidateHttpSession(true)// 서버 메모리상의 HTTP 세션 무효화
                        .deleteCookies("JSESSIONID")// 브라우저에 저장된 로그인 쿠키 삭제
                );
        http.userDetailsService(customUserDetailsService);
        return http.build();
    }
}