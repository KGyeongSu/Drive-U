package com.zerock.driveu.controller;

import com.zerock.driveu.constant.ExamConstants;
import com.zerock.driveu.domain.ExamSchedule;
import com.zerock.driveu.domain.TestCenter;
import com.zerock.driveu.domain.enums.ExamType;
import com.zerock.driveu.dto.ExamScheduleCreateDTO;
import com.zerock.driveu.dto.ExamScheduleViewDTO;
import com.zerock.driveu.repository.ExamScheduleRepository;
import com.zerock.driveu.repository.TestCenterRepository;
import com.zerock.driveu.service.ExamScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/drive-u/admin/examSchedule")
@RequiredArgsConstructor
public class AdminExamScheduleController {

    private final ExamScheduleService examScheduleService;
    private final TestCenterRepository testCenterRepository;
    private final ExamScheduleRepository examScheduleRepository;

    @GetMapping
    public String form(Model model)
    {
        model.addAttribute("examTypes", ExamType.values());
        model.addAttribute("functionLicenseType", ExamConstants.FUNCTION_LICENSE_TYPES);
        model.addAttribute("driveLicenseType", ExamConstants.DRIVE_LICENSE_TYPES);
        model.addAttribute("regions", testCenterRepository.findDistinctRegions());
        model.addAttribute("centers", testCenterRepository.findAll());
        model.addAttribute("examTimes", ExamConstants.EXAM_TIMES);

        return "drive-u/admin/examSchedule";
    }
    @GetMapping("/registeredTimes")
    @ResponseBody
    public List<LocalTime> registeredTimes(
            @RequestParam ExamType examType,
            @RequestParam(required = false) String licenseType,
            @RequestParam Long testCenterId,
            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate examDate){

        TestCenter testCenter = testCenterRepository.findById(testCenterId)
                .orElseThrow(() -> new IllegalArgumentException("시험장 없음: " + testCenterId));

        List<ExamSchedule> found;
        if(examType == ExamType.WRITTEN){
            found = examScheduleRepository
                    .findByExamTypeAndTestCenterAndExamDate(examType, testCenter, examDate);
        } else{
            found = examScheduleRepository
                    .findByExamTypeAndLicenseTypeAndTestCenterAndExamDate(
                            examType, licenseType, testCenter, examDate);
        }
        return found.stream().map(ExamSchedule::getExamTime).toList();
    }

    @PostMapping
    public String register(@Valid @ModelAttribute ExamScheduleCreateDTO dto,
                           BindingResult bindingResult,
                           RedirectAttributes rttr){

        if(bindingResult.hasErrors()){
            rttr.addFlashAttribute("errorMessage", "입력값을 확인하세요.");
            return "redirect:/drive-u/admin/examSchedule";
        }
        int count = examScheduleService.register(dto);
        rttr.addFlashAttribute("message", count + "건 등록 완료");

        return "redirect:/drive-u/admin/examSchedule";
    }

    @GetMapping("/monthly")
    @ResponseBody
    public Map<String, Integer> monthly(
            @RequestParam Long testCenterId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        LocalDate base = LocalDate.now();
        int y = (year != null) ? year : base.getYear();
        int m = (month != null) ? month : base.getMonthValue();

        Map<LocalDate, Integer> counts = examScheduleService.getMonthlyCounts(testCenterId, y, m);

        // LocalDate 키 -> "yyyy-MM-dd" 문자열 키로 변환 (JSON 직렬화 + JS에서 다루기 쉽게)
        Map<String, Integer> result = new HashMap<>();
        counts.forEach((date, cnt) -> result.put(date.toString(), cnt));
        return result;
    }
    @GetMapping("/weekly")
    @ResponseBody
    public List<ExamScheduleViewDTO> weekly(@RequestParam Long testCenterId,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart){

        return examScheduleService.getWeeklySchedules(testCenterId, weekStart);
    }
    @GetMapping("/slot")
    @ResponseBody
    public List<ExamScheduleViewDTO> slot(
            @RequestParam Long testCenterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examDate,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime examTime) {

        return examScheduleService.getSlotSchedules(testCenterId, examDate, examTime);
    }
    @PostMapping("/slot")
    @ResponseBody
    public Map<String, Object> registerSlot(@Valid @RequestBody ExamScheduleCreateDTO dto){
        int requested = dto.getExamTimes().size();   // 요청한 시간 개수
        int count = examScheduleService.register(dto); // 실제 등록된 개수
        return Map.of(
                "success", count > 0,
                "count", count,
                "duplicated", requested - count   // 중복으로 건너뛴 개수
        );
    }

    @DeleteMapping("/slot/{scheduleId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteSlot(@PathVariable Long scheduleId) {
        try {
            examScheduleService.delete(scheduleId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
