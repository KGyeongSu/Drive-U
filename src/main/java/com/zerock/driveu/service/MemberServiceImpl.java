package com.zerock.driveu.service;

import com.zerock.driveu.domain.Member;
import com.zerock.driveu.domain.SocialMember;
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
    public String registerMember(String id, String pwd, String name, String email, String phone, SocialUserDTO socialUser) {
        String loginUsername = "";

        if (socialUser != null) {
            log.info("▶ [ServiceImpl] 소셜 회원가입 진행");

            loginUsername = socialUser.getSocialKey();

            SocialMember socialMember = SocialMember.builder()
                    .socialKey(loginUsername) // 이제 정확히 맞춰짐
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .role(SocialMember.Role.USER)
                    .build();

            socialMemberRepository.save(socialMember);
        } else {
            loginUsername = id;

            String encodedPassword = passwordEncoder.encode(pwd);

            Member member = Member.builder()
                    .id(id)
                    .pwd(encodedPassword)
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .role(Member.Role.USER)
                    .build();

            memberRepository.save(member);
        }

        return loginUsername;
    }
}