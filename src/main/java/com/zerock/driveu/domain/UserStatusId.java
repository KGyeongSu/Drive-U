package com.zerock.driveu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

// 복합키 선언
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserStatusId {

    @Column(name = "user_seq")
    private Long userSeq;

    @Column(name = "member_type")
    private String memberType;

}
