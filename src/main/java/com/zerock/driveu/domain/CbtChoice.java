package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "cbt_choice",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cbt_choice_question_no",
                        columnNames = {"question_id", "choice_no"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long choiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private CbtQuestion question;

    @Column(nullable = false)
    private Integer choiceNo;

    @Column(nullable = false, length = 1000)
    private String choiceText;

    @Column(length = 500)
    private String choiceImageUrl;
}