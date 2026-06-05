package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.time.LocalDateTime;

@Entity
// 데이터 변경 불가
@Immutable
@Subselect("""
    SELECT
        m.seq AS user_seq,
        m.memberType AS member_type,
        m.name AS name,
        m.email AS email,
        -- 교육상태가 Y이면 이수, 아니면 이수예정
        CASE WHEN vp.final_completed_yn = 'Y' THEN '이수' ELSE '이수예정' END AS edu,
        -- 필기상태 : 합격 -> 접수 내역 -> 대기 순으로
        CASE WHEN ep_w.user_seq IS NOT NULL THEN '합격'
             WHEN ap_w.user_seq IS NOT NULL THEN '응시예정'
             ELSE '대기' END AS test,
        -- 기능 상태
        CASE WHEN ep_f.user_seq IS NOT NULL THEN '합격'
             WHEN ap_f.user_seq IS NOT NULL THEN '응시예정'
             ELSE '대기' END AS function,
        -- 주행 상태
        CASE WHEN ep_d.user_seq IS NOT NULL THEN '합격'
             WHEN ap_d.user_seq IS NOT NULL THEN '응시예정'
             ELSE '대기' END AS drive,

        -- 정렬
        GREATEST(
                COALESCE(CASE WHEN vp.final_completed_yn = 'Y' THEN vp.completed_at ELSE NULL END, '1900-01-01'),
                COALESCE(ap_w.created_at, '1900-01-01'),
                COALESCE(ep_w.created_at, '1900-01-01'),
                COALESCE(ap_f.created_at, '1900-01-01'),
                COALESCE(ep_f.created_at, '1900-01-01'),
                COALESCE(ap_d.created_at, '1900-01-01'),
                COALESCE(ep_d.created_at, '1900-01-01')
        ) AS recent

FROM (
        SELECT seq, 'MEMBER' AS memberType, name, email FROM member WHERE role = 'USER'
        UNION ALL
        SELECT seq, 'SOCIAL' AS memberType, name, email FROM social_member WHERE role = 'USER'
) m
    LEFT JOIN video_progress vp ON m.seq = vp.user_seq AND m.memberType = vp.member_type
    LEFT JOIN application ap_w ON m.seq = ap_w.user_seq AND m.memberType = ap_w.member_type AND ap_w.exam_type = 'WRITTEN'
    LEFT JOIN exam_pass ep_w ON m.seq = ep_w.user_seq AND m.memberType = ep_w.member_type AND ep_w.exam_type = 'WRITTEN'
    LEFT JOIN application ap_f ON m.seq = ap_f.user_seq AND m.memberType = ap_f.member_type AND ap_f.exam_type = 'FUNCTION'
    LEFT JOIN exam_pass ep_f ON m.seq = ep_f.user_seq AND m.memberType = ep_f.member_type AND ep_f.exam_type = 'FUNCTION'
    LEFT JOIN application ap_d ON m.seq = ap_d.user_seq AND m.memberType = ap_d.member_type AND ap_d.exam_type = 'DRIVE'
    LEFT JOIN exam_pass ep_d ON m.seq = ep_d.user_seq AND m.memberType = ep_d.member_type AND ep_d.exam_type = 'DRIVE'
    ORDER BY recent DESC
""")
@Synchronize({"member", "social_member", "video_progress", "application", "exam_pass"})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserStatusInfoView {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "userSeq", column = @Column(name = "user_seq")),
            @AttributeOverride(name = "memberType", column = @Column(name = "member_type"))
    })
    private UserStatusId id;

    private String name;
    private String email;
    private String edu;
    private String test;
    private String function;
    private String drive;
    private LocalDateTime recent;

}