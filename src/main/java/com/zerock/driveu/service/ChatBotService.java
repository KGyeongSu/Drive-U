package com.zerock.driveu.service;

import com.zerock.driveu.dto.ChatBotResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatBotService {

    private final ChatBotClient chatClient;
    private final ChatBotParser chatParser;

    @Value("${naver.chatbot.enabled}")
    private boolean chatbotEnabled;

    public ChatBotResponseDTO sendMessage(String question) {

        if (!chatbotEnabled) {

            return ChatBotResponseDTO.builder()
                    .question(question)
                    .answer("챗봇 서비스 준비 중입니다. 다음에 다시 시도해주세요 :)")
                    .buttons(List.of())
                    .build();

        }

        try {

            Map <String, Object> body = chatClient.call(question);

            // null-safe
            if(body == null) {

                return ChatBotResponseDTO.builder()
                        .question(question)
                        .answer("해당 문의는 아직 챗봇 서비스 전입니다. 양해 부탁드립니다 :)")
                        .buttons(List.of())
                        .build();

            }

            // 응답 -> dto 변환
            return ChatBotResponseDTO.builder()
                    .question(question)
                    .answer(chatParser.extractAnswer(body))
                    .buttons(chatParser.extractButtons(body))
                    .build();

        } catch (Exception e) {

            log.error("챗봇 서비스 실패", e);

            // 예외 > fallBack처리
            return ChatBotResponseDTO.builder()
                    .question(question)
                    .answer("지금 챗봇 응답을 가져올 수 없습니다.")
                    .buttons(List.of())
                    .build();

        }

    }

}