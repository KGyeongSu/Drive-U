package com.zerock.driveu.service;

import com.zerock.driveu.dto.SocialUserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Log4j2
public class SocialUserOAuth2ServiceImpl implements SocialUserOAuth2Service {

    @Override
    public SocialUserDTO getSocialUserdto(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        return this.getSocialUserdto(provider, oAuth2User);
    }

    @Override
    public SocialUserDTO getSocialUserdto(String provider, OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        SocialUserDTO.SocialUserDTOBuilder builder = SocialUserDTO.builder().socialProvider(provider);

        if (provider.equals("kakao")) {
            builder.socialKey(provider + "_" + String.valueOf(attributes.get("id")));

            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            if (properties != null) builder.name((String) properties.get("nickname"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) builder.email((String) kakaoAccount.get("email"));

        } else if (provider.equals("naver")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response != null) {
                builder.socialKey(provider + "_" + (String) response.get("id"));
                builder.name((String) response.get("name"));
                builder.email((String) response.get("email"));
                builder.phone((String) response.get("mobile"));
            }

        } else if (provider.equals("google")) {
            builder.socialKey(provider + "_" + (String) attributes.get("sub"));
            builder.name((String) attributes.get("name"));
            builder.email((String) attributes.get("email"));
            if (attributes.get("phone_number") != null) builder.phone((String) attributes.get("phone_number"));
        }

        return builder.build();
    }
}