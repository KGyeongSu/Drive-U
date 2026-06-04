package com.zerock.driveu.controller;

import org.springframework.stereotype.Controller;
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

}
