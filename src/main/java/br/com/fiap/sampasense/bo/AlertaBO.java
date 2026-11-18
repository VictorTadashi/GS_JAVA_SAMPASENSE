package br.com.fiap.sampasense.bo;

import br.com.fiap.sampasense.dao.AlertaDAO;
import br.com.fiap.sampasense.exception.RecursoNaoEncontradoException;
import br.com.fiap.sampasense.model.Alerta;
import br.com.fiap.sampasense.model.EventoAlagamento;
import br.com.fiap.sampasense.model.enums.CanalAlerta;
import br.com.fiap.sampasense.model.enums.NivelRisco;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Dispara (registra) os alertas de cada canal. No protótipo o envio real
 * (SMS, sirene, Cell Broadcast...) é simulado: o alerta fica gravado no banco.
 */
@Service
public class AlertaBO {

    private final AlertaDAO dao;

    public AlertaBO(AlertaDAO dao) {
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    public List<Alerta> listar(Long eventoId) {
        return eventoId == null ? dao.findAllByOrderByDataHoraDesc() : dao.findByEventoIdOrderByDataHoraAsc(eventoId);
    }

    @Transactional(readOnly = true)
    public Alerta buscarPorId(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta", id));
    }

    /** Aciona todos os canais previstos para o nível de risco. */
    @Transactional
    public List<Alerta> dispararAlertas(EventoAlagamento evento, NivelRisco nivel) {
        String mensagem = nivel.mensagemPara(evento.getPonto().getNome());
        List<Alerta> alertas = nivel.getCanais().stream()
                .map(canal -> new Alerta(evento, canal, nivel, mensagem))
                .toList();
        return dao.saveAll(alertas);
    }

    /** Aciona um canal específico com uma mensagem própria (ex.: fechamento de semáforo). */
    @Transactional
    public Alerta registrar(EventoAlagamento evento, CanalAlerta canal, String mensagem) {
        return dao.save(new Alerta(evento, canal, evento.getNivel(), mensagem));
    }
}
