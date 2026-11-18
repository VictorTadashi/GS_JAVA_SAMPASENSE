package br.com.fiap.sampasense.bo;

import br.com.fiap.sampasense.dao.LeituraDAO;
import br.com.fiap.sampasense.dto.ResultadoLeituraResponse;
import br.com.fiap.sampasense.exception.RecursoNaoEncontradoException;
import br.com.fiap.sampasense.exception.RegraNegocioException;
import br.com.fiap.sampasense.model.EventoAlagamento;
import br.com.fiap.sampasense.model.Leitura;
import br.com.fiap.sampasense.model.Sensor;
import br.com.fiap.sampasense.model.enums.NivelRisco;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeituraBO {

    private final LeituraDAO dao;
    private final SensorBO sensorBO;
    private final AvaliacaoRiscoBO avaliacaoRiscoBO;

    public LeituraBO(LeituraDAO dao, SensorBO sensorBO, AvaliacaoRiscoBO avaliacaoRiscoBO) {
        this.dao = dao;
        this.sensorBO = sensorBO;
        this.avaliacaoRiscoBO = avaliacaoRiscoBO;
    }

    @Transactional(readOnly = true)
    public List<Leitura> listar(Long sensorId) {
        return sensorId == null ? dao.findAllByOrderByDataHoraDesc() : dao.findBySensorIdOrderByDataHoraDesc(sensorId);
    }

    @Transactional(readOnly = true)
    public Leitura buscarPorId(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitura", id));
    }

    /** Registra a medição do sensor e, em seguida, roda o motor de risco do ponto. */
    @Transactional
    public ResultadoLeituraResponse registrar(Leitura leitura) {
        if (leitura.getSensor() == null || leitura.getSensor().getId() == null) {
            throw new RegraNegocioException("Informe o id do sensor da leitura.");
        }
        Sensor sensor = sensorBO.buscarPorId(leitura.getSensor().getId());
        if (!sensor.estaAtivo()) {
            throw new RegraNegocioException("O sensor " + sensor.getCodigo()
                    + " está " + sensor.getStatus() + " e não pode enviar leituras.");
        }

        Leitura anterior = dao.findFirstBySensorIdOrderByDataHoraDescIdDesc(sensor.getId()).orElse(null);

        leitura.setId(null);
        leitura.setSensor(sensor);
        leitura.setDataHora(LocalDateTime.now());
        Leitura salva = dao.save(leitura);

        EventoAlagamento evento = avaliacaoRiscoBO.avaliarLeitura(salva, anterior);
        NivelRisco nivel = evento == null ? NivelRisco.NORMAL : evento.getNivel();
        return new ResultadoLeituraResponse(salva, nivel, nivel.getOrientacao(),
                evento == null ? null : evento.getId());
    }

    @Transactional
    public void remover(Long id) {
        buscarPorId(id);
        dao.deleteById(id);
        dao.flush(); // força o DELETE agora: vínculos no banco viram HTTP 409
    }
}
