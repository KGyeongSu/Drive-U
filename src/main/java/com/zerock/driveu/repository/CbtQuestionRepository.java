package com.zerock.driveu.repository;
import com.zerock.driveu.domain.CbtQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    @Modifying //육상우 추가 (문제 비활성화)
    @Query("UPDATE CbtQuestion c SET c.activeYn = 'N' " +
            "WHERE c.sourceName = :sourceName AND c.effectiveDate = :effectiveDate")
    void deactivateBySourceAndDate(@Param("sourceName") String sourceName, @Param("effectiveDate") LocalDate effectiveDate);

    @Modifying //CSV 업로드 시 중복 방지를 위한 삭제
    @Query("DELETE FROM CbtQuestion c WHERE c.sourceName = :sourceName AND c.effectiveDate = :effectiveDate")
    void deleteBySourceNameAndEffectiveDate(@Param("sourceName") String sourceName, @Param("effectiveDate") LocalDate effectiveDate);

    // 큰 question_no를 가져오는 메소드 -> 데이터 꼬임 방지
    @Query("SELECT MAX(c.questionNo) FROM CbtQuestion c")
    Integer findMaxQuestionNo();
}
