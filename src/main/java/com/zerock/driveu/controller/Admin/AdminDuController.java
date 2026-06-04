package com.zerock.driveu.controller.Admin;

import com.zerock.driveu.dto.admin.DuReplaceDTO;
import com.zerock.driveu.service.admin.AdminDuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/drive-u/admin/video")
public class AdminDuController {

    private final AdminDuService adminDuService;

    // 교통안전교육 챕터 목록
    @GetMapping("/du")
    public String duChapterList(Model model) {
        model.addAttribute("chapters", adminDuService.getActiveDuChapters());

        return "drive-u/admin/video/du";
    }

    // 새 버전 등록 폼
    @GetMapping("/{chapterId}/replace")
    public String duReplaceForm(@PathVariable Long chapterId, Model model) {
        model.addAttribute("chapter", adminDuService.getChapter(chapterId));
        model.addAttribute("form", adminDuService.createDefaultReplaceForm(chapterId));

        return "drive-u/admin/video/duReplace";
    }

    // 새 버전 등록 처리
    @PostMapping("/{chapterId}/replace")
    public String replaceChapter(@PathVariable Long chapterId,
                                 @ModelAttribute("form") DuReplaceDTO form) {

        adminDuService.replaceChapter(chapterId, form);

        return "redirect:/drive-u/admin/video";
    }

    @GetMapping("/lVideo")
    public String adminLearningVideo() {
        return "drive-u/admin/video/lVideo";
    }
}