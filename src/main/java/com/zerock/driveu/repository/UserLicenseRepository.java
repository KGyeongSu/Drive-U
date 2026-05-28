package com.zerock.driveu.repository;

import com.zerock.driveu.domain.UserLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserLicenseRepository extends JpaRepository<UserLicense, Long> {
    // 유저 시퀀스와 상태로 유효한 면허 조회
    Optional<UserLicense> findByUserSeqAndStatus(Long userSeq, UserLicense.LicenseStatus status);
}