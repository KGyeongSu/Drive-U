package com.zerock.driveu.repository;


import com.zerock.driveu.domain.LicenseApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LicenseApplicationRepository extends JpaRepository<LicenseApplication, Long> {

    // 마이페이지에 최신 신청한 면허 신청 내역을 1건 가져오는 메소드
    Optional<LicenseApplication> findFirstByUserSeqAndUserTypeOrderByCreatedAtDesc(Long userSeq, String userType);

    // "특정 유저"가 "특정 타입의 면허"를 이미 신청했는지 DB에서 개수를 세거나 존재 여부를 확인합니다.
    boolean existsByUserSeqAndType(Long userSeq, LicenseApplication.ApplicationType type);
}