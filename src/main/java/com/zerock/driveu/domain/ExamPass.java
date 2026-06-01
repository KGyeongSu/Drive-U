package com.zerock.driveu.domain;

import com.zerock.driveu.domain.enums.ExamType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "exam_pass",
        uniqueConstraints = {
                // 같은 회원이 같은 시험·같은 종별로 합격행을 두 번 남기지 못하게 막음
                @UniqueConstraint(
                        name = "uk_exam_pass",
                        columnNames = {"userSeq", "memberType", "examType", "licenseType"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExamPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examPassId;

    // 회원 식별 (Application / PracticeLicense 와 동일하게 seq + type 조합)
    @Column(nullable = false)
    private Long userSeq;

    @Column(nullable = false, length = 20)
    private String memberType;          // "MEMBER" / "SOCIAL"

    // 어떤 시험에 합격했나 — STRING 저장
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExamType examType;          // WRITTEN / FUNCTION (DRIVE 합격은 정식면허 발급 트리거)

    // 어느 종별로 합격했나 — 종별 일치 게이트(isWrittenPassed / isFunctionPassed)가 읽음
    @Column(nullable = false, length = 30)
    private String licenseType;         // "1종 보통", "2종 보통" 등

    // 합격 여부가 아니라 합격 '시점'
    @Column(nullable = false)
    private LocalDate passedDate;       // 관리자가 합격 입력한 날

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.passedDate == null) {
            this.passedDate = LocalDate.now();
        }
    }
}