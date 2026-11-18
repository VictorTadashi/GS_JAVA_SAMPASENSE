package br.com.fiap.sampasense.model;

import br.com.fiap.sampasense.model.enums.TipoPonto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Vale ou túnel com histórico de alagamento, onde ficam os sensores e o totem.
 * Os limites são configurados por ponto, pois cada local se comporta de um jeito.
 */
@Entity
@Table(name = "TB_SS_PONTO_MONITORADO")
public class PontoMonitorado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PONTO")
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    @Column(name = "NM_PONTO", nullable = false, length = 100)
    private String nome;

    @NotNull(message = "O tipo é obrigatório (VALE ou TUNEL)")
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_PONTO", nullable = false, length = 20)
    private TipoPonto tipo;

    @NotBlank(message = "O bairro é obrigatório")
    @Size(max = 80, message = "O bairro deve ter no máximo 80 caracteres")
    @Column(name = "NM_BAIRRO", nullable = false, length = 80)
    private String bairro;

    @NotNull(message = "A latitude é obrigatória")
    @Column(name = "NR_LATITUDE", nullable = false)
    private Double latitude;

    @NotNull(message = "A longitude é obrigatória")
    @Column(name = "NR_LONGITUDE", nullable = false)
    private Double longitude;

    /** Túnel crítico: o bloqueio dos semáforos é automático, sem esperar o operador. */
    @NotNull(message = "Informe se o ponto é crítico")
    @Column(name = "ST_CRITICO", nullable = false)
    private Boolean critico;

    /** Chuva acumulada na última hora (mm) a partir da qual o risco é previsto (ATENÇÃO). */
    @NotNull(message = "O limite de chuva é obrigatório")
    @Positive(message = "O limite de chuva deve ser positivo")
    @Column(name = "VL_LIMITE_CHUVA_MM", nullable = false)
    private Double limiteChuvaMm;

    /** Altura da água (cm) que caracteriza alagamento. */
    @NotNull(message = "O limite de nível é obrigatório")
    @Positive(message = "O limite de nível deve ser positivo")
    @Column(name = "VL_LIMITE_NIVEL_CM", nullable = false)
    private Double limiteNivelCm;

    /** Subida (cm) entre duas leituras seguidas que indica água subindo rápido (ALERTA). */
    @NotNull(message = "O limite de subida é obrigatório")
    @Positive(message = "O limite de subida deve ser positivo")
    @Column(name = "VL_LIMITE_SUBIDA_CM", nullable = false)
    private Double limiteSubidaCm;

    public PontoMonitorado() {
    }

    public PontoMonitorado(String nome, TipoPonto tipo, String bairro, Double latitude, Double longitude,
                           Boolean critico, Double limiteChuvaMm, Double limiteNivelCm, Double limiteSubidaCm) {
        this.nome = nome;
        this.tipo = tipo;
        this.bairro = bairro;
        this.latitude = latitude;
        this.longitude = longitude;
        this.critico = critico;
        this.limiteChuvaMm = limiteChuvaMm;
        this.limiteNivelCm = limiteNivelCm;
        this.limiteSubidaCm = limiteSubidaCm;
    }

    // ===== Regras do domínio =====

    public boolean chuvaAcimaDoLimite(double chuvaMm) {
        return chuvaMm >= limiteChuvaMm;
    }

    public boolean nivelAcimaDoLimite(double nivelCm) {
        return nivelCm >= limiteNivelCm;
    }

    public boolean subidaRapida(double variacaoCm) {
        return variacaoCm >= limiteSubidaCm;
    }

    public boolean ehCritico() {
        return Boolean.TRUE.equals(critico);
    }

    // ===== Getters e setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public TipoPonto getTipo() { return tipo; }
    public void setTipo(TipoPonto tipo) { this.tipo = tipo; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Boolean getCritico() { return critico; }
    public void setCritico(Boolean critico) { this.critico = critico; }

    public Double getLimiteChuvaMm() { return limiteChuvaMm; }
    public void setLimiteChuvaMm(Double limiteChuvaMm) { this.limiteChuvaMm = limiteChuvaMm; }

    public Double getLimiteNivelCm() { return limiteNivelCm; }
    public void setLimiteNivelCm(Double limiteNivelCm) { this.limiteNivelCm = limiteNivelCm; }

    public Double getLimiteSubidaCm() { return limiteSubidaCm; }
    public void setLimiteSubidaCm(Double limiteSubidaCm) { this.limiteSubidaCm = limiteSubidaCm; }
}
