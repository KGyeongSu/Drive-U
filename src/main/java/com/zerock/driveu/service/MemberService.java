package com.zerock.driveu.service;

import com.zerock.driveu.dto.MemberDTO;
import com.zerock.driveu.dto.SocialUserDTO;

public interface MemberService {

    // 회원가입 규격 정의
    String registerMember(
            MemberDTO memberDTO, SocialUserDTO socialUser);

    // 아이디 중복 확인 메소드
    boolean checkIdDuplicate(String id);
}