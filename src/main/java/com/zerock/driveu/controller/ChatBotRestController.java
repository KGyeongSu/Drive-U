package com.zerock.driveu.controller;

import com.zerock.driveu.dto.ChatBotResponseDTO;
import com.zerock.driveu.service.ChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatBotRestController {

    private final ChatBotService chatService;

    @PostMapping
    public ChatBotResponseDTO chat (@RequestBody ChatBotResponseDTO requestDTO) {

        // 질문과 답변 단일 DTO 에 있어서 간단하게
        return chatService.sendMessage(

                requestDTO.getQuestion()

        );

    }

}
