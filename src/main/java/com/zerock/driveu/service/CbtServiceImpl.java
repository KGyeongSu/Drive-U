package com.zerock.driveu.service;

import com.zerock.driveu.domain.CbtChoice;
import com.zerock.driveu.domain.CbtQuestion;
import com.zerock.driveu.dto.CbtChoiceDTO;
import com.zerock.driveu.dto.CbtQuestionDTO;
import com.zerock.driveu.repository.CbtChoiceRepository;
import com.zerock.driveu.repository.CbtQuestionRepository;
import com.zerock.driveu.util.CsvParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CbtServiceImpl implements CbtService {

    private final CbtQuestionRepository cbtQuestionRepository;
    private final CbtChoiceRepository cbtChoiceRepository;
    private final CsvParser csvParser; //육상우 추가 (util 파일 위치)

    @Override
    public List<CbtQuestionDTO> getRandomQuestions(int count) {
        List<CbtQuestion> questions = cbtQuestionRepository.findRandomQuestions(count);

        return questions.stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Transactional
    @Override //육상우 추가 (csv 파일 파싱 로직)
    public void uploadQuestionsFromCsv(MultipartFile file, String sourceName, LocalDate effectiveDate) {

        cbtQuestionRepository.deleteBySourceNameAndEffectiveDate(sourceName, effectiveDate);

        //CSV 파싱 구간
        List<CbtQuestion> newQuestions = csvParser.parse(file, sourceName, effectiveDate);

        Integer maxNo = cbtQuestionRepository.findMaxQuestionNo();
        // 데이터가 없으면 1부터, 있으면 maxNo + 1부터 시작
        int sequence = (maxNo == null) ? 1 : maxNo + 1;

        // 파싱된 리스트에 순차적으로 번호 부여
        for (CbtQuestion question : newQuestions) {
            question.setQuestionNo(sequence++);
        }

        //저장
        cbtQuestionRepository.saveAll(newQuestions);
    }

    private CbtQuestionDTO entityToDTO(CbtQuestion question) {
        List<CbtChoice> choices =
                cbtChoiceRepository.findByQuestionQuestionIdOrderByChoiceNoAsc(question.getQuestionId());

        List<CbtChoiceDTO> choiceDTOList = choices.stream()
                .map(choice -> CbtChoiceDTO.builder()
                        .choiceId(choice.getChoiceId())
                        .choiceNo(choice.getChoiceNo())
                        .choiceText(choice.getChoiceText())
                        .choiceImageUrl(choice.getChoiceImageUrl())
                        .build())
                .toList();

        return CbtQuestionDTO.builder()
                .questionId(question.getQuestionId())
                .questionNo(question.getQuestionNo())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .imageUrl(question.getImageUrl())
                .difficulty(question.getDifficulty())
                .category(question.getCategory())
                .explanation(question.getExplanation())
                .choices(choiceDTOList)
                .build();
    }
}