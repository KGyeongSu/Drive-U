package com.zerock.driveu.controller;

import com.zerock.driveu.domain.VideoChapter;
import com.zerock.driveu.service.DuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/drive-u/du")
@RequiredArgsConstructor
public class DuController {

    private final DuService duService;

    @GetMapping
    public String main(Model model) {
        VideoChapter chapter = duService.getFirstDuChapter().orElse(null);

        model.addAttribute("currentPage", "du");
        model.addAttribute("chapter", chapter);
        model.addAttribute("chapters", duService.getDuChapters());

        if(chapter != null){
            model.addAttribute("nextChapter", duService.getNextChapter(chapter.getChapterId()).orElse(null));
            model.addAttribute("quizzes", duService.getRandomQuizDTOs(chapter.getChapterId()));
        } else {
            model.addAttribute("nextChapter", null);
            model.addAttribute("quizzes", List.of());
        }

        return "drive-u/du";
    }

    @GetMapping("/{chapterId}")
    public String detail(@PathVariable Long chapterId, Model model) {
        VideoChapter chapter = duService.getDuChapter(chapterId).orElse(null);

        model.addAttribute("currentPage", "du");
        model.addAttribute("chapter", chapter);
        model.addAttribute("chapters", duService.getDuChapters());
        model.addAttribute("nextChapter", duService.getNextChapter(chapterId).orElse(null));
        model.addAttribute("quizzes", duService.getRandomQuizDTOs(chapterId));

        if (chapter != null) {
            model.addAttribute("nextChapter", duService.getNextChapter(chapterId).orElse(null));
            model.addAttribute("quizzes", duService.getRandomQuizDTOs(chapter.getChapterId()));
        } else {
            model.addAttribute("nextChapter", null);
            model.addAttribute("quizzes", List.of());
        }

        return "drive-u/du";
    }


}