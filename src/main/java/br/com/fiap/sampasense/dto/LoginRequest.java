package br.com.fiap.sampasense.dto;

import jakarta.validation.constraints.NotBlank;

/** Corpo do login: e-mail e senha do usuário. */
public record LoginRequest(
        @NotBlank(message = "Informe o e-mail") String email,
        @NotBlank(message = "Informe a senha") String senha
) {
}
