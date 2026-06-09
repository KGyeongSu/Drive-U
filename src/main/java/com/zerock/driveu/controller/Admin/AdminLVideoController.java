package com.zerock.driveu.controller.Admin;

import com.zerock.driveu.domain.VideoCourse;
import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.dto.admin.LVideoCreateDTO;
import com.zerock.driveu.dto.admin.LVideoUpdateDTO;
import com.zerock.driveu.repository.VideoCourseRepository;
import com.zerock.driveu.service.admin.AdminLVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/drive-u/admin/video/lVideo")
public class AdminLVideoController {
    private final AdminLVideoService adminLVideoService;
    private final VideoCourseRepository videoCourseRepository;

    //리스트
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "lastest") String sort,
                       Model model) {
        Page<VideoCourse> videoPage = adminLVideoService.getLearningVideos(page, size, sort);

        model.addAttribute("videoPage", videoPage);
        model.addAttribute("videos", videoPage.getContent());
        model.addAttribute("sort", sort);

        return "drive-u/admin/video/lVideoList";
    }

    //등록화면
    @GetMapping("/create")
    public String createForm(Model model) {
        LVideoCreateDTO form = new LVideoCreateDTO();
        form.setUseYn("Y");

        model.addAttribute("form", form);

        return "drive-u/admin/video/lVideo";
    }

    //등록처리
    @PostMapping("/create")
    public String create(@ModelAttribute("form") LVideoCreateDTO form, @AuthenticationPrincipal AuthUserDTO authUserDTO) {
        adminLVideoService.create(form, authUserDTO.getSeq());

        return "redirect:/drive-u/admin/video/lVideo";
    }

    //수정
    @PostMapping("/{courseId}/modify")
    public String update(@PathVariable Long courseId, @ModelAttribute LVideoUpdateDTO form) {
        adminLVideoService.update(courseId, form);

        return "redirect:/drive-u/admin/video/lVideo";
    }

    //삭제
    @PostMapping("/{courseId}/delete")
    public String delete(@PathVariable Long courseId) {
        adminLVideoService.delete(courseId);

        return "redirect:/drive-u/admin/video/lVideo";
    }
}






























