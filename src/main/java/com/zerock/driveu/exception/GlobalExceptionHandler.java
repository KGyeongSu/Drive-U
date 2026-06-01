package com.zerock.driveu.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

        // 요청 id 추출해 redirect 시 같이 넘겨줌
        String id = request.getParameter("id");

        if (id != null) {

            redirectAttributes.addAttribute("id", id);

        }

        // 에러 발생 시 메시지 보여주고 나서 출력될 페이지
        return "redirect:/drive-u/userInfo/questionDetail";

    }
    //정적 리소스
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResourceFoundException(NoResourceFoundException e) {
        // favicon.ico, .well-known 같은 없는 정적 리소스는 그냥 404로 끝냄
    }

    // 서버 장애
    @ExceptionHandler(Exception.class)
    public String handleException (Exception e, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        // log 남기기
        e.printStackTrace();

        // API 요청은 화면 redirect 시키면 fetch가 HTML을 JSON으로 읽으려다 터짐
        String uri = request.getRequestURI();
        if (uri.startsWith("/drive-u/du/quiz")) {
            throw new RuntimeException(e);
        }
        redirectAttributes.addFlashAttribute("errorMsg", "현재 서버에 일시적인 장애가 발생했습니다. 잠시 후 다시 시도해 주세요.");

        return "redirect:/drive-u/userInfo/questionHome";

    }

}
