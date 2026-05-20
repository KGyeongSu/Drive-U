package com.zerock.driveu.service;

import com.zerock.driveu.dto.SocialUserDTO;

public interface MemberService {

    // 회원가입 규격 정의
    String registerMember(
            String id, String pwd, String name, String email, String phone, SocialUserDTO socialUser);
}