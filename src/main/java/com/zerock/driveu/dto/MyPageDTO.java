package com.zerock.driveu.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageDTO {
    private Long seq;         // DB 고유 번호 (조회나 수정용)
    private String id;        // 아이디 (로컬 ID 또는 소셜 ID)
    private String name;      // 이름 또는 닉네임
    private String email;     // 이메일
    private String phone;     // 전화번호
    private String address;   // 주소
    private String loginType; // "LOCAL" 또는 "SOCIAL" (화면 분기용)
}