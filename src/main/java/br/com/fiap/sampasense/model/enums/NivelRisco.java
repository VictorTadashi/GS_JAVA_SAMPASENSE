package br.com.fiap.sampasense.model.enums;

import java.util.List;

import static br.com.fiap.sampasense.model.enums.CanalAlerta.*;

/**
 * Níveis de risco do SampaSense. A ordem importa: um evento só pode subir de nível.
 * Cada nível sabe quais canais devem ser acionados e qual orientação dar ao cidadão.
 */
public enum NivelRisco {

    NORMAL("Sem risco de alagamento no momento.",
            List.of()),

    ATENCAO("Risco alto de alagamento previsto. Evite a região se puder.",
            List.of(WHATSAPP, SMS, PAINEL_TOTEM)),

    ALERTA("Água subindo. Saia da área baixa e siga até o ponto seguro mais próximo.",
            List.of(PAINEL_TOTEM, SIRENE, WIFI_EMERGENCIA, CELL_BROADCAST)),

    BLOQUEIO("Alagamento confirmado. Não entre na área e siga até o ponto seguro mais próximo.",
            List.of(PAINEL_TOTEM));

    private final String orientacao;
    private final List<CanalAlerta> canais;

    NivelRisco(String orientacao, List<CanalAlerta> canais) {
        this.orientacao = orientacao;
        this.canais = canais;
    }

    public String getOrientacao() {
        return orientacao;
    }

    public List<CanalAlerta> getCanais() {
        return canais;
    }

    public boolean maiorQue(NivelRisco outro) {
        return this.ordinal() > outro.ordinal();
    }

    public String mensagemPara(String local) {
        return "[" + name() + "] " + local + ": " + orientacao;
    }
}
