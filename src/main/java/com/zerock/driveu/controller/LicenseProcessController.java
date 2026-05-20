package com.zerock.driveu.controller;

import com.zerock.driveu.constant.ExamConstants;
import com.zerock.driveu.constant.SessionConst;
import com.zerock.driveu.dto.ApplySessionDTO;
import com.zerock.driveu.dto.ExamScheduleDTO;
import com.zerock.driveu.domain.ExamSchedule;
import com.zerock.driveu.domain.TestCenter;
import com.zerock.driveu.repository.ExamScheduleRepository;
import com.zerock.driveu.repository.TestCenterRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/drive-u/process")
@RequiredArgsConstructor
public class LicenseProcessController {

    private final TestCenterRepository testCenterRepository;
    private final ExamScheduleRepository examScheduleRepository;

    @GetMapping
    public String process() {
        return "drive-u/process";
    }

    @GetMapping("/checking")
    public String checking() {
        return "drive-u/process/checking";
    }

    @GetMapping("/wApply1")
    public String wApply1() {
        return "drive-u/process/wApply1";
    }

    @GetMapping("/wApply2")
    public String wApply2(Model model) {
        model.addAttribute("licenseTypes", ExamConstants.LICENSE_TYPES);
        model.addAttribute("regions", testCenterRepository.findDistinctRegions());
        model.addAttribute("centers", testCenterRepository.findAll());
        model.addAttribute("examTimes", ExamConstants.EXAM_TIMES);

        return "drive-u/process/wApply2";
    }
    @GetMapping("/wApply2/schedules")
    @ResponseBody
    public List<ExamScheduleDTO> getSchedules(@RequestParam String examType,
                                              @RequestParam Long testCenterId,
                                              @RequestParam @DateTimeFormat
                            (iso = DateTimeFormat.ISO.DATE) LocalDate examDate){

        TestCenter testCenter = testCenterRepository.findById(testCenterId)
                .orElseThrow(() -> new IllegalArgumentException
                        ("시험장 없음: " + testCenterId));

        return examScheduleRepository
                .findByExamTypeAndTestCenterAndExamDate(examType, testCenter, examDate)
                .stream()
                .map(ExamScheduleDTO::from)
                .toList();
    }
    @PostMapping("/wApply2")
    public String wApply2Submit(@RequestParam String licenseType,
                                @RequestParam Long examScheduleId,
                                HttpSession session) {

        // 1. ExamSchedule 조회 (실제 존재하는지 검증)
        ExamSchedule schedule = examScheduleRepository.findById(examScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정 없음: " + examScheduleId));

        // 2. 시작 1시간 전까지만 신청 가능
        LocalDateTime examStart = LocalDateTime.of(schedule.getExamDate(), schedule.getExamTime());
        if (examStart.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IllegalArgumentException("시험 시작 1시간 전까지만 신청 가능합니다.");
        }

        // 3. 세션 DTO 생성
        ApplySessionDTO applyData = ApplySessionDTO.builder()
                .examType(ExamConstants.EXAM_WRITTEN)
                .licenseType(licenseType)
                .testCenterId(schedule.getTestCenter().getTestCenterId())
                .examDate(schedule.getExamDate())
                .examTime(schedule.getExamTime().toString())
                .examScheduleId(examScheduleId)
                .build();

        // 4. 세션 저장
        session.setAttribute(SessionConst.APPLY_DATA, applyData);

        // 5. wApply3로 redirect
        return "redirect:/drive-u/process/wApply3";
    }
    @GetMapping("/wApply3")
    public String wApply3(HttpSession session, Model model) {

        // wApply2 안 거치고 직접 진입한 경우 방어 (세션DTO 없으면 리턴)
        ApplySessionDTO applyData = (ApplySessionDTO)
                session.getAttribute(SessionConst.APPLY_DATA);
        if (applyData == null) {
            return "redirect:/drive-u/process/wApply1";
        }

        // 세션 LOGIN_MEMBER에서 회원 타입 확인 후 MemberDTO / SocialMemberDTO 분기 조회 예정
        // Member 인증 완성 후 memberRepository.findById로 교체예정.
        Map<String, Object> member = new HashMap<>();
        member.put("name",  "김경수");
        member.put("phone", "010-1234-5678");
        member.put("email", "test@drive-u.com");

        model.addAttribute("member", member);
        return "drive-u/process/wApply3";
    }
    @PostMapping("/wApply3")
    public String wApply3Submit(@RequestParam String contactPhone,
                                @RequestParam String contactEmail,
                                HttpSession session){
        // 1. 세션 검증 (GET와 마찬가지로 직접 호출 방지)
        ApplySessionDTO applyData = (ApplySessionDTO)
                session.getAttribute(SessionConst.APPLY_DATA);
        if (applyData == null) {
            return "redirect:/drive-u/process/wApply1";
        }
        // 2. wApply2에서 넘어온 세션 DTO에 연락처/이메일 추가
        applyData.setContactPhone(contactPhone);
        applyData.setContactEmail(contactEmail);

        return "redirect:/drive-u/process/wApply4";
    }
    @GetMapping("/wApply4")
    public String wApply4(HttpSession session, Model model) {

        return "drive-u/process/wApply4";
    }

    @GetMapping("/wApply5")
    public String wApply5() {
        return "drive-u/process/wApply5";
    }

    @GetMapping("/fApply1")
    public String fApply1() {
        return "drive-u/process/fApply1";
    }

    @GetMapping("/fApply2")
    public String fApply2() {
        return "drive-u/process/fApply2";
    }

    @GetMapping("/fApply3")
    public String fApply3() {
        return "drive-u/process/fApply3";
    }

    @GetMapping("/fApply4")
    public String fApply4() {
        return "drive-u/process/fApply4";
    }

    @GetMapping("/fApply5")
    public String fApply5() {
        return "drive-u/process/fApply5";
    }

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
