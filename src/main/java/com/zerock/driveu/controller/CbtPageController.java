package com.zerock.driveu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CbtPageController {

    @GetMapping("/drive-u/cbt")
    public String cbtPage() {
        return "drive-u/cbt";
    }
}