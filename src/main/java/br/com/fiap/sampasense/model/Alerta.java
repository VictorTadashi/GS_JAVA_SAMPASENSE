package br.com.fiap.sampasense.model;

import br.com.fiap.sampasense.model.enums.CanalAlerta;
import br.com.fiap.sampasense.model.enums.NivelRisco;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Registro de um aviso disparado em um canal. Só o sistema cria alertas
 * (não existe POST público): nenhum alerta pode ser disparado por qualquer pessoa.
 */
@Entity
@Table(name = "TB_SS_ALERTA")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ALERTA")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_EVENTO", nullable = false)
    private EventoAlagamento evento;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_CANAL", nullable = false, length = 30)
    private CanalAlerta canal;

    @Enumerated(EnumType.STRING)
    @Column(name = "NV_RISCO", nullable = false, length = 20)
    private NivelRisco nivel;

    @Column(name = "DS_MENSAGEM", nullable = false, length = 255)
    private String mensagem;

    @Column(name = "DT_ALERTA", nullable = false)
    private LocalDateTime dataHora;

    public Alerta() {
    }

    public Alerta(EventoAlagamento evento, CanalAlerta canal, NivelRisco nivel, String mensagem) {
        this.evento = evento;
        this.canal = canal;
        this.nivel = nivel;
        this.mensagem = mensagem;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EventoAlagamento getEvento() { return evento; }
    public void setEvento(EventoAlagamento evento) { this.evento = evento; }

    public CanalAlerta getCanal() { return canal; }
    public void setCanal(CanalAlerta canal) { this.canal = canal; }

    public NivelRisco getNivel() { return nivel; }
    public void setNivel(NivelRisco nivel) { this.nivel = nivel; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
