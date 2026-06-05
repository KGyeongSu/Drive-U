package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cbt_result")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    @Column(name = "member_type", nullable = false, length = 20)
    private String memberType; // MEMBER / SOCIAL

    @Column(nullable = false)
    @Builder.Default
    private Integer score = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer correctCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer wrongCount = 0;

    @Column(nullable = false, length = 1)
    @Builder.Default
    private String passYn = "N";

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String resultStatus = "IN_PROGRESS";

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    @PrePersist
    public void prePersist() {
        if (this.score == null) {
            this.score = 0;
        }

        if (this.correctCount == null) {
            this.correctCount = 0;
        }

        if (this.wrongCount == null) {
            this.wrongCount = 0;
        }

        if (this.passYn == null) {
            this.passYn = "N";
        }

        if (this.resultStatus == null) {
            this.resultStatus = "IN_PROGRESS";
        }

        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
    }
}