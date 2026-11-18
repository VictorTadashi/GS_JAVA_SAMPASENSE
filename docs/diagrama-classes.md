# Diagrama de classes — SampaSense API

> Renderize no GitHub (Mermaid nativo) ou em https://mermaid.live e exporte como imagem para o PDF.

## Camada de domínio (model)

```mermaid
classDiagram
    direction LR

    class PontoMonitorado {
        -Long id
        -String nome
        -TipoPonto tipo
        -String bairro
        -Double latitude
        -Double longitude
        -Boolean critico
        -Double limiteChuvaMm
        -Double limiteNivelCm
        -Double limiteSubidaCm
        +chuvaAcimaDoLimite(double) boolean
        +nivelAcimaDoLimite(double) boolean
        +subidaRapida(double) boolean
        +ehCritico() boolean
    }

    class Sensor {
        -Long id
        -String codigo
        -TipoSensor tipo
        -StatusSensor status
        -LocalDate dataInstalacao
        +estaAtivo() boolean
    }

    class Leitura {
        -Long id
        -Double valor
        -LocalDateTime dataHora
    }

    class EventoAlagamento {
        -Long id
        -NivelRisco nivel
        -StatusEvento status
        -LocalDateTime dataInicio
        -LocalDateTime dataFim
        +abrir(PontoMonitorado)$ EventoAlagamento
        +estaAberto() boolean
        +escalarPara(NivelRisco) boolean
        +encerrar() void
    }

    class Alerta {
        -Long id
        -CanalAlerta canal
        -NivelRisco nivel
        -String mensagem
        -LocalDateTime dataHora
    }

    class ConfirmacaoCidada {
        -Long id
        -TipoConfirmacao tipo
        -LocalDateTime dataHora
    }

    class PontoSeguro {
        -Long id
        -String nome
        -TipoPontoSeguro tipo
        -String endereco
        -Double latitude
        -Double longitude
        -Boolean acessivel
        -Integer capacidade
    }

    class RecomendacaoBloqueio {
        -Long id
        -StatusBloqueio status
        -String operador
        -LocalDateTime dataCriacao
        -LocalDateTime dataDecisao
        +para(EventoAlagamento)$ RecomendacaoBloqueio
        +estaPendente() boolean
        +confirmar(String) void
        +recusar(String) void
    }

    class Usuario {
        -Long id
        -String nome
        -String email
        -String senha
    }

    class NivelRisco {
        <<enumeration>>
        NORMAL
        ATENCAO
        ALERTA
        BLOQUEIO
        +getCanais() List~CanalAlerta~
        +getOrientacao() String
        +maiorQue(NivelRisco) boolean
        +mensagemPara(String) String
    }

    Sensor "*" --> "1" PontoMonitorado
    Leitura "*" --> "1" Sensor
    EventoAlagamento "*" --> "1" PontoMonitorado
    Alerta "*" --> "1" EventoAlagamento
    ConfirmacaoCidada "*" --> "1" EventoAlagamento
    RecomendacaoBloqueio "*" --> "1" EventoAlagamento
    PontoSeguro "*" --> "1" PontoMonitorado
    EventoAlagamento ..> NivelRisco
    Alerta ..> NivelRisco
```

Demais enums: `TipoPonto`, `TipoSensor`, `StatusSensor`, `StatusEvento`, `CanalAlerta`,
`TipoConfirmacao`, `TipoPontoSeguro`, `StatusBloqueio`.

## Camadas (fluxo de uma leitura)

```mermaid
classDiagram
    direction LR
    class LeituraController {
        +registrar(Leitura) ResponseEntity
    }
    class LeituraBO {
        +registrar(Leitura) ResultadoLeituraResponse
    }
    class AvaliacaoRiscoBO {
        +avaliarLeitura(Leitura, Leitura) EventoAlagamento
        +escalarEvento(EventoAlagamento, NivelRisco) EventoAlagamento
        +contarSensoresNivelAcimaDoLimite(PontoMonitorado) long
        +consultarRisco(Long) RiscoResponse
    }
    class EventoAlagamentoBO
    class AlertaBO {
        +dispararAlertas(EventoAlagamento, NivelRisco) List~Alerta~
    }
    class RecomendacaoBloqueioBO {
        +gerarRecomendacao(EventoAlagamento) RecomendacaoBloqueio
    }
    class LeituraDAO {
        <<interface>>
    }
    class SensorDAO {
        <<interface>>
    }
    class GlobalExceptionHandler

    LeituraController --> LeituraBO
    LeituraBO --> LeituraDAO
    LeituraBO --> AvaliacaoRiscoBO
    AvaliacaoRiscoBO --> SensorDAO
    AvaliacaoRiscoBO --> EventoAlagamentoBO
    AvaliacaoRiscoBO --> AlertaBO
    AvaliacaoRiscoBO --> RecomendacaoBloqueioBO
    GlobalExceptionHandler ..> LeituraController : trata exceções
```
