package com.zerock.driveu.service;

import com.zerock.driveu.dto.ChatBotResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatBotServiceTest {

    @Autowired
    public ChatBotService chatService;

    @Test
    void testSendMessage () {

        String userMessage = "운전면허시험";

        // method 호출
        ChatBotResponseDTO response = chatService.sendMessage(userMessage);

        // 결과 확인
        System.out.println("챗봇 응답 : " + response.getAnswer());

        assertNotNull(response);
        assertNotNull(response.getAnswer());
        assertFalse(response.getAnswer().isBlank());


    }

}