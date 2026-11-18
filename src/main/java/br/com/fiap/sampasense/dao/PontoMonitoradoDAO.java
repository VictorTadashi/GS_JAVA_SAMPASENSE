package br.com.fiap.sampasense.dao;

import br.com.fiap.sampasense.model.PontoMonitorado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acesso a dados de pontos monitorados (Spring Data JPA gera o SQL). */
@Repository
public interface PontoMonitoradoDAO extends JpaRepository<PontoMonitorado, Long> {
}
