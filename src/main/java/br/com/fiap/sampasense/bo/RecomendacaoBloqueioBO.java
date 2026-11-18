package br.com.fiap.sampasense.bo;

import br.com.fiap.sampasense.dao.RecomendacaoBloqueioDAO;
import br.com.fiap.sampasense.exception.RecursoNaoEncontradoException;
import br.com.fiap.sampasense.model.EventoAlagamento;
import br.com.fiap.sampasense.model.RecomendacaoBloqueio;
import br.com.fiap.sampasense.model.enums.CanalAlerta;
import br.com.fiap.sampasense.model.enums.StatusBloqueio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecomendacaoBloqueioBO {

    private final RecomendacaoBloqueioDAO dao;
    private final AlertaBO alertaBO;

    public RecomendacaoBloqueioBO(RecomendacaoBloqueioDAO dao, AlertaBO alertaBO) {
        this.dao = dao;
        this.alertaBO = alertaBO;
    }

    @Transactional(readOnly = true)
    public List<RecomendacaoBloqueio> listar(StatusBloqueio status) {
        return status == null ? dao.findAllByOrderByDataCriacaoDesc() : dao.findByStatusOrderByDataCriacaoDesc(status);
    }

    @Transactional(readOnly = true)
    public RecomendacaoBloqueio buscarPorId(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recomendação de bloqueio", id));
    }

    /**
     * Chamado quando o evento chega a BLOQUEIO.
     * Túnel crítico: fecha os semáforos na hora. Demais pontos: fica PENDENTE para o operador.
     */
    @Transactional
    public RecomendacaoBloqueio gerarRecomendacao(EventoAlagamento evento) {
        if (dao.existsByEventoId(evento.getId())) {
            return null;
        }
        RecomendacaoBloqueio recomendacao = dao.save(RecomendacaoBloqueio.para(evento));
        if (recomendacao.getStatus() == StatusBloqueio.AUTOMATICO) {
            alertaBO.registrar(evento, CanalAlerta.SEMAFORO,
                    "Semáforos de acesso a " + evento.getPonto().getNome()
                            + " fechados automaticamente (túnel crítico).");
        }
        return recomendacao;
    }

    @Transactional
    public RecomendacaoBloqueio confirmar(Long id, String operador) {
        RecomendacaoBloqueio recomendacao = buscarPorId(id);
        recomendacao.confirmar(operador);
        alertaBO.registrar(recomendacao.getEvento(), CanalAlerta.SEMAFORO,
                "Semáforos de acesso a " + recomendacao.getEvento().getPonto().getNome()
                        + " fechados após confirmação do operador " + operador + ".");
        return dao.save(recomendacao);
    }

    @Transactional
    public RecomendacaoBloqueio recusar(Long id, String operador) {
        RecomendacaoBloqueio recomendacao = buscarPorId(id);
        recomendacao.recusar(operador);
        return dao.save(recomendacao);
    }
}
