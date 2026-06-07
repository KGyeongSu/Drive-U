package com.zerock.driveu.service;

import com.zerock.driveu.dto.AdminDashboardMetaDTO;
import com.zerock.driveu.dto.UserStatusInfoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AdminDashBoardServiceTest {

    @Autowired
    private AdminDashBoardService adminService;

    @Test
    public void getMataDataTest() {

        AdminDashboardMetaDTO metaDTO = adminService.getMetaData();

        System.out.println("전체 회원 : " + metaDTO.getTotalUser());
        System.out.println("이수자 : " + metaDTO.getEduPass());
        System.out.println("이수율 : " + metaDTO.getEduPassRate());
        System.out.println("이번 달 시험 건수 : " + metaDTO.getTestApply());
        System.out.println("합격자 : " + metaDTO.getPassCount());

        assertThat(metaDTO.getTotalUser()).isGreaterThanOrEqualTo(0);

    }

    @Test
    public void getUserStatusListTest() {

        // 테스트용 준비
        Pageable pageable = PageRequest.of(0, 5);
        String type = "전체";
        String keyword = "";
        String examType = "DRIVE";
        String status = "전체";

        //서비스 호출
        Page< UserStatusInfoDTO> result = adminService.getUserStatusList(type, examType, status, keyword, pageable);

        System.out.println("조회된 총 페이지 수 : " + result.getTotalPages());
        System.out.println("조회된 데이터 수 : " + result.getContent().size());

        if (!result.getContent().isEmpty()) {

            UserStatusInfoDTO dto = result.getContent().get(0);
            System.out.println("이름 : " + dto.getName());
            System.out.println("이메일 : " + dto.getUserEmail());

        }

        assertThat(result).isNotNull();

    }

}