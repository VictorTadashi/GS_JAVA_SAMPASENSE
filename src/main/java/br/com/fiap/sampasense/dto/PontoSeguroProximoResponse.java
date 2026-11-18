package br.com.fiap.sampasense.dto;

import br.com.fiap.sampasense.model.enums.TipoPontoSeguro;

/** Ponto seguro com a distância em linha reta até o ponto de risco. */
public record PontoSeguroProximoResponse(
        Long id,
        String nome,
        TipoPontoSeguro tipo,
        String endereco,
        Boolean acessivel,
        Integer capacidade,
        long distanciaMetros
) {
}
