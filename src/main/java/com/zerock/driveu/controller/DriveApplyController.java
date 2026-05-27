package com.zerock.driveu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/drive-u/process")
@RequiredArgsConstructor
public class DriveApplyController {

    @GetMapping("/dApply1")
    public String dApply1() {
        return "drive-u/process/dApply1";
    }

    @GetMapping("/dApply2")
    public String dApply2() {
        return "drive-u/process/dApply2";
    }

    @GetMapping("/dApply3")
    public String dApply3() {
        return "drive-u/process/dApply3";
    }

    @GetMapping("/dApply4")
    public String dApply4() {
        return "drive-u/process/dApply4";
    }

    @GetMapping("/dApply5")
    public String dApply5() {
        return "drive-u/process/dApply5";
    }
}
