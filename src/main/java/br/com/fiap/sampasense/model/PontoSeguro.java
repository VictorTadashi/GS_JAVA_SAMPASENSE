package br.com.fiap.sampasense.model;

import br.com.fiap.sampasense.model.enums.TipoPontoSeguro;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Local alto parceiro (escola, UBS, comércio) que acolhe quem precisa sair da rua. */
@Entity
@Table(name = "TB_SS_PONTO_SEGURO")
public class PontoSeguro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PONTO_SEGURO")
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    @Column(name = "NM_PONTO_SEGURO", nullable = false, length = 100)
    private String nome;

    @NotNull(message = "O tipo é obrigatório (ESCOLA, UBS, COMERCIO ou OUTRO)")
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_PONTO_SEGURO", nullable = false, length = 20)
    private TipoPontoSeguro tipo;

    @NotBlank(message = "O endereço é obrigatório")
    @Size(max = 150, message = "O endereço deve ter no máximo 150 caracteres")
    @Column(name = "DS_ENDERECO", nullable = false, length = 150)
    private String endereco;

    @NotNull(message = "A latitude é obrigatória")
    @Column(name = "NR_LATITUDE", nullable = false)
    private Double latitude;

    @NotNull(message = "A longitude é obrigatória")
    @Column(name = "NR_LONGITUDE", nullable = false)
    private Double longitude;

    /** Acesso sem escadas nem ladeiras íngremes: prioritário para idosos e PcD. */
    @NotNull(message = "Informe se o local é acessível")
    @Column(name = "ST_ACESSIVEL", nullable = false)
    private Boolean acessivel;

    @PositiveOrZero(message = "A capacidade não pode ser negativa")
    @Column(name = "NR_CAPACIDADE")
    private Integer capacidade;

    @NotNull(message = "Informe o ponto monitorado atendido. Ex.: \"pontoMonitorado\": {\"id\": 1}")
    @ManyToOne
    @JoinColumn(name = "ID_PONTO", nullable = false)
    private PontoMonitorado pontoMonitorado;

    public PontoSeguro() {
    }

    public PontoSeguro(String nome, TipoPontoSeguro tipo, String endereco, Double latitude, Double longitude,
                       Boolean acessivel, Integer capacidade, PontoMonitorado pontoMonitorado) {
        this.nome = nome;
        this.tipo = tipo;
        this.endereco = endereco;
        this.latitude = latitude;
        this.longitude = longitude;
        this.acessivel = acessivel;
        this.capacidade = capacidade;
        this.pontoMonitorado = pontoMonitorado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public TipoPontoSeguro getTipo() { return tipo; }
    public void setTipo(TipoPontoSeguro tipo) { this.tipo = tipo; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Boolean getAcessivel() { return acessivel; }
    public void setAcessivel(Boolean acessivel) { this.acessivel = acessivel; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public PontoMonitorado getPontoMonitorado() { return pontoMonitorado; }
    public void setPontoMonitorado(PontoMonitorado pontoMonitorado) { this.pontoMonitorado = pontoMonitorado; }
}
