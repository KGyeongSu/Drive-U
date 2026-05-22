package com.zerock.driveu.service;

import com.zerock.driveu.domain.Member;
import com.zerock.driveu.domain.SocialMember;
import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.dto.MyPageDTO;
import com.zerock.driveu.repository.MemberRepository;
import com.zerock.driveu.repository.SocialMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository memberRepository;
    private final SocialMemberRepository socialMemberRepository;

    @Override
    public MyPageDTO getMyPageInfo(AuthUserDTO localUser, OAuth2User socialUser) {

        // 인증정보에서 식별할 키(아이디) 추출
        String searchKey = null;

        if (localUser != null && localUser.getUsername() != null) {
            searchKey = localUser.getUsername();
        } else if (socialUser != null) {
            if (socialUser.getAttribute("socialKey") != null) {
                searchKey = (String) socialUser.getAttribute("socialKey");
            } else if (socialUser.getAttribute("id") != null) {
                searchKey = String.valueOf(socialUser.getAttribute("id"));
            }
        }


        if (searchKey == null) {
            return MyPageDTO.builder().build();
        }

        // 1. 소셜 회원 테이블에서 우선적으로 검색 시도
        Optional<SocialMember> findSocial = socialMemberRepository.findBySocialKey(searchKey);
        if (findSocial.isPresent()) {
            SocialMember social = findSocial.get();
            return MyPageDTO.builder()
                    .seq(social.getSeq())
                    .id(social.getSocialKey())
                    .name(social.getName())
                    .email(social.getEmail())
                    .phone(social.getPhone())
                    .address(social.getAddress())
                    .loginType("SOCIAL")
                    .build();
        }

        // 2. 소셜 회원이 아니라면 일반 회원 테이블에서 검색 시도
        Member member = memberRepository.findById(searchKey)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return MyPageDTO.builder()
                .seq(member.getSeq())
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .address(member.getAddress())
                .phone(member.getPhone())
                .loginType("LOCAL")
                .build();
    }

    @Override
    @Transactional
    public void updateAddress(String username, String newAddress) {

        //소셜 먼저 조회
        Optional<SocialMember> findSocial = socialMemberRepository.findBySocialKey(username);
        if (findSocial.isPresent()) {
            SocialMember social = findSocial.get();
            social.changeAddress(newAddress);
            return;
        }

        //소셜에 존재 x 로컬 이동
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.changeAddress(newAddress);
    }
}