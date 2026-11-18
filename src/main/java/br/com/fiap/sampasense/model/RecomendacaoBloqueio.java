package br.com.fiap.sampasense.model;

import br.com.fiap.sampasense.exception.RegraNegocioException;
import br.com.fiap.sampasense.model.enums.StatusBloqueio;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Recomendação de fechar os semáforos de acesso ao ponto.
 * Fechar semáforo mexe com o trânsito da região, por isso passa por um operador humano,
 * exceto em túneis críticos (regra explícita: bloqueio automático).
 */
@Entity
@Table(name = "TB_SS_RECOMENDACAO_BLOQUEIO")
public class RecomendacaoBloqueio {

    public static final String OPERADOR_SISTEMA = "SISTEMA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_BLOQUEIO")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_EVENTO", nullable = false)
    private EventoAlagamento evento;

    @Enumerated(EnumType.STRING)
    @Column(name = "ST_BLOQUEIO", nullable = false, length = 20)
    private StatusBloqueio status;

    @Column(name = "NM_OPERADOR", length = 60)
    private String operador;

    @Column(name = "DT_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "DT_DECISAO")
    private LocalDateTime dataDecisao;

    public RecomendacaoBloqueio() {
    }

    /** Fábrica: decide entre bloqueio automático (túnel crítico) e pendente (operador decide). */
    public static RecomendacaoBloqueio para(EventoAlagamento evento) {
        RecomendacaoBloqueio r = new RecomendacaoBloqueio();
        r.evento = evento;
        r.dataCriacao = LocalDateTime.now();
        if (evento.getPonto().ehCritico()) {
            r.status = StatusBloqueio.AUTOMATICO;
            r.operador = OPERADOR_SISTEMA;
            r.dataDecisao = r.dataCriacao;
        } else {
            r.status = StatusBloqueio.PENDENTE;
        }
        return r;
    }

    // ===== Regras do domínio =====

    public boolean estaPendente() {
        return status == StatusBloqueio.PENDENTE;
    }

    public void confirmar(String operador) {
        validarDecisao();
        this.status = StatusBloqueio.CONFIRMADO;
        this.operador = operador;
        this.dataDecisao = LocalDateTime.now();
    }

    public void recusar(String operador) {
        validarDecisao();
        this.status = StatusBloqueio.RECUSADO;
        this.operador = operador;
        this.dataDecisao = LocalDateTime.now();
    }

    private void validarDecisao() {
        if (!estaPendente()) {
            throw new RegraNegocioException("A recomendação " + id + " já foi decidida (status " + status + ").");
        }
        if (!evento.estaAberto()) {
            throw new RegraNegocioException("O evento desta recomendação já foi encerrado.");
        }
    }

    // ===== Getters e setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EventoAlagamento getEvento() { return evento; }
    public void setEvento(EventoAlagamento evento) { this.evento = evento; }

    public StatusBloqueio getStatus() { return status; }
    public void setStatus(StatusBloqueio status) { this.status = status; }

    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataDecisao() { return dataDecisao; }
    public void setDataDecisao(LocalDateTime dataDecisao) { this.dataDecisao = dataDecisao; }
}
