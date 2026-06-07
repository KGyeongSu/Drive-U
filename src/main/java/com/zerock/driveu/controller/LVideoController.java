package com.zerock.driveu.controller;


import com.zerock.driveu.domain.VideoCourse;
import com.zerock.driveu.service.LVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/drive-u/lVideo")
@RequiredArgsConstructor
public class LVideoController {

    private final LVideoService lVideoService;

    // /drive-u/lVideo
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "order") String sort,
                       Model model) {
        Page<VideoCourse> videoPage = lVideoService.getLearningVideos(page, size, sort);

        model.addAttribute("videoPage", videoPage);
        model.addAttribute("videos", videoPage.getContent());
        model.addAttribute("sort", sort);

        return "drive-u/lVideo";
    }

    // /drive-u/lVideo/{courseId}
    @GetMapping("/{courseId}")
    public String detail(@PathVariable Long courseId, Model model) {
        model.addAttribute("currentPage", "lVideo");
        model.addAttribute("video", lVideoService.getLVideo(courseId).orElse(null));

        return "drive-u/lVideo/lVideoDetail";
    }
}