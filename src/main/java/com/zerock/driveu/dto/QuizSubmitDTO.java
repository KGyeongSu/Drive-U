package com.zerock.driveu.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmitDTO {

    private Long chapterId;

    // 로그인 기능 붙기 전까지는 임시 member seq를 같이 보낼 수도 있음
    private Long memberSeq;

    private List<QuizAnswerSubmitDTO> answers;
}