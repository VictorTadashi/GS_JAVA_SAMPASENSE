package br.com.fiap.sampasense.dto;

import br.com.fiap.sampasense.model.enums.NivelRisco;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Situação atual de um ponto: é o conteúdo exibido no portal do Wi-Fi em modo emergência
 * e no painel do totem (nível, orientação e para onde ir).
 */
public record RiscoResponse(
        Long pontoId,
        String ponto,
        String bairro,
        NivelRisco nivel,
        String orientacao,
        Long eventoId,
        LocalDateTime desde,
        List<PontoSeguroProximoResponse> pontosSeguros
) {
}
