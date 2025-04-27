package com.heez.urlib.domain.member.model;


import com.heez.urlib.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "login_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "login_type_id", nullable = false)
  private Long loginTypeId;

  private String type;

  private Long identifier;

}
