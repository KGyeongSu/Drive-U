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
public class QuestionListDTO {

    private Long id;
    private String title;
    private LocalDateTime regDate;
    private QuestionStatus status;
    private String writerName;

}
