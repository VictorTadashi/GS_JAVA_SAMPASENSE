package br.com.fiap.sampasense.dao;

import br.com.fiap.sampasense.model.Sensor;
import br.com.fiap.sampasense.model.enums.StatusSensor;
import br.com.fiap.sampasense.model.enums.TipoSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDAO extends JpaRepository<Sensor, Long> {

    List<Sensor> findByPontoId(Long pontoId);

    List<Sensor> findByPontoIdAndTipoAndStatus(Long pontoId, TipoSensor tipo, StatusSensor status);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);
}
