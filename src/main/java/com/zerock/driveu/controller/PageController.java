package com.zerock.driveu.controller;

import com.zerock.driveu.service.LawsService;
import com.zerock.driveu.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final NoticeService noticeService;
    private final LawsService lawsService;

    @GetMapping({"/", "/drive-u"})
    public String index(Model model) {

        model.addAttribute("noticeList", noticeService.getMainNotice(7));
        model.addAttribute("lawList", lawsService.getMainLaw(7));

        return "/index";
    }

    @GetMapping("/drive-u/Driving")
    public String Driving() {
        return "drive-u/Driving";
    }
    @GetMapping("/drive-u/driving/fDriving")
    public String functionDriving() {
        return "drive-u/driving/fDriving";
    }

    @GetMapping("/drive-u/driving/rDriving")
    public String roadDriving() {
        return "drive-u/driving/rDriving";
    }

}