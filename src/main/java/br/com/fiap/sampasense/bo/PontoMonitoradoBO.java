package br.com.fiap.sampasense.bo;

import br.com.fiap.sampasense.dao.PontoMonitoradoDAO;
import br.com.fiap.sampasense.exception.RecursoNaoEncontradoException;
import br.com.fiap.sampasense.model.PontoMonitorado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PontoMonitoradoBO {

    private final PontoMonitoradoDAO dao;

    public PontoMonitoradoBO(PontoMonitoradoDAO dao) {
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    public List<PontoMonitorado> listar() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public PontoMonitorado buscarPorId(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ponto monitorado", id));
    }

    @Transactional
    public PontoMonitorado cadastrar(PontoMonitorado ponto) {
        ponto.setId(null);
        return dao.save(ponto);
    }

    @Transactional
    public PontoMonitorado atualizar(Long id, PontoMonitorado ponto) {
        buscarPorId(id);
        ponto.setId(id);
        return dao.save(ponto);
    }

    @Transactional
    public void remover(Long id) {
        buscarPorId(id);
        dao.deleteById(id);
        dao.flush(); // força o DELETE agora: vínculos no banco viram HTTP 409
    }
}
