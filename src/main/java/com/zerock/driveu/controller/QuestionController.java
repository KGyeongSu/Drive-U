package com.zerock.driveu.controller;

import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.dto.QuestionRequestDTO;
import com.zerock.driveu.service.QuestionService;
import com.zerock.driveu.util.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/drive-u/userInfo")
@RequiredArgsConstructor
@Log4j2
public class QuestionController {

    private final QuestionService questionService;

    // 문의사항 리스트 전체 보기
    @GetMapping("/questionHome")
    public String questionHome(@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        var questionPage = questionService.getList(pageable);

        PageUtils.addPageAttributes(model, questionPage);
        model.addAttribute("listUrl", "/drive-u/userInfo/listFragment");
        model.addAttribute("detailUrl", "/drive-u/userInfo/questionDetail");

        log.info("문의 리스트 페이지 호출 - 페이지 번호 : " + pageable.getPageNumber());

        // 서비스에서 Page <QuestionListDTO> 를 받아 모델에 넣어 넘겨주기
        model.addAttribute("dataPage", questionPage);

        return "drive-u/userInfo/questionHome";

    }

    // 비동기로 페이징 처리하기
    @GetMapping("/listFragment")
    public String questionListFragment(@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        // 서비스에서 데이터 받아오기
        var questionPage = questionService.getList(pageable);

        PageUtils.addPageAttributes(model, questionPage);

        // 주소 날아감 방지
        model.addAttribute("listUrl", "/drive-u/userInfo/listFragment");
        model.addAttribute("detailUrl", "/drive-u/userInfo/questionDetail");

        log.info("비동기로 paging - 페이지 번호 : " + pageable.getPageNumber());

        model.addAttribute("dataPage", questionPage);

        return "fragments/pagination :: list";

    }

    // 문의 등록 페이지 보기
    @GetMapping("/question")
    public String question() {

        return "drive-u/userInfo/question";

    }

    // 문의 등록
    @PostMapping("/questionRegister")
    public String registerQ (QuestionRequestDTO questionRequestDTO, @AuthenticationPrincipal AuthUserDTO userDTO, RedirectAttributes redirectAttributes) {

        log.info("registerQ 호출 - 작성자 : " + userDTO.getEmail());

        Long id = questionService.register(

                questionRequestDTO,
                userDTO.getEmail(),
                userDTO.isSocial(),
                userDTO.getName()

        );

        redirectAttributes.addFlashAttribute("successMsg", "문의가 성공적으로 등록되었습니다.");
        redirectAttributes.addAttribute("id", id);

        return "redirect:/drive-u/userInfo/questionHome";

    }

    // 문의 상세보기
    @GetMapping("/questionDetail")
    public String questionDetail (@RequestParam("id") Long id, Model model) {

        log.info("문의 상세 페이지 호출 - id : " + id);

        model.addAttribute("questionDetail", questionService.getOne(id));

        return "drive-u/userInfo/question";

    }

    // 문의 수정
    @GetMapping("/questionModify")
    public String questionModify (@RequestParam("id") Long id, Model model) {

        model.addAttribute("questionDetail", questionService.getOne(id));

        return "drive-u/userInfo/question";

    }

    // 문의 수정 등록
    @PostMapping("/questionModifyRegister")
    public String questionModifyRegister (QuestionRequestDTO questionRequestDTO, @RequestParam("id") Long id, @AuthenticationPrincipal AuthUserDTO userDTO, RedirectAttributes redirectAttributes) {

        log.info("문의 수정 등록 : " + id + ", 작성자 : " + userDTO.getEmail());

        // 서비스 update 에 넘겨주기
        questionService.update(id, questionRequestDTO, userDTO.getEmail());

        // 수정 완료 후 메시지 출력
        redirectAttributes.addFlashAttribute("successMsg", "문의 수정이 완료되었습니다.");

        // 문의사항 상세페이지 이동 시 param id 넘김
        redirectAttributes.addAttribute("id", id);

        return "redirect:/drive-u/userInfo/questionDetail";

    }

    // 문의 답변 등록 ps.답변 등록 시 get해오는 건 questionDetail 공유
    @PostMapping("/admin/answerRegister")
    public String adminAnswerRegister (@RequestParam("id") Long id, @RequestParam("answer") String answer, RedirectAttributes redirectAttributes) {

        log.info("관리자 답변 저장 - id : " + id);

        // 서비스에서 저장 처리
        questionService.updateAnswer(id, answer);

        redirectAttributes.addFlashAttribute("successMsg", "답변 등록이 완료되었습니다.");
        // 저장 후 상세 페이지로 보내서 답변 확인
        redirectAttributes.addAttribute("id", id);

        return "redirect:/drive-u/userInfo/questionDetail";

    }

    // 문의 삭제
    @PostMapping("/questionRemove")
    public String remove (@RequestParam("id") Long id, @AuthenticationPrincipal AuthUserDTO userDTO, RedirectAttributes redirectAttributes) {

        log.info("문의 삭제 요청 - id : " + id);

        //서비스에서 삭제 처리
        questionService.delete(id, userDTO.getEmail());

        redirectAttributes.addFlashAttribute("successMsg", "문의 삭제가 완료되었습니다.");

        return "redirect:/drive-u/userInfo/questionHome";

    }

}
