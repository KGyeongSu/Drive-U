package com.zerock.driveu.repository;


import com.zerock.driveu.domain.ChapterQuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterQuizSubRepository extends JpaRepository<ChapterQuizSubmission, Long> {

    List<ChapterQuizSubmission> findByUserSeqAndMemberTypeAndChapter_ChapterIdOrderBySubmittedAtDesc(
            Long userSeq,
            String memberType,
            Long chapterId
    );
}