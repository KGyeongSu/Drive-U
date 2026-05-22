package com.zerock.driveu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatBotResponseDTO {

    // 챗봇 요청
    private String question;

    // 챗봇 응답
    private String answer;
    private List<ChatBotAnswerButtonDTO> buttons;

}