package com.zerock.driveu.service;

import com.zerock.driveu.domain.Member;
import com.zerock.driveu.dto.AuthUserDTO;
import com.zerock.driveu.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다." + username));

        String roleName = "ROLE_" + member.getRole().name(); //권한을 담은 변수

        return new AuthUserDTO(
                member.getId(),
                member.getPwd(),
                List.of(new SimpleGrantedAuthority(roleName)),// 변수로 권한 찾게
                member.getEmail(),
                member.getName()
        );
    }


}
