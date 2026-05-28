package com.zerock.driveu.domain;

import com.zerock.driveu.service.QuestionWriterService;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocialMember implements QuestionWriterService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, unique = true) //kakao_12345,
    private String socialKey;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = true)
    private String address;

    public enum Role {
        USER, ADMIN
    }

    //주소 수정용 메소드 (육상우 만듦)
    public void changeAddress(String address) {
        this.address = address;
    }

    // 문의사항 작성자 가져올 때 필요한 method
    @Override
    public String getWriterName() {

        return this.name;

    }

    @Override
    public String getWriterEmail() {

        return this.email;

    }

}
