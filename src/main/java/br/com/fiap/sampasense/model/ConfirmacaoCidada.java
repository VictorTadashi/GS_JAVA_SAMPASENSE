package br.com.fiap.sampasense.model;

import br.com.fiap.sampasense.model.enums.TipoConfirmacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Confirmação ANÔNIMA de um morador ("alagou aqui" / "já baixou").
 * Privacidade por design: não guarda nome, telefone nem localização de quem confirmou.
 */
@Entity
@Table(name = "TB_SS_CONFIRMACAO_CIDADA")
public class ConfirmacaoCidada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONFIRMACAO")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_EVENTO", nullable = false)
    private EventoAlagamento evento;

    @NotNull(message = "O tipo é obrigatório (ALAGOU ou BAIXOU)")
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_CONFIRMACAO", nullable = false, length = 20)
    private TipoConfirmacao tipo;

    @Column(name = "DT_CONFIRMACAO", nullable = false)
    private LocalDateTime dataHora;

    public ConfirmacaoCidada() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EventoAlagamento getEvento() { return evento; }
    public void setEvento(EventoAlagamento evento) { this.evento = evento; }

    public TipoConfirmacao getTipo() { return tipo; }
    public void setTipo(TipoConfirmacao tipo) { this.tipo = tipo; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
