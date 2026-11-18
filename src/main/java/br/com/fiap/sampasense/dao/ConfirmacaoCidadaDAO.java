package br.com.fiap.sampasense.dao;

import br.com.fiap.sampasense.model.ConfirmacaoCidada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfirmacaoCidadaDAO extends JpaRepository<ConfirmacaoCidada, Long> {

    List<ConfirmacaoCidada> findByEventoIdOrderByDataHoraAsc(Long eventoId);
}
