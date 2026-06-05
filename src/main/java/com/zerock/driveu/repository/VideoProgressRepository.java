package com.zerock.driveu.repository;

import com.zerock.driveu.domain.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {

    boolean existsByUserSeqAndMemberType(
            Long userSeq,
            String memberType);

    Optional<VideoProgress> findByUserSeqAndMemberTypeAndCourse_CourseType(
            Long userSeq,
            String memberType,
            String courseType
    );

    boolean existsByUserSeqAndMemberTypeAndCourse_CourseTypeAndFinalCompletedYn(
            Long userSeq,
            String memberType,
            String courseType,
            String finalCompletedYn
    );

    // 교통안전교육 이수자 세기(admin metaData)
    long countByFinalCompletedYn (String status);


}
