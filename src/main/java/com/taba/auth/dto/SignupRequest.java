package com.taba.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 8, message = "{validation.password.min_length}")
    private String password;

    @NotBlank(message = "{validation.nickname.required}")
    @Size(min = 2, max = 50, message = "{validation.nickname.size}")
    private String nickname;

    @NotNull(message = "{validation.terms.required}")
    private Boolean agreeTerms;

    @NotNull(message = "{validation.privacy.required}")
    private Boolean agreePrivacy;
}

