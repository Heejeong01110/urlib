package com.heez.urlib.domain.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Schema(description = "회원가입 요청 DTO")
@Builder
public record SignUpRequest(

    @Schema(description = "이메일", example = "testEmail@email.com")
    @Email(message = "유효한 이메일을 입력해주세요")
    String email,

    @Schema(description = "비밀번호", example = "password1234")
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    String password,

    @Schema(description = "닉네임", example = "testUser001")
    @NotBlank(message = "닉네임은 필수입니다")
    String nickname

) {

}
