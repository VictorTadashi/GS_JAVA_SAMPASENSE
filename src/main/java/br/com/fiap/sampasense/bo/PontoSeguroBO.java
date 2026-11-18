package br.com.fiap.sampasense.bo;

import br.com.fiap.sampasense.dao.PontoSeguroDAO;
import br.com.fiap.sampasense.dto.PontoSeguroProximoResponse;
import br.com.fiap.sampasense.exception.RecursoNaoEncontradoException;
import br.com.fiap.sampasense.exception.RegraNegocioException;
import br.com.fiap.sampasense.model.PontoMonitorado;
import br.com.fiap.sampasense.model.PontoSeguro;
import br.com.fiap.sampasense.util.GeoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class PontoSeguroBO {

    private final PontoSeguroDAO dao;
    private final PontoMonitoradoBO pontoBO;

    public PontoSeguroBO(PontoSeguroDAO dao, PontoMonitoradoBO pontoBO) {
        this.dao = dao;
        this.pontoBO = pontoBO;
    }

    @Transactional(readOnly = true)
    public List<PontoSeguro> listar() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public PontoSeguro buscarPorId(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ponto seguro", id));
    }

    /**
     * Pontos seguros que atendem um ponto de risco, do mais próximo ao mais distante.
     * Com apenasAcessiveis=true, só retorna locais sem escadas/ladeiras (idosos e PcD).
     * A rota completa (desviando de trechos alagados) é calculada no módulo de grafos (Python).
     */
    @Transactional(readOnly = true)
    public List<PontoSeguroProximoResponse> listarProximos(Long pontoId, boolean apenasAcessiveis) {
        PontoMonitorado ponto = pontoBO.buscarPorId(pontoId);
        List<PontoSeguro> seguros = apenasAcessiveis
                ? dao.findByPontoMonitoradoIdAndAcessivelTrue(pontoId)
                : dao.findByPontoMonitoradoId(pontoId);

        return seguros.stream()
                .map(s -> new PontoSeguroProximoResponse(
                        s.getId(), s.getNome(), s.getTipo(), s.getEndereco(), s.getAcessivel(), s.getCapacidade(),
                        GeoUtil.distanciaEmMetros(ponto.getLatitude(), ponto.getLongitude(),
                                s.getLatitude(), s.getLongitude())))
                .sorted(Comparator.comparingLong(PontoSeguroProximoResponse::distanciaMetros))
                .toList();
    }

    @Transactional
    public PontoSeguro cadastrar(PontoSeguro pontoSeguro) {
        pontoSeguro.setId(null);
        pontoSeguro.setPontoMonitorado(resolverPonto(pontoSeguro));
        return dao.save(pontoSeguro);
    }

    @Transactional
    public PontoSeguro atualizar(Long id, PontoSeguro pontoSeguro) {
        buscarPorId(id);
        pontoSeguro.setId(id);
        pontoSeguro.setPontoMonitorado(resolverPonto(pontoSeguro));
        return dao.save(pontoSeguro);
    }

    @Transactional
    public void remover(Long id) {
        buscarPorId(id);
        dao.deleteById(id);
        dao.flush(); // força o DELETE agora: vínculos no banco viram HTTP 409
    }

    private PontoMonitorado resolverPonto(PontoSeguro pontoSeguro) {
        if (pontoSeguro.getPontoMonitorado() == null || pontoSeguro.getPontoMonitorado().getId() == null) {
            throw new RegraNegocioException("Informe o id do ponto monitorado atendido por este ponto seguro.");
        }
        return pontoBO.buscarPorId(pontoSeguro.getPontoMonitorado().getId());
    }
}
