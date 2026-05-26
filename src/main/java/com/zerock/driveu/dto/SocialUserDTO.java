package com.zerock.driveu.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class SocialUserDTO {
    private String name;
    private String email;
    private String phone;
    private String socialProvider; // 구글,네이버,카카오 구분자
    private String socialKey;  // 소셜 고유 ID

}
