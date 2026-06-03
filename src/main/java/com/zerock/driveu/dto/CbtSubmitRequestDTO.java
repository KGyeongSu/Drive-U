package com.zerock.driveu.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtSubmitRequestDTO {

    private List<CbtSubmitAnswerDTO> answers;
}