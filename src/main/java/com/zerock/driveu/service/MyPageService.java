package com.zerock.driveu.service;

import com.zerock.driveu.dto.MyPageDTO;
import com.zerock.driveu.dto.AuthUserDTO;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface MyPageService {
    //조회 메소드
    MyPageDTO getMyPageInfo(AuthUserDTO localUser, OAuth2User socialUser);

    void updateAddress(String username, String newAddress);
}
