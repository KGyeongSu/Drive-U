package com.zerock.driveu.service;

import com.zerock.driveu.domain.Member;
import com.zerock.driveu.domain.SocialMember;
import com.zerock.driveu.dto.MemberDTO;
import com.zerock.driveu.dto.SocialUserDTO;
import com.zerock.driveu.repository.MemberRepository;
import com.zerock.driveu.repository.SocialMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final SocialMemberRepository socialMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String registerMember(MemberDTO memberDTO, SocialUserDTO socialUser) {
        String loginUsername = "";

        if (socialUser != null) {
            loginUsername = socialUser.getSocialKey();

            SocialMember socialMember = SocialMember.builder()
                    .socialKey(loginUsername)
                    .name(memberDTO.getName())
                    .email(memberDTO.getEmail())
                    .phone(memberDTO.getPhone())
                    .role(SocialMember.Role.USER)
                    .build();

            socialMemberRepository.save(socialMember);
        } else {

            loginUsername = memberDTO.getId(); // DTO에서 가져옴
            String encodedPassword = passwordEncoder.encode(memberDTO.getPwd());

            Member member = Member.builder()
                    .id(memberDTO.getId())
                    .pwd(encodedPassword)
                    .name(memberDTO.getName())
                    .email(memberDTO.getEmail())
                    .phone(memberDTO.getPhone())
                    .role(Member.Role.USER)
                    .build();

            memberRepository.save(member);
        }

        return loginUsername;
    }

    @Override
    public boolean checkIdDuplicate(String id) {
        // existsById는 해당 ID가 DB에 있으면 true를 반환합니다.
        return memberRepository.existsById(id);
    }
}