package br.com.fiap.sampasense.bo;

import br.com.fiap.sampasense.dao.ConfirmacaoCidadaDAO;
import br.com.fiap.sampasense.exception.RegraNegocioException;
import br.com.fiap.sampasense.model.ConfirmacaoCidada;
import br.com.fiap.sampasense.model.EventoAlagamento;
import br.com.fiap.sampasense.model.enums.NivelRisco;
import br.com.fiap.sampasense.model.enums.TipoConfirmacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Confirmações dos moradores reduzem falsos alarmes:
 *  - ALAGOU em evento de ATENÇÃO ............................ sobe para ALERTA
 *  - ALAGOU em ALERTA + 1 sensor de nível acima do limite ..... sobe para BLOQUEIO (sensor + cidadão)
 *  - BAIXOU com todos os sensores de nível abaixo do limite ... encerra o evento
 */
@Service
public class ConfirmacaoCidadaBO {

    private final ConfirmacaoCidadaDAO dao;
    private final EventoAlagamentoBO eventoBO;
    private final AvaliacaoRiscoBO avaliacaoRiscoBO;

    public ConfirmacaoCidadaBO(ConfirmacaoCidadaDAO dao, EventoAlagamentoBO eventoBO,
                               AvaliacaoRiscoBO avaliacaoRiscoBO) {
        this.dao = dao;
        this.eventoBO = eventoBO;
        this.avaliacaoRiscoBO = avaliacaoRiscoBO;
    }

    @Transactional(readOnly = true)
    public List<ConfirmacaoCidada> listarPorEvento(Long eventoId) {
        eventoBO.buscarPorId(eventoId);
        return dao.findByEventoIdOrderByDataHoraAsc(eventoId);
    }

    @Transactional
    public ConfirmacaoCidada registrar(Long eventoId, ConfirmacaoCidada confirmacao) {
        EventoAlagamento evento = eventoBO.buscarPorId(eventoId);
        if (!evento.estaAberto()) {
            throw new RegraNegocioException("Não é possível confirmar o evento " + eventoId + ": ele já foi encerrado.");
        }

        confirmacao.setId(null);
        confirmacao.setEvento(evento);
        confirmacao.setDataHora(LocalDateTime.now());
        ConfirmacaoCidada salva = dao.save(confirmacao);

        long sensoresAcima = avaliacaoRiscoBO.contarSensoresNivelAcimaDoLimite(evento.getPonto());

        if (confirmacao.getTipo() == TipoConfirmacao.ALAGOU) {
            if (evento.getNivel() == NivelRisco.ALERTA && sensoresAcima >= 1) {
                avaliacaoRiscoBO.escalarEvento(evento, NivelRisco.BLOQUEIO);
            } else {
                avaliacaoRiscoBO.escalarEvento(evento, NivelRisco.ALERTA);
            }
        } else if (sensoresAcima == 0) {
            eventoBO.encerrar(evento);
        }
        return salva;
    }
}
