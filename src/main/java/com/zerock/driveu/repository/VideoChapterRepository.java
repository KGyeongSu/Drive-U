package com.zerock.driveu.repository;
import com.zerock.driveu.domain.VideoChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VideoChapterRepository extends JpaRepository<VideoChapter, Long> {

    long countByCourse_CourseId(Long courseId);

    List<VideoChapter> findByCourse_CourseIdAndUseYnOrderByChapterOrderAsc(Long courseId, String useYn);

    Optional<VideoChapter> findByChapterIdAndCourse_CourseTypeAndCourse_UseYn(
            Long chapterId,
            String courseType,
            String courseUseYn
    );

    @Query("""
    select count(vc)
    from VideoChapter vc
    where vc.course.courseType = :courseType
      and vc.course.useYn = :courseUseYn
      and vc.useYn = :chapterUseYn
""")
    long countByCourseTypeAndUseYn(
            String courseType,
            String courseUseYn,
            String chapterUseYn
    );

    List<VideoChapter> findByCourse_CourseTypeAndCourse_UseYnAndUseYnOrderByChapterOrderAsc(
            String courseType,
            String courseUseYn,
            String chapterUseYn
    );
}