package br.com.fiap.sampasense.dao;

import br.com.fiap.sampasense.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaDAO extends JpaRepository<Alerta, Long> {

    List<Alerta> findByEventoIdOrderByDataHoraAsc(Long eventoId);

    List<Alerta> findAllByOrderByDataHoraDesc();
}
