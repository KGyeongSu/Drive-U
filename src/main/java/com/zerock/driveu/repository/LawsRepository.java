package com.zerock.driveu.repository;

import com.zerock.driveu.domain.LawsBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LawsRepository extends JpaRepository<LawsBoard, Long> {
}
