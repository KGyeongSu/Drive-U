package com.zerock.driveu.service;

import com.zerock.driveu.dto.QuestionRequestDTO;
import com.zerock.driveu.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuestionServiceTest {

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @Test
    public void insert () {

        for (int i = 0; i < 21; i++) {

            questionService.register(new QuestionRequestDTO("문의" + i, "내용" + i), "sdads@gmail.com", false, "이은영");

        }

        assertThat(questionRepository.count()).isEqualTo(21);

    }

    @Test
    public void modify () {

        questionService.update(20L, new QuestionRequestDTO("수정이다다다다", "수정된 내용이다다다"), "sdads@gmail.com");

        assertThat(questionService.getOne(20L).getTitle()).isEqualTo("수정이다다다다");

    }

    @Test
    public void answer () {

        questionService.updateAnswer(20L, "관리자 답변입니다.");

        assertThat(questionService.getOne(20L).getAnswer()).isEqualTo("관리자 답변입니다.");

    }

    @Test
    public void delete () {

        questionService.delete(21L, "sdads@gmail.com");

        assertThat(questionRepository.count()).isEqualTo(20);

    }

}