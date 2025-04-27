package com.heez.urlib.domain.member.repository;

import com.heez.urlib.domain.member.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

  @Override
  Optional<Member> findById(Long id);

  @Query("select m from Member m where m.email= ?1")
  Optional<Member> findMemberByEmail(String email);

  @Query("select m from Member m where m.nickname= ?1")
  Optional<Member> findMemberByNickname(String nickname);


}
