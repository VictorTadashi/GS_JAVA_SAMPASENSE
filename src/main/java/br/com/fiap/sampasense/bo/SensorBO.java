package br.com.fiap.sampasense.bo;

import br.com.fiap.sampasense.dao.SensorDAO;
import br.com.fiap.sampasense.exception.RecursoNaoEncontradoException;
import br.com.fiap.sampasense.exception.RegraNegocioException;
import br.com.fiap.sampasense.model.PontoMonitorado;
import br.com.fiap.sampasense.model.Sensor;
import br.com.fiap.sampasense.model.enums.StatusSensor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SensorBO {

    private final SensorDAO dao;
    private final PontoMonitoradoBO pontoBO;

    public SensorBO(SensorDAO dao, PontoMonitoradoBO pontoBO) {
        this.dao = dao;
        this.pontoBO = pontoBO;
    }

    @Transactional(readOnly = true)
    public List<Sensor> listar(Long pontoId) {
        return pontoId == null ? dao.findAll() : dao.findByPontoId(pontoId);
    }

    @Transactional(readOnly = true)
    public Sensor buscarPorId(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sensor", id));
    }

    @Transactional
    public Sensor cadastrar(Sensor sensor) {
        if (dao.existsByCodigo(sensor.getCodigo())) {
            throw new RegraNegocioException("Já existe um sensor com o código " + sensor.getCodigo() + ".");
        }
        sensor.setId(null);
        sensor.setPonto(resolverPonto(sensor));
        if (sensor.getStatus() == null) {
            sensor.setStatus(StatusSensor.ATIVO);
        }
        if (sensor.getDataInstalacao() == null) {
            sensor.setDataInstalacao(LocalDate.now());
        }
        return dao.save(sensor);
    }

    @Transactional
    public Sensor atualizar(Long id, Sensor sensor) {
        Sensor atual = buscarPorId(id);
        if (dao.existsByCodigoAndIdNot(sensor.getCodigo(), id)) {
            throw new RegraNegocioException("Já existe outro sensor com o código " + sensor.getCodigo() + ".");
        }
        sensor.setId(id);
        sensor.setPonto(resolverPonto(sensor));
        if (sensor.getStatus() == null) {
            sensor.setStatus(atual.getStatus());
        }
        if (sensor.getDataInstalacao() == null) {
            sensor.setDataInstalacao(atual.getDataInstalacao());
        }
        return dao.save(sensor);
    }

    @Transactional
    public void remover(Long id) {
        buscarPorId(id);
        dao.deleteById(id);
        dao.flush(); // força o DELETE agora: vínculos no banco viram HTTP 409
    }

    private PontoMonitorado resolverPonto(Sensor sensor) {
        if (sensor.getPonto() == null || sensor.getPonto().getId() == null) {
            throw new RegraNegocioException("Informe o id do ponto monitorado do sensor.");
        }
        return pontoBO.buscarPorId(sensor.getPonto().getId());
    }
}
