package com.zerock.driveu.repository;

import com.zerock.driveu.domain.VideoCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    Page<VideoCourse> findByCourseTypeOrderByCourseOrderAsc(
            String courseType,
            Pageable pageable
    );

    Page<VideoCourse> findByCourseTypeOrderByCreatedAtDesc(
            String courseType,
            Pageable pageable
    );
    Page<VideoCourse> findByCourseTypeOrderByCreatedAtAsc(
            String courseType,
            Pageable pageable
    );

    Page<VideoCourse> findByCourseTypeAndUseYnOrderByCourseOrderAsc(
            String courseType,
            String useYn,
            Pageable pageable
    );

    Page<VideoCourse> findByCourseTypeAndUseYnOrderByCreatedAtDesc(
            String courseType,
            String useYn,
            Pageable pageable
    );
    Page<VideoCourse> findByCourseTypeAndUseYnOrderByCreatedAtAsc(
            String courseType,
            String useYn,
            Pageable pageable
    );

    @Query("""
                select coalesce(max(vc.courseOrder), 0)
                from VideoCourse vc
                where vc.courseType = :courseType
            """)
    Integer findMaxCourseOrderByCourseType(
            @Param("courseType") String courseType
    );
}