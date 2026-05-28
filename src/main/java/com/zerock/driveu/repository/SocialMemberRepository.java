package com.zerock.driveu.repository;

import com.zerock.driveu.domain.SocialMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialMemberRepository extends JpaRepository<SocialMember, Long> {

    //이미 가입된 회원인지 확인하기 위해 socialKey로 조회
    Optional<SocialMember> findBySocialKey(String socialKey);

    // 작성자 이메일을 통해 찾기 위함
    Optional<SocialMember> findByEmail(String email);
}