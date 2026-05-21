package com.zerock.driveu.repository;

import com.zerock.driveu.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
/*
    Optional<Member> findBySocialKey(String socialKey);
    Optional<Member> findById(String id);*/
}
