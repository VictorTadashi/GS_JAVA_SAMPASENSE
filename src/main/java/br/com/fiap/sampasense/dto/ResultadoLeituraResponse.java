package br.com.fiap.sampasense.dto;

import br.com.fiap.sampasense.model.Leitura;
import br.com.fiap.sampasense.model.enums.NivelRisco;

/** Resposta do registro de leitura: a leitura salva e o nível de risco resultante no ponto. */
public record ResultadoLeituraResponse(
        Leitura leitura,
        NivelRisco nivelAtual,
        String orientacao,
        Long eventoId
) {
}
