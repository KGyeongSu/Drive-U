package com.zerock.driveu.repository;

import com.zerock.driveu.domain.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {

    Optional<VideoProgress> findByUserSeqAndMemberTypeAndCourse_CourseId(
            Long userSeq,
            String memberType,
            Long courseId
    );

    boolean existsByUserSeqAndMemberTypeAndCourse_CourseTypeAndFinalCompletedYn(
            Long userSeq,
            String memberType,
            String courseType,
            String finalCompletedYn
    );


}