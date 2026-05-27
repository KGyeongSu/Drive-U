package com.zerock.driveu.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class AuthUserDTO extends User implements OAuth2User {

    private final String email;
    private final String name;
    private String phone;
    private Map<String, Object> attr; //소셜 로그인 시 카카오/네이버가 주는 원본 데이터 저장소
    // 문의사항 관련 기능에 활용
    private final boolean isSocial;
    private final Long seq;             // seq+type을 식별자로 쓰기위해 추가(경수추가)
    private final String memberType;    // 생성자에도 추가했음.
  
    // 로컬 로그인용
    public AuthUserDTO(String username, String password, Collection<? extends GrantedAuthority> authorities,
                       String email, String name, String phone, boolean isSocial, Long seq, String memberType) {
        super(username, password, authorities);
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.isSocial = isSocial;
        this.seq = seq;
        this.memberType = memberType;
    }

    //소셜 로그인용
    public AuthUserDTO(String username, String password, Collection<? extends GrantedAuthority> authorities,
                       String email, String name, Map<String, Object> attr, boolean isSocial, Long seq, String memberType) {
        super(username, password, authorities);
        this.email = email;
        this.name = name;
        this.attr = attr;
        this.isSocial = isSocial;
        this.seq = seq;
        this.memberType = memberType;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return this.attr;
    }

    public String getRealName() {
        return this.name;
    }
}