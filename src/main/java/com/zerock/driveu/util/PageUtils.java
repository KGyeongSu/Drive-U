package com.zerock.driveu.util;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public class PageUtils {

    // 범용성 목적으로 generic에 ?
    public static void addPageAttributes (Model model, Page<?> page) {

        // 페이지 버튼
        int nowPage = page.getNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, page.getTotalPages());

        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

    }

}
