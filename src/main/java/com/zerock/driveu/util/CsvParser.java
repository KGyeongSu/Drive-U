package com.zerock.driveu.util;

import com.zerock.driveu.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvParser {

    private static final Logger log = LoggerFactory.getLogger(CsvParser.class);

    public List<CbtQuestion> parse(MultipartFile file, String sourceName, LocalDate effectiveDate) {
        List<CbtQuestion> questionList = new ArrayList<>();
        int qNo = 1; // 문제 번호 카운터 추가

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] columns = line.split(",");

                if (columns.length < 8) {
                    log.warn("Skipping invalid line (insufficient columns, expected 8): {}", line);
                    continue;
                }

                // 문제 생성
                CbtQuestion question = CbtQuestion.builder()
                        .questionNo(qNo)
                        .questionText(columns[0].trim())
                        .explanation(columns[7].trim()) //해설
                        .sourceName(sourceName)
                        .effectiveDate(effectiveDate)
                        .build();

                log.info("Parsed Question #{}: {}", qNo, question.getQuestionText());

                // 보기 생성
                List<CbtChoice> choices = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    if (i < columns.length && !columns[i].trim().isEmpty()) {
                        CbtChoice choice = CbtChoice.builder()
                                .choiceNo(i)
                                .choiceText(columns[i].trim())
                                .question(question)
                                .build();
                        choices.add(choice);
                    }
                }
                question.setChoices(choices);

                // 복수 정답 처리
                String rawAnswer = columns[6].replaceAll("[^0-9,]", "");
                String[] answerArray = rawAnswer.split(",");

                List<CbtCorrectAnswer> answers = new ArrayList<>();
                for (String ans : answerArray) {
                    if (!ans.trim().isEmpty()) {
                        try {
                            CbtCorrectAnswer correctAnswer = CbtCorrectAnswer.builder()
                                    .correctChoiceNo(Integer.parseInt(ans.trim()))
                                    .question(question)
                                    .build();
                            answers.add(correctAnswer);
                        } catch (NumberFormatException e) {
                            log.error("Failed to parse answer: {}", ans);
                        }
                    }
                }
                question.setCorrectAnswerList(answers);

                questionList.add(question);

                // 다음 문제를 위해 번호증가
                qNo++;
            }
        } catch (Exception e) {
            log.error("CSV 파싱 중 치명적 오류 발생", e);
            throw new RuntimeException("CSV 파일 파싱 중 오류가 발생했습니다.", e);
        }

        return questionList;
    }
}