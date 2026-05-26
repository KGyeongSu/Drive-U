package com.zerock.driveu.service;

import com.zerock.driveu.dto.SocialUserDTO;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;


public interface SocialUserOAuth2Service {

    //시큐리티 기본 필터용 ( 변수명은 같아도 파라메타가 틀리기에 호출시 파라메타로 찾아감)
    SocialUserDTO getSocialUserdto(OAuth2UserRequest userRequest, OAuth2User oAuth2User);

    //SuccessHandler에서 사용 용도
    SocialUserDTO getSocialUserdto(String provider, OAuth2User oAuth2User);
}
