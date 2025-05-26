package com.heez.urlib.domain.auth.service;

import com.heez.urlib.domain.auth.service.dto.ReissueDto;

public interface AuthService {

  public ReissueDto reissue(String oldRefreshToken);

  public void logout(String refreshToken);
}
