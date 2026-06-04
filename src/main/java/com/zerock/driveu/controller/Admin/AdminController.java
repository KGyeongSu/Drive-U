package com.zerock.driveu.controller.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/drive-u/admin")
    public String adminDashboard() {
        return "drive-u/admin/dashboard";
    }



    @GetMapping("/drive-u/admin/cbt")
    public String adminCbt() {
        return "drive-u/admin/cbt";
    }

    @GetMapping("/drive-u/admin/examSchedule")
    public String adminExamSchedule() {
        return "drive-u/admin/examSchedule";
    }
}
