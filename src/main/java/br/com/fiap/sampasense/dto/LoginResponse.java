package br.com.fiap.sampasense.dto;

/** Resposta do login: o token JWT a ser enviado no cabeçalho Authorization: Bearer {token}. */
public record LoginResponse(
        String token,
        String tipo,
        long expiraEmSegundos
) {
}
