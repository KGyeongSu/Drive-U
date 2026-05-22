package com.zerock.driveu.controller;

import com.zerock.driveu.config.PortoneProperties;
import com.zerock.driveu.constant.ExamConstants;
import com.zerock.driveu.constant.SessionConst;
import com.zerock.driveu.domain.Application;
import com.zerock.driveu.domain.enums.ApplicationStatus;
import com.zerock.driveu.domain.enums.ExamType;
import com.zerock.driveu.dto.ApplySessionDTO;
import com.zerock.driveu.dto.ExamScheduleDTO;
import com.zerock.driveu.domain.ExamSchedule;
import com.zerock.driveu.domain.TestCenter;
import com.zerock.driveu.repository.ApplicationRepository;
import com.zerock.driveu.repository.ExamScheduleRepository;
import com.zerock.driveu.repository.TestCenterRepository;
import com.zerock.driveu.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/drive-u/process")
@RequiredArgsConstructor
public class LicenseProcessController {

    private final TestCenterRepository testCenterRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final PaymentService paymentService;
    private final PortoneProperties portoneProperties;
    private final ApplicationRepository applicationRepository;

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
    public List<ExamScheduleDTO> getSchedules(@RequestParam ExamType examType,
                                              @RequestParam Long testCenterId,
                                              @RequestParam @DateTimeFormat
                                                      (iso = DateTimeFormat.ISO.DATE) LocalDate examDate){

        TestCenter testCenter = testCenterRepository.findById(testCenterId)
                .orElseThrow(() -> new IllegalArgumentException
                        ("시험장 없음: " + testCenterId));

        return examScheduleRepository
                .findByExamTypeAndTestCenterAndExamDate(examType, testCenter, examDate)
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
                .examType(ExamType.WRITTEN)
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

        session.setAttribute(SessionConst.APPLY_DATA, applyData);

        return "redirect:/drive-u/process/wApply4";
    }

    @GetMapping("/wApply4")
    public String wApply4(HttpSession session, Model model) {

        // 1. 세션 검증
        ApplySessionDTO applyData = (ApplySessionDTO)
                session.getAttribute(SessionConst.APPLY_DATA);
        if(applyData == null){
            return "redirect:/drive-u/process/wApply1";
        }

        // 2. 회원 정보 (TODO: Member 인증 완성 후 세션에서 꺼내기)
        Long memberId = 1L;              // 임시
        String memberType = "MEMBER";    // 임시

        // 3. Application INSERT (기존 미결제건 있으면 CANCELLED 처리됨)
        Application application = paymentService.createApplication(applyData, memberId, memberType);

        // 4. Model에 담기
        model.addAttribute("applyData", applyData);
        model.addAttribute("fee", application.getFee());
        model.addAttribute("merchantUid", application.getMerchantUid());  // 결제창 호출용
        // 5. 포트원 V2 결제창 호출에 필요한 키(프론트 노출 OK)
        model.addAttribute("storeId", portoneProperties.getStoreId());
        model.addAttribute("channelKey", portoneProperties.getChannelKey());

        // TODO: Member 인증 완성 후 회원 이름은 세션에서 꺼내기
        model.addAttribute("customerName", "김경수");                  // 임시
        model.addAttribute("customerPhone", applyData.getContactPhone());
        model.addAttribute("customerEmail", applyData.getContactEmail());

        return "drive-u/process/wApply4";
    }

    @GetMapping("/wApply5")
    public String wApply5(HttpSession session, Model model) {

        // 1. 세션 검증
        ApplySessionDTO applyData = (ApplySessionDTO)
                session.getAttribute(SessionConst.APPLY_DATA);
        if (applyData == null) {
            return "redirect:/drive-u/process/wApply1";
        }

        // 2. 회원 정보 (TODO: Member 인증 완성 후 세션에서 꺼내기)
        Long memberId = 1L;

        // 3. 가장 최근 COMPLETED 신청건 조회
        Application application = applicationRepository
                .findFirstByMemberIdAndStatusOrderByCreatedAtDesc(
                        memberId,
                        ApplicationStatus.COMPLETED)
                .orElseThrow(() -> new IllegalStateException("완료된 신청건 없음"));

        // 4. 접수번호 포맷 (DU-yyyyMMdd-applicationId)
        String receiptNo = "DU-"
                + application.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%04d", application.getApplicationId());

        // 5. Model
        model.addAttribute("applyData", applyData);
        model.addAttribute("appData", application);
        model.addAttribute("receiptNo", receiptNo);

        // 6. 세션 클리어 (재진입 방어)
        session.removeAttribute(SessionConst.APPLY_DATA);

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
