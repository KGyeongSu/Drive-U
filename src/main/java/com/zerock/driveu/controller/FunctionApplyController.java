package com.zerock.driveu.controller;

import com.zerock.driveu.config.PortoneProperties;
import com.zerock.driveu.constant.ExamConstants;
import com.zerock.driveu.constant.SessionConst;
import com.zerock.driveu.domain.Application;
import com.zerock.driveu.domain.ExamSchedule;
import com.zerock.driveu.domain.TestCenter;
import com.zerock.driveu.domain.enums.ApplicationStatus;
import com.zerock.driveu.domain.enums.ExamType;
import com.zerock.driveu.dto.ApplySessionDTO;
import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.dto.ExamScheduleDTO;
import com.zerock.driveu.repository.ApplicationRepository;
import com.zerock.driveu.repository.ExamScheduleRepository;
import com.zerock.driveu.repository.TestCenterRepository;
import com.zerock.driveu.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/drive-u/process")
@RequiredArgsConstructor
public class FunctionApplyController {

    private final TestCenterRepository testCenterRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final ApplicationRepository applicationRepository;
    private final PaymentService paymentService;
    private final PortoneProperties portoneProperties;


    @GetMapping("/fApply1")
    public String fApply1() {
        return "drive-u/process/fApply1";
    }

    @GetMapping("/fApply2")
    public String fApply2(Model model) {
        model.addAttribute("licenseTypes", ExamConstants.FUNCTION_LICENSE_TYPES);
        model.addAttribute("regions", testCenterRepository.findDistinctRegions());
        model.addAttribute("centers", testCenterRepository.findAll());
        model.addAttribute("examTimes", ExamConstants.EXAM_TIMES);

        return "drive-u/process/fApply2";
    }
    @GetMapping("/fApply2/schedules")
    @ResponseBody
    public List<ExamScheduleDTO> getSchedules(@RequestParam ExamType examType,
                                              @RequestParam String licenseType,
                                              @RequestParam Long testCenterId,
                                              @RequestParam @DateTimeFormat
                                                      (iso = DateTimeFormat.ISO.DATE) LocalDate examDate){

        TestCenter testCenter = testCenterRepository.findById(testCenterId)
                .orElseThrow(() -> new IllegalArgumentException
                        ("시험장 없음: " + testCenterId));

        return examScheduleRepository
                .findByExamTypeAndLicenseTypeAndTestCenterAndExamDate(examType, licenseType, testCenter, examDate)
                .stream()
                .map(schedule -> {
                    long count = applicationRepository
                            .countByExamSchedule_ScheduleIdAndStatus(
                                    schedule.getScheduleId(),
                                    ApplicationStatus.COMPLETED);
                    return ExamScheduleDTO.from(schedule, count);
                })
                .toList();
    }
    @PostMapping("/fApply2")
    public String fApply2Submit(@RequestParam String licenseType,
                                @RequestParam(required = false) Long examScheduleId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes){

        // 일정 미선택 방어
        if(examScheduleId == null){
            redirectAttributes.addFlashAttribute("errorMessage", "시험 일정을 선택해주세요.");
            return "redirect:/drive-u/process/fApply2";
        }

        // 1. ExamSchedule 조회 (실제 존재하는지 검증)
        ExamSchedule schedule = examScheduleRepository.findById(examScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정 없음: " + examScheduleId));

        // 2. 시작 1시간 전까지만 신청 가능
        LocalDateTime examStart = LocalDateTime.of(schedule.getExamDate(), schedule.getExamTime());
        if(examStart.isBefore(LocalDateTime.now().plusHours(1))){
            throw new IllegalArgumentException("시험 시작 1시간 전까지만 신청 가능합니다.");
        }

        // 3. 세션 DTO 생성
        ApplySessionDTO applyData = ApplySessionDTO.builder()
                .examType(ExamType.FUNCTION)
                .licenseType(licenseType)
                .testCenterId(schedule.getTestCenter().getTestCenterId())
                .testCenterName(schedule.getTestCenter().getCenterName())
                .region(schedule.getTestCenter().getRegion())
                .examDate(schedule.getExamDate())
                .examTime(schedule.getExamTime().toString())
                .examScheduleId(examScheduleId)
                .build();

        // 4. 세션 저장
        session.setAttribute(SessionConst.APPLY_DATA, applyData);

        // 5. fApply3으로 redirect
        return "redirect:/drive-u/process/fApply3";
    }

    @GetMapping("/fApply3")
    public String fApply3(@AuthenticationPrincipal AuthUserDTO authUser,
                          HttpSession session, Model model) {

        // url로 직접 진입할 경우 대비한 방어
        ApplySessionDTO applyData = (ApplySessionDTO)session
                .getAttribute(SessionConst.APPLY_DATA);
        if(applyData == null){
            return "redirect:/drive-u/process/fApply1";
        }

        // 로그인 회원 정보를 화면에 표시
        Map<String, Object> member = new HashMap<>();
        member.put("name", authUser.getRealName());
        member.put("phone", authUser.getPhone());
        member.put("email", authUser.getEmail());

        model.addAttribute("member", member);
        return "drive-u/process/fApply3";
    }
    @PostMapping("/fApply3")
    public String fApply3Submit(@RequestParam String contactPhone,
                                @RequestParam String contactEmail,
                                HttpSession session){

        // url로 직접 진입할 경우 대비한 방어
        ApplySessionDTO applyData = (ApplySessionDTO)session
                .getAttribute(SessionConst.APPLY_DATA);
        if(applyData == null){
            return "redirect:/drive-u/process/fApply1";
        }

        // sessionDTO에 연락처, 이메일 추가
        applyData.setContactPhone(contactPhone);
        applyData.setContactEmail(contactEmail);

        session.setAttribute(SessionConst.APPLY_DATA, applyData);

        return "redirect:/drive-u/process/fApply4";
    }

    @GetMapping("/fApply4")
    public String fApply4(@AuthenticationPrincipal AuthUserDTO authUser,
                          HttpSession session, Model model) {

        // 1. url로 직접 진입할 경우 대비한 방어
        ApplySessionDTO applyData = (ApplySessionDTO)session
                .getAttribute(SessionConst.APPLY_DATA);
        if(applyData == null){
            return "redirect:/drive-u/process/fApply1";
        }

        // 2. 회원 식별자 꺼내기
        Long memberId = authUser.getSeq();
        String memberType = authUser.getMemberType();

        // 3. Application INSERT (기존 미결제건 있으면 CANCELLED 처리)
        Application application = paymentService.createApplication(applyData, memberId, memberType);

        // 4. model에 담기
        model.addAttribute("applyData", applyData);
        model.addAttribute("fee", application.getFee());
        model.addAttribute("merchantUid", application.getMerchantUid());
        // 5. 포트원 결제창 호출에 필요한 키
        model.addAttribute("storeId", portoneProperties.getStoreId());
        model.addAttribute("channelKey", portoneProperties.getChannelKey());

        // 결제창에 표시될 고객 정보
        model.addAttribute("customerName", authUser.getRealName());
        model.addAttribute("customerPhone", applyData.getContactPhone());
        model.addAttribute("customerEmail", applyData.getContactEmail());

        return "drive-u/process/fApply4";
    }

    @GetMapping("/fApply5")
    public String fApply5(@AuthenticationPrincipal AuthUserDTO authUser,
                          HttpSession session, Model model) {

        // 1. url로 직접 진입할 경우 대비한 방어
        ApplySessionDTO applyData = (ApplySessionDTO)session
                .getAttribute(SessionConst.APPLY_DATA);
        if(applyData == null){
            return "redirect:/drive-u/process/fApply1";
        }

        // 2. 회원 정보
        Long memberId = authUser.getSeq();
        String memberType = authUser.getMemberType();

        // 3. 가장 최근 Completed 조회
        Application application = applicationRepository
                .findFirstByMemberIdAndMemberTypeAndStatusOrderByCreatedAtDesc(
                        memberId,
                        memberType,
                        ApplicationStatus.COMPLETED)
                .orElseThrow(() -> new IllegalStateException("완료된 신청건 없음."));

        // 4. 접수번호 포맷 (DU-yyyyMMdd-applicationId)
        String receiptNo = "DU-"
                + application.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%04d", application.getApplicationId());

        // 5. Model
        model.addAttribute("applyData", applyData);
        model.addAttribute("appData", application);
        model.addAttribute("receiptNo", receiptNo);

        // 6. 세션 클리어 (재진입 방지)
        session.removeAttribute(SessionConst.APPLY_DATA);

        return "drive-u/process/fApply5";
    }
}
