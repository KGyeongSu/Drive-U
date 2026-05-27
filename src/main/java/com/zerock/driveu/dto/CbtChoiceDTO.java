package com.zerock.driveu.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtChoiceDTO {

    private Long choiceId;
    private Integer choiceNo;
    private String choiceText;
    private String choiceImageUrl;
}