package com.zerock.driveu.repository;
import com.zerock.driveu.domain.VideoChapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoChapterRepository extends JpaRepository<VideoChapter, Long> {

    List<VideoChapter> findByCourse_CourseIdOrderByChapterOrderAsc(Long courseId);

    Optional<VideoChapter> findByChapterIdAndCourse_CourseTypeAndCourse_UseYn(
            Long chapterId,
            String courseType,
            String useYn
    );
}