package com.heez.urlib.domain.auth.strategy;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

import com.heez.urlib.domain.auth.model.OAuthType;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class OAuth2StrategyComposite {

  private final Map<OAuthType, OAuth2Strategy> oauth2ProviderMap;

  public OAuth2StrategyComposite(Set<OAuth2Strategy> clients) {
    this.oauth2ProviderMap = clients.stream()
        .collect(toMap(OAuth2Strategy::getOAuth2ProviderType, identity()));
  }

  public OAuth2Strategy getOAuth2Strategy(OAuthType provider) {
    return Optional.ofNullable(oauth2ProviderMap.get(provider))
        .orElseThrow(() -> new OAuth2AuthenticationException("not supported OAuth2 provider"));
  }
}
