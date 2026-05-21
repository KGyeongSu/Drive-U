package com.zerock.driveu.repository;

import com.zerock.driveu.domain.VideoCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoCourseRepository extends JpaRepository<VideoCourse, Long> {

    Optional<VideoCourse> findFirstByCourseTypeAndUseYnOrderByCourseOrderAsc(
            String courseType,
            String useYn
    );

    List<VideoCourse> findByCourseTypeAndUseYnOrderByCourseOrderAsc(
            String courseType,
            String useYn
    );

    Optional<VideoCourse> findByCourseIdAndCourseTypeAndUseYn(
            Long courseId,
            String courseType,
            String useYn
    );
}