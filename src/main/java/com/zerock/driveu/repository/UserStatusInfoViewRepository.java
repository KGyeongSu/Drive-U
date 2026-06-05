package com.zerock.driveu.repository;

import com.zerock.driveu.domain.UserStatusId;
import com.zerock.driveu.domain.UserStatusInfoView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserStatusInfoViewRepository extends JpaRepository<UserStatusInfoView, UserStatusId> {

    // 검색 기능 -> 동적 쿼리 (필요한 param에 따라 sql문 생성)
    @Query("""
        SELECT u FROM UserStatusInfoView u
             WHERE
                 (:keyword IS NULL OR :keyword = '' OR
                   ((:type IS NULL OR :type = '' OR :type = 'NAME') AND u.name LIKE %:keyword%) OR
                   ((:type IS NULL OR :type = '' OR :type = 'EMAIL') AND u.email LIKE %:keyword%)
                 )
                 AND (:examType IS NULL OR
                     (:examType = 'WRITTEN' AND (:status IS NULL OR u.test = :status)) OR
                     (:examType = 'FUNCTION' AND (:status IS NULL OR u.function = :status)) OR
                     (:examType = 'DRIVE' AND (:status IS NULL OR u.drive = :status))
                 )
                 AND (:examType IS NOT NULL OR :status IS NULL OR
                     (u.edu = :status OR u.test = :status OR u.function = :status OR u.drive = :status)
                 )
    """)
    Page<UserStatusInfoView> searchMembers(
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("examType") String examType,
            @Param("status") String status,
            Pageable pageable
    );


}
