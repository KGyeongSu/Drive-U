package com.zerock.driveu.repository;
import com.zerock.driveu.domain.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, Long> {

    Optional<ChapterProgress> findByMember_SeqAndChapter_ChapterId(
            Long seq,
            Long chapterId
    );
}