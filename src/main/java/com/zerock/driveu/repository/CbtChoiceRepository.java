package com.zerock.driveu.repository;


import com.zerock.driveu.domain.CbtChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CbtChoiceRepository extends JpaRepository<CbtChoice, Long> {

    List<CbtChoice> findByQuestionQuestionIdOrderByChoiceNoAsc(Long questionId);
}