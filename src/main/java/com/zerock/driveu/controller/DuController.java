package com.zerock.driveu.controller;

import com.zerock.driveu.domain.VideoChapter;
import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.repository.ChapterProgressRepository;
import com.zerock.driveu.service.DuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/drive-u/du")
@RequiredArgsConstructor
public class DuController {

    private final DuService duService;
    private final ChapterProgressRepository chapterProgressRepository;

    @GetMapping
    public String main(@AuthenticationPrincipal AuthUserDTO authUser, Model model) {

        Long userSeq = authUser.getSeq();
        String memberType = authUser.getMemberType();

        VideoChapter chapter = duService.getFirstDuChapter().orElse(null);


        model.addAttribute("currentPage", "du");
        model.addAttribute("chapter", chapter);
        model.addAttribute("chapterMenus", duService.getChapterMenus(userSeq, memberType));

        if (chapter != null) {
            model.addAttribute("nextChapter", duService.getNextChapter(chapter.getChapterId()).orElse(null));
            model.addAttribute("quizzes", duService.getRandomQuizDTOs(chapter.getChapterId()));
        } else {
            model.addAttribute("nextChapter", null);
            model.addAttribute("quizzes", List.of());
        }

        model.addAttribute("userSeq", userSeq);
        model.addAttribute("memberType", memberType);

        return "drive-u/du";
    }

    @GetMapping("/{chapterId}")
    public String detail(@PathVariable Long chapterId, @AuthenticationPrincipal AuthUserDTO authUser, Model model) {

        Long userSeq = authUser.getSeq();
        String memberType = authUser.getMemberType();

        if (!duService.canAccessChapter(userSeq, memberType, chapterId)) {
            VideoChapter firstChapter = duService.getFirstDuChapter().orElse(null);

            if (firstChapter != null) {
                return "redirect:/drive-u/du/" + firstChapter.getChapterId();
            }

            return "redirect:/drive-u/du";
        }

        VideoChapter chapter = duService.getDuChapter(chapterId).orElse(null);

        model.addAttribute("currentPage", "du");
        model.addAttribute("chapter", chapter);
        model.addAttribute("chapterMenus", duService.getChapterMenus(userSeq, memberType));

        if (chapter != null) {
            model.addAttribute("nextChapter", duService.getNextChapter(chapter.getChapterId()).orElse(null));
            model.addAttribute("quizzes", duService.getRandomQuizDTOs(chapter.getChapterId()));
        } else {
            model.addAttribute("nextChapter", null);
            model.addAttribute("quizzes", List.of());
        }

        return "drive-u/du";
    }


}