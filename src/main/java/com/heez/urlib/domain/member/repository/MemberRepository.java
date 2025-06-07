package com.heez.urlib.domain.member.repository;

import com.heez.urlib.domain.auth.model.AuthType;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.model.vo.Nickname;
import com.heez.urlib.domain.member.service.dto.TokenProjection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findMemberByEmail(Email email);

  @Query("SELECT m.email AS email, m.role AS role " +
      "FROM Member m " +
      "WHERE m.memberId = :id")
  Optional<TokenProjection> findEmailAndRoleById(@Param("id") Long id);

  Optional<Member> findMemberByOauthTypeAndIdentifier(AuthType authType, String identifier);

  boolean existsByEmail(Email email);
  boolean existsByNickname(Nickname nickname);

}
