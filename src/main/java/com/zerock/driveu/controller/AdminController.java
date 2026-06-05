package com.zerock.driveu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/drive-u/admin")
    public String adminDashboard() {
        return "drive-u/admin/dashboard";
    }

    @GetMapping("/drive-u/admin/video")
    public String adminVideo() {
        return "drive-u/admin/video";
    }

    @GetMapping("/drive-u/admin/video/du")
    public String adminDuVideo() {
        return "drive-u/admin/video/du";
    }

    @GetMapping("/drive-u/admin/video/lVideo")
    public String adminLearningVideo() {
        return "drive-u/admin/video/lVideo";
    }

    @GetMapping("/drive-u/admin/cbt")
    public String adminCbt() {
        return "drive-u/admin/cbt";
    }

    @GetMapping("/drive-u/admin/examSchedule")
    public String adminExamSchedule() {
        return "drive-u/admin/examSchedule";
    }

    //권한이 없는 곳으로 접속했을때 예외처리 주소
    @GetMapping("/drive-u/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorMsg", "관리자만 접근할 수 있는 페이지입니다.");
        return "drive-u/error/403";
    }
}
