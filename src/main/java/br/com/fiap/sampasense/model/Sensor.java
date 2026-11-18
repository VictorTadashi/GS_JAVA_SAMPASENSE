package br.com.fiap.sampasense.model;

import br.com.fiap.sampasense.model.enums.StatusSensor;
import br.com.fiap.sampasense.model.enums.TipoSensor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Sensor instalado em poste ou totem. Envia apenas medições numéricas. */
@Entity
@Table(name = "TB_SS_SENSOR")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SENSOR")
    private Long id;

    @NotBlank(message = "O código do sensor é obrigatório")
    @Size(max = 30, message = "O código deve ter no máximo 30 caracteres")
    @Column(name = "CD_SENSOR", nullable = false, unique = true, length = 30)
    private String codigo;

    @NotNull(message = "O tipo é obrigatório (PLUVIOMETRO ou NIVEL_AGUA)")
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_SENSOR", nullable = false, length = 20)
    private TipoSensor tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "ST_SENSOR", nullable = false, length = 20)
    private StatusSensor status;

    @Column(name = "DT_INSTALACAO", nullable = false)
    private LocalDate dataInstalacao;

    @NotNull(message = "Informe o ponto monitorado. Ex.: \"ponto\": {\"id\": 1}")
    @ManyToOne
    @JoinColumn(name = "ID_PONTO", nullable = false)
    private PontoMonitorado ponto;

    public Sensor() {
    }

    public Sensor(String codigo, TipoSensor tipo, StatusSensor status, LocalDate dataInstalacao, PontoMonitorado ponto) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.status = status;
        this.dataInstalacao = dataInstalacao;
        this.ponto = ponto;
    }

    public boolean estaAtivo() {
        return status == StatusSensor.ATIVO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public TipoSensor getTipo() { return tipo; }
    public void setTipo(TipoSensor tipo) { this.tipo = tipo; }

    public StatusSensor getStatus() { return status; }
    public void setStatus(StatusSensor status) { this.status = status; }

    public LocalDate getDataInstalacao() { return dataInstalacao; }
    public void setDataInstalacao(LocalDate dataInstalacao) { this.dataInstalacao = dataInstalacao; }

    public PontoMonitorado getPonto() { return ponto; }
    public void setPonto(PontoMonitorado ponto) { this.ponto = ponto; }
}
