package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;
@Table(
        name = "social_member")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SocialMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "social_key", nullable = false, unique = true)
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
