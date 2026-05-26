package com.zerock.driveu.repository;

import com.zerock.driveu.domain.ChapterQuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterQuizAnswerRepository extends JpaRepository<ChapterQuizAnswer, Long> {

    List<ChapterQuizAnswer> findByQuizSubmission_QuizSubmissionId(
            Long quizSubmissionId
    );
}