package com.zerock.driveu.controller;

import com.zerock.driveu.dto.LawsRequestDTO;
import com.zerock.driveu.service.LawsService;
import com.zerock.driveu.util.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/drive-u/userInfo/lawsHome")
@RequiredArgsConstructor
@Log4j2
public class LawsController {

    private final LawsService lawsService;

    @GetMapping
    public String lawsHome(@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        var lawsHome = lawsService.getList(pageable);
        PageUtils.addPageAttributes(model, lawsHome);

        model.addAttribute("listUrl", "/drive-u/userInfo/lawsHome/listFragment");
        model.addAttribute("detailUrl", "/drive-u/userInfo/lawsHome/lawsDetail");
        model.addAttribute("dataPage", lawsHome);

        return "drive-u/userInfo/lawsHome";
    }

    @GetMapping("/listFragment")
    public String lawsListFragment(@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        var lawsPage = lawsService.getList(pageable);
        PageUtils.addPageAttributes(model, lawsPage);

        model.addAttribute("listUrl", "/drive-u/userInfo/lawsHome/listFragment");
        model.addAttribute("detailUrl", "/drive-u/userInfo/lawsHome/lawsDetail");
        model.addAttribute("dataPage", lawsPage);

        return "fragments/pagination :: list";
    }

    @GetMapping("/registerForm")
    public String registerForm() {
        return "drive-u/userInfo/lawsDetail";
    }

    @PostMapping("/lawsRegister")
    public String register(LawsRequestDTO lawsRequestDTO, @RequestParam(value = "files", required = false) List<MultipartFile> files, RedirectAttributes redirectAttributes) throws IOException {

        lawsService.registerLaws(lawsRequestDTO, files);
        redirectAttributes.addFlashAttribute("successMsg", "법규 등록이 완료되었습니다.");

        return "redirect:/drive-u/userInfo/lawsHome";
    }

    @GetMapping("/lawsDetail")
    public String lawsDetail(@RequestParam("id") Long id, Model model) {
        model.addAttribute("lawsDetail", lawsService.getOne(id));
        return "drive-u/userInfo/lawsDetail";
    }

    @GetMapping("/modifyForm")
    public String lawsModify(@RequestParam("id") Long id, Model model) {
        model.addAttribute("lawsDetail", lawsService.getOne(id));
        return "drive-u/userInfo/lawsDetail";
    }

    @PostMapping("/lawsModify")
    public String modify(@RequestParam("id") Long id, LawsRequestDTO lawsRequestDTO, @RequestParam(value = "files", required = false) List<MultipartFile> newFiles, @RequestParam(value = "deleteIds", required = false) List<Long> deleteIds, RedirectAttributes redirectAttributes) throws IOException {

        lawsService.updateLaws(id, lawsRequestDTO, newFiles, deleteIds);

        redirectAttributes.addFlashAttribute("successMsg", "법규 수정이 완료되었습니다.");
        redirectAttributes.addAttribute("id", id);

        return "redirect:/drive-u/userInfo/lawsHome/lawsDetail";
    }

    @PostMapping("/lawsDelete")
    public String delete(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {

        lawsService.deleteLaws(id);
        redirectAttributes.addFlashAttribute("successMsg", "법규 삭제가 완료되었습니다.");

        return "redirect:/drive-u/userInfo/lawsHome";
    }
}