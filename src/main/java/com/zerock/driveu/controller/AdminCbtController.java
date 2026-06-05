package com.zerock.driveu.controller;

import com.zerock.driveu.service.CbtService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Controller
@RequestMapping("/drive-u/admin/cbt") // 공통 경로 설정
@RequiredArgsConstructor
public class AdminCbtController {

    private final CbtService cbtService;

    // 업로드 폼 페이지
    @GetMapping("/upload")
    public String uploadForm() {
        return "drive-u/admin/cbt/upload";
    }

    // 파일 처리 로직
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("sourceName") String sourceName,
                             @RequestParam("effectiveDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate) {

        cbtService.uploadQuestionsFromCsv(file, sourceName, effectiveDate);
        return "redirect:/drive-u/admin/cbt/upload?success";
    }
}
