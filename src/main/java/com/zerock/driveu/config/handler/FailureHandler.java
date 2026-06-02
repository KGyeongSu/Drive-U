package com.zerock.driveu.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String errorMassage;
        String redirectUrl = "/drive-u/login";

        //중복 로그인으로 예외처리
        if(exception instanceof SessionAuthenticationException){
            redirectUrl += "?expired=true";
        }
        else{ // 아이디 / 패스워드 불일치 예외처리
            redirectUrl += "?error=true";
        }
        response.sendRedirect(redirectUrl);

    }
}
