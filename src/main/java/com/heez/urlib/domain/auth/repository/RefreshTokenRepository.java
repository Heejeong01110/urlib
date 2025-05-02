package com.heez.urlib.domain.auth.repository;

import com.heez.urlib.domain.auth.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
