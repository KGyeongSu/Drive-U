package com.zerock.driveu.service.admin;

import com.zerock.driveu.domain.ChapterQuiz;
import com.zerock.driveu.domain.ChapterQuizChoice;
import com.zerock.driveu.domain.VideoChapter;
import com.zerock.driveu.dto.admin.AdminQuizDTO;
import com.zerock.driveu.dto.admin.DuReplaceDTO;
import com.zerock.driveu.repository.ChapterQuizChoiceRepository;
import com.zerock.driveu.repository.ChapterQuizRepository;
import com.zerock.driveu.repository.VideoChapterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDuService {
    private final VideoChapterRepository videoChapterRepository;
    private final ChapterQuizRepository chapterQuizRepository;
    private final ChapterQuizChoiceRepository chapterQuizChoiceRepository;

    public List<VideoChapter> getActiveDuChapters() {
        return videoChapterRepository
                .findByCourse_CourseTypeAndCourse_UseYnAndUseYnOrderByChapterOrderAsc(
                        "DU",
                        "Y",
                        "Y"
                );
    }

    public VideoChapter getChapter(Long chapterId) {
        return videoChapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("챕터를 찾을 수 없습니다."));
    }

    public DuReplaceDTO createDefaultReplaceForm(Long chapterId) {
        VideoChapter chapter = getChapter(chapterId);

        DuReplaceDTO dto = new DuReplaceDTO();
        dto.setChapterTitle(chapter.getChapterTitle());
        dto.setVideoUrl(chapter.getVideoUrl());
        dto.setStartSec(chapter.getStartSec());
        dto.setEndSec(chapter.getEndSec());

        return dto;
    }

    @Transactional
    public Long replaceChapter(Long oldChapterId, DuReplaceDTO dto) {
        validateQuizzes(dto.getQuizzes());
        VideoChapter oldChapter = videoChapterRepository.findById(oldChapterId)
                .orElseThrow(() -> new IllegalArgumentException("기존 챕터를 찾을 수 없습니다."));

        oldChapter.setUseYn("N");
        oldChapter.setUpdatedAt(LocalDateTime.now());

        VideoChapter newChapter = VideoChapter.builder()
                .course(oldChapter.getCourse())
                .chapterOrder(oldChapter.getChapterOrder())
                .chapterTitle(dto.getChapterTitle())
                .videoUrl(dto.getVideoUrl())
                .startSec(dto.getStartSec() == null ? 0 : dto.getStartSec())
                .endSec(dto.getEndSec())
                .quizRequiredYn("Y")
                .useYn("Y")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        VideoChapter savedChapter = videoChapterRepository.save(newChapter);
        saveQuizzes(savedChapter, dto.getQuizzes());
        return savedChapter.getChapterId();
    }

    private void validateQuizzes(List<AdminQuizDTO> quizzes) {
        if (quizzes == null || quizzes.size() < 3) {
            throw new IllegalArgumentException("퀴즈는 최소 3개 이상 등록해야 합니다.");
        }

        for (AdminQuizDTO quiz : quizzes) {
            if (quiz.getQuestionText() == null || quiz.getQuestionText().isBlank()) {
                throw new IllegalArgumentException("퀴즈 문제를 입력해야 합니다.");
            }

            if (quiz.getChoices() == null || quiz.getChoices().size() < 4) {
                throw new IllegalArgumentException("보기는 최소 1개 이상 필요합니다.");
            }
            if (quiz.getCorrectChoiceOrder() == null) {
                throw new IllegalArgumentException("정답 보기를 선택해야 합니다.");
            }
        }
    }

    private void saveQuizzes(VideoChapter chapter, List<AdminQuizDTO> quizDTOS) {
        if (quizDTOS == null || quizDTOS.isEmpty()) {
            return;
        }

        int quizOrder = 1;

        for (AdminQuizDTO quizDTO : quizDTOS) {
            if (quizDTO.getQuestionText() == null || quizDTO.getQuestionText().isBlank()) {
                continue;
            }

            ChapterQuiz quiz = ChapterQuiz.builder()
                    .chapter(chapter)
                    .questionText(quizDTO.getQuestionText())
                    .quizType("SINGLE")
                    .quizOrder(quizOrder)
                    .explanation(quizDTO.getExplanation())
                    .passRequiredYn("Y")
                    .useYn("Y")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            ChapterQuiz saveQuiz = chapterQuizRepository.save(quiz);
            saveChoices(saveQuiz, quizDTO);
            quizOrder++;
        }
    }

    private void saveChoices(ChapterQuiz quiz, AdminQuizDTO quizDTO) {
        List<String> choices = quizDTO.getChoices();
        if (choices == null || choices.isEmpty()) {
            throw new IllegalArgumentException("보기는 최소 1개 이상 필요합니다.");
        }

        Integer correctChoiceOrder = quizDTO.getCorrectChoiceOrder();
        if (correctChoiceOrder == null) {
            throw new IllegalArgumentException("정답 보기를 선택해야 합니다.");
        }

        for (int i = 0; i < choices.size(); i++) {
            String choiceText = choices.get(i);

            if (choiceText == null || choiceText.isBlank()) {
                continue;
            }

            int choiceOrder = i + 1;
            ChapterQuizChoice choice = ChapterQuizChoice.builder()
                    .quiz(quiz)
                    .choiceOrder(choiceOrder)
                    .choiceText(choiceText)
                    .correctYn(choiceOrder == correctChoiceOrder ? "Y" : "N")
                    .build();

            chapterQuizChoiceRepository.save(choice);
        }
    }
}
