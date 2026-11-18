package br.com.fiap.sampasense.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

/** Medição enviada por um sensor: chuva (mm) ou nível da água (cm). */
@Entity
@Table(name = "TB_SS_LEITURA")
public class Leitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LEITURA")
    private Long id;

    @NotNull(message = "Informe o sensor. Ex.: \"sensor\": {\"id\": 1}")
    @ManyToOne
    @JoinColumn(name = "ID_SENSOR", nullable = false)
    private Sensor sensor;

    @NotNull(message = "O valor da leitura é obrigatório")
    @PositiveOrZero(message = "O valor da leitura não pode ser negativo")
    @Column(name = "VL_LEITURA", nullable = false)
    private Double valor;

    /** Definida pelo servidor no momento do registro. */
    @Column(name = "DT_LEITURA", nullable = false)
    private LocalDateTime dataHora;

    public Leitura() {
    }

    public Leitura(Sensor sensor, Double valor, LocalDateTime dataHora) {
        this.sensor = sensor;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
