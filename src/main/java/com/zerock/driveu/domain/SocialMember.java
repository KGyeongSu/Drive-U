package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocialMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, unique = true) //kakao_12345,
    private String socialKey;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    public enum Role {
        USER, ADMIN
    }
}