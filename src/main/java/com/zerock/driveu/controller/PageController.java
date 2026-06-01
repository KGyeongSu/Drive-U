package com.zerock.driveu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/drive-u"})
    public String index() {
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

    @GetMapping("/drive-u/userInfo")
    public String userInfo() {
        return "drive-u/userInfo";
    }

    @GetMapping("/drive-u/userInfo/law")
    public String userInfoLaw() {
        return "drive-u/userInfo/law";
    }
}