package com.zerock.driveu.repository;
import com.zerock.driveu.domain.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, Long> {

    List<ChapterProgress> findByUserSeqAndMemberType(
            Long userSeq,
            String memberType
    );

    Optional<ChapterProgress> findByUserSeqAndMemberTypeAndChapter_ChapterId(
            Long userSeq,
            String memberType,
            Long chapterId
    );

    List<ChapterProgress> findByUserSeqAndMemberTypeAndCompletedYn(
            Long userSeq,
            String memberType,
            String completedYn
    );

    List<ChapterProgress> findByUserSeqAndMemberTypeAndQuizPassedYn(
            Long userSeq,
            String memberType,
            String quizPassedYn
    );

    @Query("""
        select count(cp)
        from ChapterProgress cp
        where cp.userSeq = :userSeq
          and cp.memberType = :memberType
          and cp.chapter.course.courseType = :courseType
          and cp.completedYn = 'Y'
          and cp.quizPassedYn = 'Y'
    """)
    long countCompletedChapters(
            Long userSeq,
            String memberType,
            String courseType
    );

    @Query("""
    select count(cp)
    from ChapterProgress cp
    where cp.userSeq = :userSeq
      and cp.memberType = :memberType
      and cp.chapter.course.courseId = :courseId
      and cp.completedYn = 'Y'
      and cp.quizPassedYn = 'Y'
""")
    long countCompletedChaptersByCourse(
            Long userSeq,
            String memberType,
            Long courseId
    );

    Optional<ChapterProgress> findFirstByUserSeqAndMemberTypeAndQuizPassedYnOrderByUpdatedAtDesc(
            Long userSeq,
            String memberType,
            String quizPassedYn
    );

    @Query("""
            select cp
            from ChapterProgress cp
            join cp.chapter vc
            where cp.userSeq = :userSeq
                and cp.memberType = :memberType
                and cp.quizPassedYn = 'N'
                and vc.useYn = 'Y'
                and vc.course.courseType = 'DU'
            order by vc.chapterOrder asc
            """)

    List<ChapterProgress> findIncompleteDuProgress(
            @Param("userSeq") Long userSeq,
            @Param("memberType") String memberType
    );
}