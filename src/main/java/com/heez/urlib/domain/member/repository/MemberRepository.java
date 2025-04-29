package com.heez.urlib.domain.member.repository;

import com.heez.urlib.domain.auth.model.OAuthType;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.model.vo.Nickname;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  @Override
  Optional<Member> findById(Long id);

  Optional<Member> findMemberByEmail(Email email);

  boolean existsByEmail(Email email);

  Optional<Member> findMemberByNickname(Nickname nickname);

  Optional<Member> findMemberByOauthTypeAndIdentifier(OAuthType oAuthType, String identifier);
  boolean existsByOauthTypeAndIdentifier(OAuthType oAuthType,String identifier);


}
