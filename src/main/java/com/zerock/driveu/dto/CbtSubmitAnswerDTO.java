package com.zerock.driveu.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtSubmitAnswerDTO {

    private Long questionId;
    private List<Integer> selectedChoiceNos;
}