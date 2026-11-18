package br.com.fiap.sampasense.bo;

import br.com.fiap.sampasense.dao.LeituraDAO;
import br.com.fiap.sampasense.dao.SensorDAO;
import br.com.fiap.sampasense.dto.RiscoResponse;
import br.com.fiap.sampasense.model.EventoAlagamento;
import br.com.fiap.sampasense.model.Leitura;
import br.com.fiap.sampasense.model.PontoMonitorado;
import br.com.fiap.sampasense.model.Sensor;
import br.com.fiap.sampasense.model.enums.NivelRisco;
import br.com.fiap.sampasense.model.enums.StatusSensor;
import br.com.fiap.sampasense.model.enums.TipoSensor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * MOTOR DE RISCO — o coração do domínio do SampaSense.
 *
 * Regras:
 *  - Pluviômetro com chuva acumulada >= limite do ponto ............ ATENÇÃO (previsão)
 *  - Nível da água subindo rápido OU acima do limite (1 sensor) ..... ALERTA
 *  - Dois sensores de nível acima do limite (confirmação dupla) ...... BLOQUEIO
 *  - O nível de um evento só sobe; cada subida dispara os alertas do novo nível.
 *  - Em BLOQUEIO é gerada a recomendação de fechar os semáforos.
 *
 * Simplificação do protótipo: a previsão usa limiares fixos por ponto. O modelo estatístico
 * (regressão) é desenvolvido na disciplina de Data Science e pode substituir esses limiares.
 */
@Service
public class AvaliacaoRiscoBO {

    private final SensorDAO sensorDAO;
    private final LeituraDAO leituraDAO;
    private final PontoMonitoradoBO pontoBO;
    private final EventoAlagamentoBO eventoBO;
    private final AlertaBO alertaBO;
    private final RecomendacaoBloqueioBO bloqueioBO;
    private final PontoSeguroBO pontoSeguroBO;

    public AvaliacaoRiscoBO(SensorDAO sensorDAO, LeituraDAO leituraDAO, PontoMonitoradoBO pontoBO,
                            EventoAlagamentoBO eventoBO, AlertaBO alertaBO,
                            RecomendacaoBloqueioBO bloqueioBO, PontoSeguroBO pontoSeguroBO) {
        this.sensorDAO = sensorDAO;
        this.leituraDAO = leituraDAO;
        this.pontoBO = pontoBO;
        this.eventoBO = eventoBO;
        this.alertaBO = alertaBO;
        this.bloqueioBO = bloqueioBO;
        this.pontoSeguroBO = pontoSeguroBO;
    }

    /**
     * Avalia uma leitura recém-salva. Retorna o evento aberto do ponto (ou null se não houver risco).
     */
    @Transactional
    public EventoAlagamento avaliarLeitura(Leitura leitura, Leitura anterior) {
        PontoMonitorado ponto = leitura.getSensor().getPonto();
        NivelRisco nivelCalculado = calcularNivel(ponto, leitura, anterior);

        if (nivelCalculado == NivelRisco.NORMAL) {
            return eventoBO.buscarAberto(ponto.getId()).orElse(null);
        }

        EventoAlagamento evento = eventoBO.buscarAberto(ponto.getId())
                .orElseGet(() -> eventoBO.abrir(ponto));
        return escalarEvento(evento, nivelCalculado);
    }

    /** Sobe o nível do evento e executa as ações do novo nível. */
    @Transactional
    public EventoAlagamento escalarEvento(EventoAlagamento evento, NivelRisco novoNivel) {
        if (!evento.escalarPara(novoNivel)) {
            return evento; // nível igual ou menor: nada muda
        }
        EventoAlagamento salvo = eventoBO.salvar(evento);
        alertaBO.dispararAlertas(salvo, novoNivel);
        if (novoNivel == NivelRisco.BLOQUEIO) {
            bloqueioBO.gerarRecomendacao(salvo);
        }
        return salvo;
    }

    /** Quantos sensores de nível ativos do ponto estão, na última leitura, acima do limite. */
    @Transactional(readOnly = true)
    public long contarSensoresNivelAcimaDoLimite(PontoMonitorado ponto) {
        return sensorDAO.findByPontoIdAndTipoAndStatus(ponto.getId(), TipoSensor.NIVEL_AGUA, StatusSensor.ATIVO)
                .stream()
                .map(sensor -> leituraDAO.findFirstBySensorIdOrderByDataHoraDescIdDesc(sensor.getId()))
                .flatMap(Optional::stream)
                .filter(ultima -> ponto.nivelAcimaDoLimite(ultima.getValor()))
                .count();
    }

    /** Situação atual do ponto, com orientação e pontos seguros (conteúdo do totem e do Wi-Fi). */
    @Transactional(readOnly = true)
    public RiscoResponse consultarRisco(Long pontoId) {
        PontoMonitorado ponto = pontoBO.buscarPorId(pontoId);
        Optional<EventoAlagamento> aberto = eventoBO.buscarAberto(pontoId);
        NivelRisco nivel = aberto.map(EventoAlagamento::getNivel).orElse(NivelRisco.NORMAL);

        return new RiscoResponse(
                ponto.getId(),
                ponto.getNome(),
                ponto.getBairro(),
                nivel,
                nivel.getOrientacao(),
                aberto.map(EventoAlagamento::getId).orElse(null),
                aberto.map(EventoAlagamento::getDataInicio).orElse(null),
                pontoSeguroBO.listarProximos(pontoId, false)
        );
    }

    private NivelRisco calcularNivel(PontoMonitorado ponto, Leitura leitura, Leitura anterior) {
        Sensor sensor = leitura.getSensor();
        double valor = leitura.getValor();

        if (sensor.getTipo() == TipoSensor.PLUVIOMETRO) {
            return ponto.chuvaAcimaDoLimite(valor) ? NivelRisco.ATENCAO : NivelRisco.NORMAL;
        }

        // Sensor de nível da água
        if (contarSensoresNivelAcimaDoLimite(ponto) >= 2) {
            return NivelRisco.BLOQUEIO;
        }
        boolean subiuRapido = anterior != null && ponto.subidaRapida(valor - anterior.getValor());
        if (subiuRapido || ponto.nivelAcimaDoLimite(valor)) {
            return NivelRisco.ALERTA;
        }
        return NivelRisco.NORMAL;
    }
}
