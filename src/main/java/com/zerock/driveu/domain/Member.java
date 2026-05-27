package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = true)
    private String address;

    public enum Role {
        USER, ADMIN
    }

    //주소 수정용 메소드 myPage(육상우가 만듦)
    public void changeAddress(String address) {
        this.address = address;
    }
}
