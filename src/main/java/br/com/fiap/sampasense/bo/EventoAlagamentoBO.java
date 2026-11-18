package br.com.fiap.sampasense.bo;

import br.com.fiap.sampasense.dao.EventoAlagamentoDAO;
import br.com.fiap.sampasense.exception.RecursoNaoEncontradoException;
import br.com.fiap.sampasense.model.EventoAlagamento;
import br.com.fiap.sampasense.model.PontoMonitorado;
import br.com.fiap.sampasense.model.enums.StatusEvento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventoAlagamentoBO {

    private final EventoAlagamentoDAO dao;

    public EventoAlagamentoBO(EventoAlagamentoDAO dao) {
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    public List<EventoAlagamento> listar(StatusEvento status) {
        return status == null ? dao.findAllByOrderByDataInicioDesc() : dao.findByStatusOrderByDataInicioDesc(status);
    }

    @Transactional(readOnly = true)
    public EventoAlagamento buscarPorId(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento de alagamento", id));
    }

    @Transactional(readOnly = true)
    public Optional<EventoAlagamento> buscarAberto(Long pontoId) {
        return dao.findFirstByPontoIdAndStatusOrderByDataInicioDesc(pontoId, StatusEvento.ABERTO);
    }

    /** Abre um evento para o ponto. Só é chamado pelo motor de risco. */
    @Transactional
    public EventoAlagamento abrir(PontoMonitorado ponto) {
        return dao.save(EventoAlagamento.abrir(ponto));
    }

    @Transactional
    public EventoAlagamento salvar(EventoAlagamento evento) {
        return dao.save(evento);
    }

    /** Encerramento manual pelo operador (ex.: água já escoou e foi verificado no local). */
    @Transactional
    public EventoAlagamento encerrar(Long id) {
        return encerrar(buscarPorId(id));
    }

    @Transactional
    public EventoAlagamento encerrar(EventoAlagamento evento) {
        evento.encerrar();
        return dao.save(evento);
    }
}
