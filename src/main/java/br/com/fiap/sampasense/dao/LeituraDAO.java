package br.com.fiap.sampasense.dao;

import br.com.fiap.sampasense.model.Leitura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeituraDAO extends JpaRepository<Leitura, Long> {

    /** Última leitura de um sensor (usada para calcular a velocidade de subida da água). */
    Optional<Leitura> findFirstBySensorIdOrderByDataHoraDescIdDesc(Long sensorId);

    List<Leitura> findBySensorIdOrderByDataHoraDesc(Long sensorId);

    List<Leitura> findAllByOrderByDataHoraDesc();
}
