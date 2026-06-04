package com.zerock.driveu.service;


import com.zerock.driveu.constant.CourseType;
import com.zerock.driveu.domain.ChapterQuiz;
import com.zerock.driveu.domain.ChapterQuizChoice;
import com.zerock.driveu.domain.VideoChapter;
import com.zerock.driveu.domain.VideoCourse;
import com.zerock.driveu.dto.*;
import com.zerock.driveu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.zerock.driveu.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DuService {

    private final VideoCourseRepository videoCourseRepository;
    private final VideoChapterRepository videoChapterRepository;
    private final ChapterQuizRepository chapterQuizRepository;
    private final ChapterQuizChoiceRepository chapterQuizChoiceRepository;
    private final VideoProgressRepository videoProgressRepository;

    private final ChapterQuizSubRepository chapterQuizSubRepository;
    private final ChapterQuizAnswerRepository chapterQuizAnswerRepository;
    private final ChapterProgressRepository chapterProgressRepository;

    //Video course type
    public Optional<VideoCourse> getDuCourse() {
        return videoCourseRepository
                .findFirstByCourseTypeAndUseYnOrderByCourseOrderAsc(
                        CourseType.DU,
                        "Y"
                );
    }

    //du Chapter
    public List<VideoChapter> getDuChapters() {
        return getDuCourse()
                .map(course -> videoChapterRepository
                        .findByCourse_CourseIdAndUseYnOrderByChapterOrderAsc(course.getCourseId(), course.getUseYn()))
                .orElse(Collections.emptyList());
    }


    //Chapter1 상시 표시
    public Optional<VideoChapter> getFirstDuChapter() {
        return getDuChapters()
                .stream()
                .findFirst();
    }

    public Optional<VideoChapter> getDuChapter(Long chapterId) {
        return videoChapterRepository
                .findByChapterIdAndCourse_CourseTypeAndCourse_UseYn(
                        chapterId,
                        CourseType.DU,
                        "Y"
                );
    }

    public Optional<VideoChapter> getNextChapter(Long chapterId) {
        Optional<VideoChapter> currentOpt = getDuChapter(chapterId);

        if (currentOpt.isEmpty()) {
            return Optional.empty();
        }


        VideoChapter current = currentOpt.get();

        List<VideoChapter> chapters = videoChapterRepository.findByCourse_CourseIdAndUseYnOrderByChapterOrderAsc(current.getCourse().getCourseId(), current.getCourse().getUseYn());

        return chapters.stream()
                .filter(chapter -> chapter.getChapterOrder() > current.getChapterOrder())
                .findFirst();
    }

 /*   public List<ChapterQuiz> getRandomQuizzes(Long chapterId) {
        List<ChapterQuiz> quizzes = chapterQuizRepository
                .findByChapter_ChapterIdAndUseYnOrderByQuizOrderAsc(chapterId, "Y");

        Collections.shuffle(quizzes);

        return quizzes.stream()
                .limit(3)
                .toList();
    }*/

    public List<ChapterQuizDTO> getRandomQuizDTOs(Long chapterId) {
        List<ChapterQuiz> quizzes = chapterQuizRepository
                .findByChapter_ChapterIdAndUseYnOrderByQuizOrderAsc(chapterId, "Y");

        Collections.shuffle(quizzes);

        return quizzes.stream()
                .limit(3)
                .map(quiz -> {
                    List<ChapterQuizChoice> choices = chapterQuizChoiceRepository
                            .findByQuiz_QuizIdOrderByChoiceOrderAsc(quiz.getQuizId());

                    Collections.shuffle(choices);

                    List<QuizChoiceDTO> choiceDTOs = choices.stream()
                            .map(choice -> QuizChoiceDTO.builder()
                                    .choiceId(choice.getChoiceId())
                                    .choiceText(choice.getChoiceText())
                                    .correctYn(choice.getCorrectYn())
                                    .build())
                            .toList();

                    return ChapterQuizDTO.builder()
                            .quizId(quiz.getQuizId())
                            .questionText(quiz.getQuestionText())
                            .explanation(quiz.getExplanation())
                            .choices(choiceDTOs)
                            .build();
                })
                .toList();
    }


    // 퀴즈
    @Transactional
    public QuizSubmitResultDTO submitQuiz(QuizSubmitDTO dto,
                                          Long userSeq,
                                          String memberType) {

        VideoChapter chapter = videoChapterRepository.findById(dto.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("챕터 정보가 없습니다."));

        List<QuizAnswerSubmitDTO> answers = dto.getAnswers();

        if (answers == null || answers.isEmpty()) {
            return QuizSubmitResultDTO.builder()
                    .passed(false)
                    .totalCount(0)
                    .correctCount(0)
                    .wrongCount(0)
                    .message("제출된 답안이 없습니다.")
                    .build();
        }

        int totalCount = answers.size();
        int correctCount = 0;

        ChapterQuizSubmission submission = ChapterQuizSubmission.builder()
                .userSeq(userSeq)
                .memberType(memberType)
                .chapter(chapter)
                .totalCount(totalCount)
                .correctCount(0)
                .wrongCount(0)
                .score(0)
                .passYn("N")
                .build();

        ChapterQuizSubmission savedSubmission = chapterQuizSubRepository.save(submission);

        for (QuizAnswerSubmitDTO answerDTO : answers) {
            ChapterQuiz quiz = chapterQuizRepository.findById(answerDTO.getQuizId())
                    .orElseThrow(() -> new IllegalArgumentException("퀴즈 정보가 없습니다."));

            ChapterQuizChoice selectedChoice = chapterQuizChoiceRepository.findById(answerDTO.getChoiceId())
                    .orElseThrow(() -> new IllegalArgumentException("선택한 보기가 없습니다."));

            boolean correct = "Y".equals(selectedChoice.getCorrectYn());

            if (correct) {
                correctCount++;
            }

            ChapterQuizAnswer answer = ChapterQuizAnswer.builder()
                    .quizSubmission(savedSubmission)
                    .quiz(quiz)
                    .selectedChoice(selectedChoice)
                    .correctYn(correct ? "Y" : "N")
                    .build();

            chapterQuizAnswerRepository.save(answer);
        }

        int wrongCount = totalCount - correctCount;
        boolean passed = wrongCount == 0;
        int score = (int) Math.round((correctCount * 100.0) / totalCount);

        savedSubmission.setCorrectCount(correctCount);
        savedSubmission.setWrongCount(wrongCount);
        savedSubmission.setScore(score);
        savedSubmission.setPassYn(passed ? "Y" : "N");

        updateChapterProgress(userSeq, memberType, chapter, passed);
        if (passed) {
            updateVideoProgressFinal(userSeq, memberType, chapter.getCourse());
        }

        return QuizSubmitResultDTO.builder()
                .passed(passed)
                .totalCount(totalCount)
                .correctCount(correctCount)
                .wrongCount(wrongCount)
                .message(passed
                        ? "정답입니다. 다음 챕터로 이동할 수 있습니다."
                        : "오답입니다. 이 챕터를 처음부터 다시 시청해야 합니다.")
                .build();
    }

    private void updateChapterProgress(Long userSeq,
                                       String memberType, VideoChapter chapter, boolean passed) {
        ChapterProgress progress = chapterProgressRepository
                .findByUserSeqAndMemberTypeAndChapter_ChapterId(userSeq, memberType, chapter.getChapterId())
                .orElseGet(() -> ChapterProgress.builder()
                        .userSeq(userSeq)
                        .memberType(memberType)
                        .chapter(chapter)
                        .watchedSec(0)
                        .maxWatchedSec(0)
                        .completedYn("N")
                        .quizPassedYn("N")
                        .build());

        if (passed) {
            progress.setCompletedYn("Y");
            progress.setQuizPassedYn("Y");
            progress.setCompletedAt(LocalDateTime.now());
            updateVideoProgressFinal(userSeq, memberType, chapter.getCourse());
        } else {
            progress.setCompletedYn("N");
            progress.setQuizPassedYn("N");
            progress.setWatchedSec(0);
            progress.setMaxWatchedSec(0);
            progress.setCompletedAt(null);
        }

        chapterProgressRepository.save(progress);
    }

    public List<ChapterMenuDTO> getChapterMenus(Long userSeq, String memberType) {
        List<VideoChapter> chapters = getDuChapters();

        List<ChapterProgress> completedProgressList =
                chapterProgressRepository.findByUserSeqAndMemberTypeAndCompletedYn(
                        userSeq,
                        memberType,
                        "Y"
                );

        Set<Long> completedChapterIds = completedProgressList.stream()
                .map(progress -> progress.getChapter().getChapterId())
                .collect(Collectors.toSet());

        int maxAccessibleOrder = 1;

        for (VideoChapter chapter : chapters) {
            if (completedChapterIds.contains(chapter.getChapterId())) {
                maxAccessibleOrder = Math.max(
                        maxAccessibleOrder,
                        chapter.getChapterOrder() + 1
                );
            }
        }

        int finalMaxAccessibleOrder = maxAccessibleOrder;

        return chapters.stream()
                .map(chapter -> ChapterMenuDTO.builder()
                        .chapterId(chapter.getChapterId())
                        .chapterOrder(chapter.getChapterOrder())
                        .chapterTitle(chapter.getChapterTitle())
                        .completed(completedChapterIds.contains(chapter.getChapterId()))
                        .accessible(chapter.getChapterOrder() <= finalMaxAccessibleOrder)
                        .build())
                .toList();
    }

    //url 접근 제한
    public boolean canAccessChapter(Long userSeq, String memberType, Long chapterId) {
        return getChapterMenus(userSeq, memberType).stream()
                .anyMatch(menu ->
                        menu.getChapterId().equals(chapterId)
                                && menu.isAccessible()
                );
    }


    private void updateVideoProgressFinal(
            Long userSeq,
            String memberType,
            VideoCourse course
    ) {
        long totalChapterCount = videoChapterRepository.countByCourseTypeAndUseYn(
                course.getCourseType(),
                "Y",
                "Y"
        );

        long completedChapterCount = chapterProgressRepository.countCompletedChaptersByCourse(
                userSeq,
                memberType,
                course.getCourseId()
        );

        boolean finalCompleted = totalChapterCount > 0
                && completedChapterCount >= totalChapterCount;

        System.out.println("===== VIDEO PROGRESS FINAL CHECK =====");
        System.out.println("userSeq = " + userSeq);
        System.out.println("memberType = " + memberType);
        System.out.println("courseId = " + course.getCourseId());
        System.out.println("courseType = " + course.getCourseType());
        System.out.println("totalChapterCount = " + totalChapterCount);
        System.out.println("completedChapterCount = " + completedChapterCount);
        System.out.println("finalCompleted = " + finalCompleted);


        if (!finalCompleted) {
            return;
        }

        VideoProgress videoProgress = videoProgressRepository
                .findByUserSeqAndMemberTypeAndCourse_CourseType(
                        userSeq,
                        memberType,
                        course.getCourseType()
                )
                .orElseGet(() -> VideoProgress.builder()
                        .userSeq(userSeq)
                        .memberType(memberType)
                        .course(course)
                        .finalCompletedYn("N")
                        .build());

        videoProgress.setFinalCompletedYn("Y");
        videoProgress.setCompletedAt(LocalDateTime.now());

        videoProgressRepository.save(videoProgress);
    }
}