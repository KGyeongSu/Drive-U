package com.zerock.driveu.repository;

import com.zerock.driveu.domain.CbtCorrectAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CbtCorrectAnswerRepository extends JpaRepository<CbtCorrectAnswer, Long> {

    List<CbtCorrectAnswer> findByQuestionQuestionId(Long questionId);
}