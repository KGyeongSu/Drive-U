package com.zerock.driveu.repository;

import com.zerock.driveu.domain.QuestionBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository <QuestionBoard, Long> {



}
