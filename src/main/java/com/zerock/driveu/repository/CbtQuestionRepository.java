package com.zerock.driveu.repository;
import com.zerock.driveu.domain.CbtQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CbtQuestionRepository extends JpaRepository<CbtQuestion, Long> {

    List<CbtQuestion> findByActiveYn(String activeYn);

    @Query(value = """
            SELECT *
            FROM cbt_question
            WHERE active_yn = 'Y'
            ORDER BY RAND()
            LIMIT :count
            """, nativeQuery = true)
    List<CbtQuestion> findRandomQuestions(int count);
}