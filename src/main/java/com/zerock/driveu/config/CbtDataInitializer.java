package com.zerock.driveu.config;

import com.zerock.driveu.domain.CbtChoice;
import com.zerock.driveu.domain.CbtCorrectAnswer;
import com.zerock.driveu.domain.CbtQuestion;
import com.zerock.driveu.repository.CbtChoiceRepository;
import com.zerock.driveu.repository.CbtCorrectAnswerRepository;
import com.zerock.driveu.repository.CbtQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CbtDataInitializer implements CommandLineRunner {

    private final CbtQuestionRepository questionRepository;
    private final CbtChoiceRepository choiceRepository;
    private final CbtCorrectAnswerRepository correctAnswerRepository;

    @Override
    public void run(String... args) {
        if (questionRepository.count() > 0) {
            return;
        }

        saveQuestion1();
        saveQuestion2();
        saveQuestion3();
    }

    private void saveQuestion1() {
        CbtQuestion question = CbtQuestion.builder()
                .questionNo(1)
                .questionText("모바일운전면허증 발급을 위해 필요한 절차로 옳은 것은?")
                .questionType("TEXT")
                .difficulty("NORMAL")
                .category("면허")
                .explanation("도로교통법 제85조의2 및 도로교통법시행규칙 제78조의2에 따라 본인 명의 스마트폰과 본인 인증을 통해 신청해야 한다.")
                .sourceName("한국도로교통공단 학과시험 문제은행")
                .sourceUrl("https://www.safedriving.or.kr")
                .effectiveDate(LocalDate.of(2026, 3, 9))
                .activeYn("Y")
                .build();

        questionRepository.save(question);

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(1)
                .choiceText("모든 앱에서 신청 가능하다.")
                .build());

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(2)
                .choiceText("본인 명의 스마트폰과 본인 인증을 통해 신청해야 한다.")
                .build());

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(3)
                .choiceText("경찰서 교통민원실에서만 신청할 수 있다.")
                .build());

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(4)
                .choiceText("운전면허시험장에서는 발급이 불가능하다.")
                .build());

        correctAnswerRepository.save(CbtCorrectAnswer.builder()
                .question(question)
                .correctChoiceNo(2)
                .build());
    }

    private void saveQuestion2() {
        CbtQuestion question = CbtQuestion.builder()
                .questionNo(2)
                .questionText("도로교통법령상 운전면허증 발급에 대한 설명으로 옳지 않은 것은?")
                .questionType("TEXT")
                .difficulty("NORMAL")
                .category("면허")
                .explanation("운전면허증은 영문운전면허증과 모바일운전면허증으로도 발급받을 수 있다.")
                .sourceName("한국도로교통공단 학과시험 문제은행")
                .sourceUrl("https://www.safedriving.or.kr")
                .effectiveDate(LocalDate.of(2026, 3, 9))
                .activeYn("Y")
                .build();

        questionRepository.save(question);

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(1)
                .choiceText("운전면허시험 합격일로부터 30일 이내에 운전면허증을 발급받아야 한다.")
                .build());

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(2)
                .choiceText("영문운전면허증을 발급받을 수 없다.")
                .build());

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(3)
                .choiceText("모바일운전면허증을 발급받을 수 있다.")
                .build());

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(4)
                .choiceText("운전면허증을 잃어버린 경우에는 재발급 받을 수 있다.")
                .build());

        correctAnswerRepository.save(CbtCorrectAnswer.builder()
                .question(question)
                .correctChoiceNo(2)
                .build());
    }

    private void saveQuestion3() {
        CbtQuestion question = CbtQuestion.builder()
                .questionNo(3)
                .questionText("시·도 경찰청장이 발급한 국제운전면허증의 유효기간은 발급받은 날부터 몇 년인가?")
                .questionType("TEXT")
                .difficulty("NORMAL")
                .category("면허")
                .explanation("국제운전면허증의 유효기간은 발급받은 날부터 1년이다.")
                .sourceName("한국도로교통공단 학과시험 문제은행")
                .sourceUrl("https://www.safedriving.or.kr")
                .effectiveDate(LocalDate.of(2026, 3, 9))
                .activeYn("Y")
                .build();

        questionRepository.save(question);

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(1)
                .choiceText("1년")
                .build());

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(2)
                .choiceText("2년")
                .build());

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(3)
                .choiceText("3년")
                .build());

        choiceRepository.save(CbtChoice.builder()
                .question(question)
                .choiceNo(4)
                .choiceText("4년")
                .build());

        correctAnswerRepository.save(CbtCorrectAnswer.builder()
                .question(question)
                .correctChoiceNo(1)
                .build());
    }
}