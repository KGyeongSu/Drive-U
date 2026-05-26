package com.zerock.driveu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/drive-u"})
    public String index() {
        return "/index";
    }

    @GetMapping("/drive-u/myPage")
    public String myPage() {
        return "drive-u/myPage";
    }

    @GetMapping("/drive-u/login")
    public String login() {
        return "drive-u/login";
    }

    @GetMapping("/login/signUp")
    public String signUp() {
        return "login/signUp";
    }

    @GetMapping("/drive-u/du")
    public String safetyEducation() {
        return "drive-u/du";
    }

    @GetMapping("/drive-u/lVideo")
    public String learningVideo() {
        return "drive-u/lVideo";
    }

    @GetMapping("/drive-u/cbt")
    public String cbt() {
        return "drive-u/cbt";
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

    @GetMapping("/drive-u/process")
    public String process() {
        return "drive-u/process";
    }

    @GetMapping("/drive-u/process/checking")
    public String checking() {
        return "drive-u/process/checking";
    }

    @GetMapping("/drive-u/process/wApply1")
    public String wApply1() {
        return "drive-u/process/wApply1";
    }

    @GetMapping("/drive-u/process/wApply2")
    public String wApply2() {
        return "drive-u/process/wApply2";
    }

    @GetMapping("/drive-u/process/wApply3")
    public String wApply3() {
        return "drive-u/process/wApply3";
    }

    @GetMapping("/drive-u/process/wApply4")
    public String wApply4() {
        return "drive-u/process/wApply4";
    }

    @GetMapping("/drive-u/process/wApply5")
    public String wApply5() {
        return "drive-u/process/wApply5";
    }

    @GetMapping("/drive-u/process/fApply1")
    public String fApply1() {
        return "drive-u/process/fApply1";
    }

    @GetMapping("/drive-u/process/fApply2")
    public String fApply2() {
        return "drive-u/process/fApply2";
    }

    @GetMapping("/drive-u/process/fApply3")
    public String fApply3() {
        return "drive-u/process/fApply3";
    }

    @GetMapping("/drive-u/process/fApply4")
    public String fApply4() {
        return "drive-u/process/fApply4";
    }

    @GetMapping("/drive-u/process/fApply5")
    public String fApply5() {
        return "drive-u/process/fApply5";
    }

    @GetMapping("/drive-u/process/dApply1")
    public String dApply1() {
        return "drive-u/process/dApply1";
    }

    @GetMapping("/drive-u/process/dApply2")
    public String dApply2() {
        return "drive-u/process/dApply2";
    }

    @GetMapping("/drive-u/process/dApply3")
    public String dApply3() {
        return "drive-u/process/dApply3";
    }

    @GetMapping("/drive-u/process/dApply4")
    public String dApply4() {
        return "drive-u/process/dApply4";
    }

    @GetMapping("/drive-u/process/dApply5")
    public String dApply5() {
        return "drive-u/process/dApply5";
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
}