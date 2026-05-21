package com.zerock.driveu.controller;


import com.zerock.driveu.service.LVideoService;
import lombok.RequiredArgsConstructor;
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
    public String list(Model model) {
        model.addAttribute("currentPage", "lVideo");
        model.addAttribute("videos", lVideoService.getLVideoList());

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