package com.zerock.driveu.repository;

import com.zerock.driveu.domain.ChapterQuizChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterQuizChoiceRepository extends JpaRepository<ChapterQuizChoice, Long> {

    List<ChapterQuizChoice> findByQuiz_QuizIdOrderByChoiceOrderAsc(Long quizId);
}