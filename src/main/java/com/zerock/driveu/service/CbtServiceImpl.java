package com.zerock.driveu.service;

import com.zerock.driveu.domain.CbtChoice;
import com.zerock.driveu.domain.CbtQuestion;
import com.zerock.driveu.dto.CbtChoiceDTO;
import com.zerock.driveu.dto.CbtQuestionDTO;
import com.zerock.driveu.repository.CbtChoiceRepository;
import com.zerock.driveu.repository.CbtQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CbtServiceImpl implements CbtService {

    private final CbtQuestionRepository cbtQuestionRepository;
    private final CbtChoiceRepository cbtChoiceRepository;

    @Override
    public List<CbtQuestionDTO> getRandomQuestions(int count) {
        List<CbtQuestion> questions = cbtQuestionRepository.findRandomQuestions(count);

        return questions.stream()
                .map(this::entityToDTO)
                .toList();
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