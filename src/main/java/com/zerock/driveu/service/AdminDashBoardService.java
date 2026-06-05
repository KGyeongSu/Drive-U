package com.zerock.driveu.service;

import com.zerock.driveu.domain.Member;
import com.zerock.driveu.domain.SocialMember;
import com.zerock.driveu.domain.UserStatusInfoView;
import com.zerock.driveu.dto.AdminDashboardMetaDTO;
import com.zerock.driveu.dto.UserStatusInfoDTO;
import com.zerock.driveu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class AdminDashBoardService {

    private final MemberRepository memberRepository;
    private final SocialMemberRepository socialRepository;
    private final VideoProgressRepository progressRepository;
    private final ApplicationRepository applicationRepository;
    private final ExamPassRepository passRepository;
    private final UserStatusInfoViewRepository userStatusRepository;

    // 관리자 대시보드 meta data
    public AdminDashboardMetaDTO getMetaData () {

        // 전체회원
        long totalUser = memberRepository.countByRole(Member.Role.USER) + socialRepository.countByRole(SocialMember.Role.USER);

        // 전달 대비 회원 증가 ?
        long increaseUser = memberRepository.count() + socialRepository.count();

        // 교통안전교육 이수자
        long eduPass = progressRepository.countByFinalCompletedYn("Y");

        // 교통안전교육 이수율, 0으로  나누기 방지
        float eduPassRate = (totalUser == 0) ? 0 : ((float) eduPass / totalUser) * 100;

        // 이번 달 시험 건수
        // createdAt : ms 까지 지원
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime endOfMonth = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        long testApply = applicationRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);

        // 합격자 수
        long passCount = passRepository.count();

        return AdminDashboardMetaDTO.builder()
                .totalUser(totalUser)
                .increaseUser(increaseUser)
                .eduPass(eduPass)
                .eduPassRate(eduPassRate)
                .testApply(testApply)
                .passCount(passCount).build();

    }

    // 사용자 진행 현황
    public Page<UserStatusInfoDTO> getUserStatusList (String type, String examType, String status, String keyword, Pageable pageable) {

        // 검색조건 -> 초기 페이지는 전체 -> 로드될 때 초기 페이지가 그대로 됨
        String goType = "전체".equals(type) || "".equals(type) ? null : type;
        String goExam = "전체".equals(examType) || "".equals(examType) ? null : examType;
        String goStatus = "전체".equals(status) || "".equals(status) ? null : status;
        String goKeyword = (keyword == null || keyword.isEmpty()) ? null : keyword;

        // repository 호출
        Page <UserStatusInfoView> viewPage = userStatusRepository.searchMembers(goType, goKeyword, goExam, goStatus, pageable);

        return viewPage.map(v -> UserStatusInfoDTO.builder()
                .name(v.getName())
                .userEmail(v.getEmail())
                .eduStatus(v.getEdu())
                .testStatus(v.getTest())
                .functionStatus(v.getFunction())
                .driveStatus(v.getDrive())
                .recentDate(v.getRecent())
                .build());

    }


}
