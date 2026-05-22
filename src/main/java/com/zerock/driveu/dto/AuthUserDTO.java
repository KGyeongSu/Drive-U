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

    // 로컬 로그인용
    public AuthUserDTO(String username, String password, Collection<? extends GrantedAuthority> authorities,
                       String email, String name,String phone) {
        super(username, password, authorities);
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    //소셜 로그인용
    public AuthUserDTO(String username, String password, Collection<? extends GrantedAuthority> authorities,
                       String email, String name, Map<String, Object> attr) {
        super(username, password, authorities);
        this.email = email;
        this.name = name;
        this.attr = attr;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return this.attr;
    }

    public String getRealName() {
        return this.name;
    }
}