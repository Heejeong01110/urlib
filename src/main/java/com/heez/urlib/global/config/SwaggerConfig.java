package com.heez.urlib.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "URLib API", version = "v1.0.0", description = "URLib API 명세서"),
    security = @SecurityRequirement(name = "JWT") // 전역 보안 적용
)
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("JWT"))
        .components(new Components().addSecuritySchemes("JWT",
            new SecurityScheme()
                .name("JWT")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        ));
  }
}
