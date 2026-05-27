package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cbt_question",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cbt_question_source_no",
                        columnNames = {"source_name", "effective_date", "question_no"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false)
    private Integer questionNo;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String questionText;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String questionType = "TEXT";

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String difficulty = "NORMAL";

    @Column(length = 50)
    private String category;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String explanation;

    /**
     * traffic_law 테이블이 확정되면 ManyToOne으로 바꿔도 됨.
     * 지금은 일단 lawId만 들고 가는 게 안전함.
     */
    private Long lawId;

    @Column(nullable = false, length = 100)
    private String sourceName;

    @Column(length = 500)
    private String sourceUrl;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false, length = 1)
    @Builder.Default
    private String activeYn = "Y";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.questionType == null) {
            this.questionType = "TEXT";
        }

        if (this.difficulty == null) {
            this.difficulty = "NORMAL";
        }

        if (this.activeYn == null) {
            this.activeYn = "Y";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}