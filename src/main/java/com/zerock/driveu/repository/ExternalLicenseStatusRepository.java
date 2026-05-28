package com.zerock.driveu.repository;

import com.zerock.driveu.domain.ExternalLicenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ExternalLicenseStatusRepository extends JpaRepository<ExternalLicenseStatus, Long> {

    // 처음 해당 유저의 합격 데이터를 seq, type으로 조회하는 메소드
    Optional<ExternalLicenseStatus> findByUserSeqAndUserType(Long userSeq, String userType);

    // userSeq와 licenseType으로 정확하게 찾을 수 있게 메서드 생성
    Optional<ExternalLicenseStatus> findByUserSeqAndLicenseType(Long userSeq, String licenseType);
}