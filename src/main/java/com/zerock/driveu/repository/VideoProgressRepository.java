package com.zerock.driveu.repository;

import com.zerock.driveu.domain.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {

    boolean existsByUserSeqAndMemberType(Long userSeq, String memberType);
}
