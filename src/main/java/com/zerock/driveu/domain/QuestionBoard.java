package com.zerock.driveu.domain;

import com.zerock.driveu.constant.QuestionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "question")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QuestionBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", length = 3000, nullable = false)
    private String content;

    @Column(name = "answer", length = 3000)
    private String answer;

    // constant 활용 > 답변예정, 답변완료
    @Enumerated(EnumType.STRING)
    private QuestionStatus status;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private LocalDateTime answerDate;

    // 작성자 이름
    @Column(name = "writerName", nullable = false)
    private String writerName;
    // 작성자 식별자
    @Column(name = "writerEmail", nullable = false)
    private String writerEmail;

    // PrePersist > 미리 저장해 놓는다
    @PrePersist
    public void prePersist() {

        this.status = QuestionStatus.PENDING;
        this.regDate = LocalDateTime.now();

    }

    // 질문 modify
    public void updateContent (String title, String content) {

        this.title = title;
        this.content = content;
        this.modDate = LocalDateTime.now();

    }

    // 답변 update
    public void updateAnswer (String answer) {

        if (answer == null || answer.isBlank()) {

            throw new IllegalArgumentException("답변 내용을 입력해 주세요.");

        }

        this.answer = answer;
        this.status = QuestionStatus.COMPLETED;
        this.answerDate = LocalDateTime.now();

    }

}
