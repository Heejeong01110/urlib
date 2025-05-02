package com.heez.urlib.domain.member.repository;

import com.heez.urlib.domain.auth.model.OAuthType;
import com.heez.urlib.domain.auth.repository.entity.TokenEntity;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.model.vo.Nickname;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

  @Override
  Optional<Member> findById(Long id);

  Optional<Member> findMemberByEmail(Email email);

  @Query("SELECT new com.heez.urlib.domain.auth.repository.entity.TokenEntity(m.email, m.role) FROM Member m WHERE m.id = :id")
  Optional<TokenEntity> findEmailAndRoleById(Long id);

  boolean existsByEmail(Email email);

  Optional<Member> findMemberByNickname(Nickname nickname);

  Optional<Member> findMemberByOauthTypeAndIdentifier(OAuthType oAuthType, String identifier);

  boolean existsByOauthTypeAndIdentifier(OAuthType oAuthType, String identifier);


}
