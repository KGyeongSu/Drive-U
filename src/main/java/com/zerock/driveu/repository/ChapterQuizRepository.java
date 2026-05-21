package com.zerock.driveu.repository;

import com.zerock.driveu.domain.ChapterQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterQuizRepository extends JpaRepository<ChapterQuiz, Long> {

    List<ChapterQuiz> findByChapter_ChapterIdAndUseYnOrderByQuizOrderAsc(
            Long chapterId,
            String useYn
    );
}