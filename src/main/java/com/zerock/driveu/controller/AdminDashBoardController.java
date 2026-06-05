package com.zerock.driveu.controller;

import com.zerock.driveu.dto.UserStatusInfoDTO;
import com.zerock.driveu.service.AdminDashBoardService;
import com.zerock.driveu.util.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/drive-u/admin")
@RequiredArgsConstructor
@Log4j2
public class AdminDashBoardController {

    private final AdminDashBoardService adminService;

    @GetMapping
    public String adminDashboard(
            @RequestParam(value = "type", defaultValue = "") String type,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "examType", defaultValue = "") String examType,
            @RequestParam(value = "status", defaultValue = "") String status,
            @PageableDefault(page = 0, size = 4, sort = "recent", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {

        log.info("검색 파라미터 확인: type={}, keyword={}, examType={}, status={}", type, keyword, examType, status);

        // metaData
        model.addAttribute("meta", adminService.getMetaData());

        // 사용자 진행 현황
        Page<UserStatusInfoDTO> userStatusList = adminService.getUserStatusList(type, examType, status, keyword, pageable);

        // 검색조건 유지 목적
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("examType", examType);
        model.addAttribute("status", status);

        PageUtils.addPageAttributes(model, userStatusList);
        // statusList
        model.addAttribute("userStatusList", userStatusList);

        // 주소 날아감 방지
        model.addAttribute("listUrl", "/drive-u/admin/listFragment");

        return "drive-u/admin/dashboard";

    }

    @GetMapping("/listFragment")
    public String adminListFragment(
            @RequestParam(value = "type", defaultValue = "") String type,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "examType", defaultValue = "") String examType,
            @RequestParam(value = "status", defaultValue = "") String status,
            @PageableDefault(page = 0, size = 4, sort = "recent", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {

        // 사용자 진행 현황
        Page<UserStatusInfoDTO> userStatusList = adminService.getUserStatusList(type, examType, status, keyword, pageable);

        // 검색조건 유지 목적
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("examType", examType);
        model.addAttribute("status", status);

        // statusList
        model.addAttribute("userStatusList", userStatusList);
        PageUtils.addPageAttributes(model, userStatusList);

        // 주소 날아감 방지
        model.addAttribute("listUrl", "/drive-u/admin/listFragment");

        log.info("비동기로 paging - 페이지 번호 : " + pageable.getPageNumber());

        return "fragments/pagination :: adminList";

    }

}
