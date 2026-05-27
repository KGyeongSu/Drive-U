package com.zerock.driveu.dto;

import com.zerock.driveu.constant.QuestionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO {

    private Long id;
    private String title;
    private String content;
    private String answer;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private LocalDateTime answerDate;

    private QuestionStatus status;
    private String writerName;
    private String writerEmail;

}
