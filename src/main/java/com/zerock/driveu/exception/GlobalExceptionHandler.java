package com.zerock.driveu.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 모든 컨트롤러에서 발생하는 Exception 처리할게 ~
@ControllerAdvice
public class GlobalExceptionHandler {

    // .class -> Class Literal (class의 metaData[구조] 가져와 ~)
    // 무권한, 데이터 없음 등
    // HttpServlet : 현재 브라우저의 정보가 담긴 것
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException (IllegalArgumentException e, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        // 예외 msg 저장
        redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());

        // Referer 헤더 확인
        String referer = request.getHeader("Referer");
        String currentUrl = request.getRequestURL().toString();

        // 이전 페이지로 redirect
        // 무한루프 방지
        if (referer != null && !referer.isEmpty() && !referer.equals(currentUrl)) {

            return "redirect:" + referer;

        } else {

            // 없는경우 안전하게 홈페이지로 redirect
            return "redirect:/drive-u";

        }

    }

    // 서버 장애
    @ExceptionHandler(Exception.class)
    public String handleException (Exception e, RedirectAttributes redirectAttributes) {

        // log 남기기
        e.printStackTrace();

        redirectAttributes.addFlashAttribute("errorMsg", "현재 서버에 일시적인 장애가 발생했습니다. 잠시 후 다시 시도해 주세요.");

        return "redirect:/drive-u";

    }

}
