package com.zerock.driveu.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtQuestionDTO {

    private Long questionId;
    private Integer questionNo;
    private String questionText;
    private String questionType;
    private String imageUrl;
    private String difficulty;
    private String category;
    private String explanation;

    private List<CbtChoiceDTO> choices;
}