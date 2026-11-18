package br.com.fiap.sampasense.model;

import br.com.fiap.sampasense.exception.RegraNegocioException;
import br.com.fiap.sampasense.model.enums.NivelRisco;
import br.com.fiap.sampasense.model.enums.StatusEvento;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Ocorrência de risco em um ponto. É aberta pelo sistema (nunca manualmente),
 * só sobe de nível e vira histórico para a subprefeitura quando encerrada.
 */
@Entity
@Table(name = "TB_SS_EVENTO_ALAGAMENTO")
public class EventoAlagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVENTO")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_PONTO", nullable = false)
    private PontoMonitorado ponto;

    @Enumerated(EnumType.STRING)
    @Column(name = "NV_RISCO", nullable = false, length = 20)
    private NivelRisco nivel;

    @Enumerated(EnumType.STRING)
    @Column(name = "ST_EVENTO", nullable = false, length = 20)
    private StatusEvento status;

    @Column(name = "DT_INICIO", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "DT_FIM")
    private LocalDateTime dataFim;

    public EventoAlagamento() {
    }

    /** Fábrica: todo evento nasce aberto, em nível NORMAL, e é escalado em seguida. */
    public static EventoAlagamento abrir(PontoMonitorado ponto) {
        EventoAlagamento evento = new EventoAlagamento();
        evento.ponto = ponto;
        evento.nivel = NivelRisco.NORMAL;
        evento.status = StatusEvento.ABERTO;
        evento.dataInicio = LocalDateTime.now();
        return evento;
    }

    // ===== Regras do domínio =====

    public boolean estaAberto() {
        return status == StatusEvento.ABERTO;
    }

    /** Sobe o nível do evento. Retorna false se o novo nível não for maior (o nível nunca regride). */
    public boolean escalarPara(NivelRisco novoNivel) {
        if (!estaAberto()) {
            throw new RegraNegocioException("O evento " + id + " já está encerrado.");
        }
        if (!novoNivel.maiorQue(nivel)) {
            return false;
        }
        this.nivel = novoNivel;
        return true;
    }

    public void encerrar() {
        if (!estaAberto()) {
            throw new RegraNegocioException("O evento " + id + " já está encerrado.");
        }
        this.status = StatusEvento.ENCERRADO;
        this.dataFim = LocalDateTime.now();
    }

    // ===== Getters e setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PontoMonitorado getPonto() { return ponto; }
    public void setPonto(PontoMonitorado ponto) { this.ponto = ponto; }

    public NivelRisco getNivel() { return nivel; }
    public void setNivel(NivelRisco nivel) { this.nivel = nivel; }

    public StatusEvento getStatus() { return status; }
    public void setStatus(StatusEvento status) { this.status = status; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
}
