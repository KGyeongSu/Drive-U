package com.zerock.driveu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {





    @GetMapping("/drive-u/admin/cbt")
    public String adminCbt() {
        return "drive-u/admin/cbt";
    }

}
