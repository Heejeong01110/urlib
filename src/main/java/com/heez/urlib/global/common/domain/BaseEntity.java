package com.heez.urlib.global.common.domain;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @CreatedDate
  @Column(name = "createdAt", updatable = false)
  protected LocalDateTime createdAt;

  @LastModifiedDate
  @Column
  private LocalDateTime updatedAt;

}
