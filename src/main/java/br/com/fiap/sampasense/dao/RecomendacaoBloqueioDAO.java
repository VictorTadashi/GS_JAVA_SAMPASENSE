package br.com.fiap.sampasense.dao;

import br.com.fiap.sampasense.model.RecomendacaoBloqueio;
import br.com.fiap.sampasense.model.enums.StatusBloqueio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecomendacaoBloqueioDAO extends JpaRepository<RecomendacaoBloqueio, Long> {

    List<RecomendacaoBloqueio> findByStatusOrderByDataCriacaoDesc(StatusBloqueio status);

    List<RecomendacaoBloqueio> findAllByOrderByDataCriacaoDesc();

    boolean existsByEventoId(Long eventoId);
}
