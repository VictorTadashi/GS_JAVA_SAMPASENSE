package br.com.fiap.sampasense.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corpo enviado pelo operador da CET/CGE ao confirmar ou recusar um bloqueio. */
public record DecisaoBloqueioRequest(
        @NotBlank(message = "Informe o operador responsável pela decisão")
        @Size(max = 60, message = "O operador deve ter no máximo 60 caracteres")
        String operador
) {
}
