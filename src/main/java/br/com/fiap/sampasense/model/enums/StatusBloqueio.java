package br.com.fiap.sampasense.model.enums;

/**
 * PENDENTE: aguardando o operador da CET/CGE.
 * CONFIRMADO / RECUSADO: decisão do operador.
 * AUTOMATICO: túnel crítico, fechado pela regra explícita do sistema.
 */
public enum StatusBloqueio {
    PENDENTE,
    CONFIRMADO,
    RECUSADO,
    AUTOMATICO
}
