package br.com.fiap.sampasense.dao;

import br.com.fiap.sampasense.model.PontoSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PontoSeguroDAO extends JpaRepository<PontoSeguro, Long> {

    List<PontoSeguro> findByPontoMonitoradoId(Long pontoId);

    List<PontoSeguro> findByPontoMonitoradoIdAndAcessivelTrue(Long pontoId);
}
