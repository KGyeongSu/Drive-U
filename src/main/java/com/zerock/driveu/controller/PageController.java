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

    @GetMapping("/drive-u/card")
    public String card() {
        return "drive-u/card";
    }

    @GetMapping("/drive-u/card/re")
    public String cardRe() {
        return "drive-u/card/re";
    }

    @GetMapping("/drive-u/card/up")
    public String cardUp() {
        return "drive-u/card/up";
    }

    @GetMapping("/drive-u/userInfo")
    public String userInfo() {
        return "drive-u/userInfo";
    }

    @GetMapping("/drive-u/userInfo/question")
    public String question() {
        return "drive-u/userInfo/question";
    }

    @GetMapping("/drive-u/userInfo/law")
    public String userInfoLaw() {
        return "drive-u/userInfo/law";
    }

    @GetMapping("/drive-u/userInfo/location")
    public String location() {
        return "drive-u/userInfo/location";
    }
}
