package com.zerock.driveu.controller;

import com.zerock.driveu.dto.NoticeRequestDTO;
import com.zerock.driveu.dto.NoticeResponseDTO;
import com.zerock.driveu.service.NoticeService;
import com.zerock.driveu.util.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/drive-u/userInfo/noticeHome")
@RequiredArgsConstructor
@Log4j2
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    // 공지사항 메인
    public String noticeHome (@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        log.info("공지사항 리스트 메인 호출 - 페이지 번호 : " + pageable.getPageNumber());

        var noticeHome = noticeService.getList(pageable);

        PageUtils.addPageAttributes(model, noticeHome);

        model.addAttribute("listUrl", "/drive-u/userInfo/noticeHome/listFragment");
        model.addAttribute("detailUrl", "/drive-u/userInfo/noticeHome/noticeDetail");
        model.addAttribute("dataPage", noticeHome);

        return "drive-u/userInfo/noticeHome";

    }

    @GetMapping("/listFragment")
    public String noticeListPragment (@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        log.info("공지사항 비동기 paging - 페이지 번호 : " + pageable.getPageNumber());

        var noticePage = noticeService.getList(pageable);

        PageUtils.addPageAttributes(model, noticePage);

        model.addAttribute("listUrl", "/drive-u/userInfo/noticeHome/listFragment");
        model.addAttribute("detailUrl", "/drive-u/userInfo/noticeHome/noticeDetail");
        model.addAttribute("dataPage", noticePage);

        return "fragments/pagination :: list";

    }

    // 공지사항 등록 페이지 보기
    @GetMapping("/registerForm")
    public String registerForm () {

        return "drive-u/userInfo/noticeDetail";

    }

    // 공지사항 등록
    @PostMapping("/noticeRegister")
    public String register (NoticeRequestDTO noticeRequestDTO, @RequestParam(value = "files", required = false) List<MultipartFile> files, RedirectAttributes redirectAttributes) throws IOException {

        noticeService.registerNotice(noticeRequestDTO, files);

        redirectAttributes.addFlashAttribute("successMsg", "문의사항 등록이 완료되었습니다.");

        return "redirect:/drive-u/userInfo/noticeHome";

    }

    // 공지 상세보기
    @GetMapping("/noticeDetail")
    public String noticeDetail (@RequestParam("id") Long id, Model model) {

        log.info("문의 상세 페이지 호출 - id : " + id);

        model.addAttribute("noticeDetail", noticeService.getOne(id));

        return "drive-u/userInfo/noticeDetail";

    }

    // 문의 수정
    @GetMapping("/modifyForm")
    public String questionModify (@RequestParam("id") Long id, Model model) {

        NoticeResponseDTO dto = noticeService.getOne(id);
        System.out.println("컨트롤러 확인 - 파일개수: " + (dto.getFiles() != null ? dto.getFiles().size() : "null"));
        model.addAttribute("noticeDetail", dto);

        return "drive-u/userInfo/noticeDetail";

    }

    // 공지사항 수정 등록
    @PostMapping("/noticeModify")
    public String modify (@RequestParam("id") Long id, NoticeRequestDTO noticeRequestDTO, @RequestParam(value = "files", required = false) List<MultipartFile> newFiles, @RequestParam(value = "deleteIds", required = false) List<Long> deleteIds, RedirectAttributes redirectAttributes) throws IOException {

        noticeService.updateNotice(id, noticeRequestDTO, newFiles, deleteIds);

        redirectAttributes.addFlashAttribute("successMsg", "공지사항 수정이 완료되었습니다.");
        redirectAttributes.addAttribute("id", id);

        return "redirect:/drive-u/userInfo/noticeHome/noticeDetail";

    }

    @PostMapping("/noticeDelete")
    public String delete (@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {

        noticeService.deleteNotice(id);

        redirectAttributes.addFlashAttribute("successMsg", "공지사항 삭제가 완료되었습니다");

        return "redirect:/drive-u/userInfo/noticeHome";

    }

}
