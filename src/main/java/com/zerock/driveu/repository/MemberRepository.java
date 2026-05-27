package com.zerock.driveu.repository;

import com.zerock.driveu.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findById(String id);

    boolean existsById(String id); //MemberRepository에 아이디 존재여부 확인 육상우가 만든 메소드

    // 작성자 이메일을 통해 찾기 위함
    Optional<Member> findByEmail(String email);
}
