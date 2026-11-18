package br.com.fiap.sampasense.dao;

import br.com.fiap.sampasense.model.EventoAlagamento;
import br.com.fiap.sampasense.model.enums.StatusEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoAlagamentoDAO extends JpaRepository<EventoAlagamento, Long> {

    Optional<EventoAlagamento> findFirstByPontoIdAndStatusOrderByDataInicioDesc(Long pontoId, StatusEvento status);

    List<EventoAlagamento> findByStatusOrderByDataInicioDesc(StatusEvento status);

    List<EventoAlagamento> findAllByOrderByDataInicioDesc();
}
