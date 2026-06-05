package com.zerock.driveu.dto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtQuestionResultDTO {

    private Long questionId;
    private Integer questionNo;
    private String questionText;

    private List<Integer> selectedChoiceNos;
    private List<Integer> correctChoiceNos;

    private String correctYn;
    private String explanation;
}