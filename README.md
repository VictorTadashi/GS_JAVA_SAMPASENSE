# SampaSense API

**Alerta de alagamento e rota segura em tempo real para quem passa por vales e túneis de São Paulo.**

FIAP · Global Solution 2026.2 · CitizenTech · Engenharia de Software (2º ano) · Disciplina: Domain Driven Design – Java

---

## Ficha da CitizenTech

| Campo | Descrição |
|---|---|
| **Nome** | SampaSense |
| **Cidade e bairro** | São Paulo, em um ponto real com histórico de alagamento (vale ou túnel) |
| **Cidadão beneficiado** | Pedestres, motoristas, moradores e comerciantes que passam por vales e túneis que alagam, com atenção especial a idosos e pessoas com deficiência |
| **Problema, em uma frase** | Em dias de chuva forte, quem passa por vales e túneis é pego de surpresa pela água subindo em minutos, porque os alertas atuais são genéricos e não dizem o que fazer naquele ponto |
| **Como funciona** | Sensores medem a chuva e o nível da água; o sistema estima o risco, dispara alertas locais em vários canais e indica o ponto seguro mais próximo |
| **O que cada parte faz** | Java: API central e motor de risco · Database Design: modelo Oracle · Data Science: modelos de previsão · Dynamic Programming: rota segura (grafos) · Network: rede segmentada · Agile: design da solução · AR/VR: simulação 3D |
| **Link do repositório** | https://github.com/VictorTadashi/GS_Java_SampaSense |
| **Indicador de impacto** | Tempo entre detecção e alerta; pessoas alcançadas no raio do ponto; veículos impedidos de entrar na área; taxa de falsos alarmes |
| **ODS conectados** | ODS 11, ODS 13, ODS 9 e ODS 3 |

## Integrantes

| Nome completo | RM |
|---|---|
| Anna Clara Ruggeri da Silva | RM565553 |
| Eduardo Viudes Chorro | RM564075 |
| Giovanna da Silva Santos | RM566301 |
| Giovanna Luiza Bento | RM563203 |
| Victor Tadashi Saito Barra | RM563582 |

## Links

- **Deploy (Render):** https://gs-java-sampasense.onrender.com
- **Swagger (documentação interativa):** https://gs-java-sampasense.onrender.com/swagger-ui.html
- **GitHub:** https://github.com/VictorTadashi/GS_Java_SampaSense

> O deploy usa o plano gratuito do Render. Após um período sem uso o serviço "dorme", e a **primeira requisição pode levar cerca de 2 minutos** para responder. As seguintes são rápidas.

---

## Descrição da solução

A API é o núcleo do SampaSense. Ela recebe as medições dos sensores instalados em postes e totens, avalia o risco de alagamento de cada ponto e dispara os alertas certos para cada nível. Também controla a decisão de fechar semáforos e informa os pontos seguros para onde o cidadão deve ir.

### Motor de risco (regras de domínio)

| Nível | Gatilho | Ações |
|---|---|---|
| **ATENÇÃO** | Pluviômetro com chuva acumulada ≥ limite do ponto | WhatsApp, SMS e painel do totem (amarelo) |
| **ALERTA** | Água subindo rápido (≥ limite de subida entre duas leituras) ou um sensor acima do limite de nível | Painel vermelho, sirene, Wi-Fi em modo emergência, pedido de Cell Broadcast |
| **BLOQUEIO** | Dois sensores de nível acima do limite (confirmação dupla) | Recomendação de fechar semáforos: **PENDENTE** para o operador da CET/CGE, ou **AUTOMÁTICA** em túnel crítico |

Regras complementares:

- O nível de um evento **só sobe**. Cada subida registra os alertas do novo nível.
- Confirmação de morador **ALAGOU** sobe um evento de ATENÇÃO para ALERTA. Em ALERTA, com um sensor acima do limite, sobe para BLOQUEIO (sensor + cidadão).
- Confirmação de morador **BAIXOU**, com todos os sensores abaixo do limite, encerra o evento.
- **Ninguém dispara alertas manualmente.** Não existe POST de alerta nem de evento: só o motor de risco cria.
- **Privacidade por design:** sensores enviam só números, e as confirmações são anônimas.

### Segurança (Spring Security + JWT)

A API usa autenticação por **token JWT** (padrão da Aula 18): o usuário faz login, recebe um token e o envia no cabeçalho `Authorization: Bearer {token}`.

| Acesso | Rotas |
|---|---|
| **Público** (cidadão, sem login) | `GET /api/pontos/{id}/risco`, `GET /api/pontos/{id}/pontos-seguros`, `POST /api/eventos/{id}/confirmacoes`, `POST /api/auth/login`, `POST /api/usuarios`, Swagger |
| **Com token** (operador, administrador e sensores) | Todas as demais rotas |

- As senhas são gravadas com **BCrypt** e nunca aparecem nas respostas.
- O token vale **2 horas** e é assinado com HS256. A chave fica na variável `JWT_SECRET` (há um valor padrão para desenvolvimento).
- Sem token, ou com token inválido, a API responde **401** no mesmo formato JSON dos demais erros.

**Usuário de demonstração** (criado automaticamente na inicialização):

| E-mail | Senha |
|---|---|
| `operador@sampasense.com` | `Sampa@2026` |

**Como usar no Swagger:** execute `POST /api/auth/login` com o usuário acima, copie o `token` da resposta, clique em **Authorize** (cadeado no topo), cole o token e confirme.

### Simplificações do protótipo

- A previsão usa **limiares fixos por ponto**. O modelo estatístico (regressão) é desenvolvido em Data Science e pode substituí-los.
- O envio real (SMS, sirene, Cell Broadcast, semáforo) é **simulado**: cada alerta é registrado no banco.
- A rota completa, desviando de trechos alagados, é calculada em Dynamic Programming (grafos). A API devolve os pontos seguros ordenados por distância, com filtro de acessibilidade.
- **Todos os dados são simulados.** Não há nenhum dado pessoal real.
- O cadastro de usuários (`POST /api/usuarios`) é público, como no exemplo visto em aula. Em produção, seria restrito a administradores.

---

## Arquitetura e estrutura de pastas

```
sampasense-api/
├── database/ddl.sql              # Script Oracle (mesmo modelo de Database Design)
├── docs/
│   ├── endpoints.md              # Tabela de endpoints (para o PDF)
│   ├── diagrama-classes.md       # Diagrama de classes em Mermaid
│   └── requisicoes.http          # Roteiro de testes/demonstração (com login)
├── Dockerfile                    # Build e execução no Render
├── pom.xml
└── src/main/java/br/com/fiap/sampasense/
    ├── model/                    # Entidades JPA com as regras do domínio
    │   └── enums/                # NivelRisco, CanalAlerta, StatusBloqueio...
    ├── dao/                      # Acesso a dados (Spring Data JPA)
    ├── bo/                       # Regras de negócio (AvaliacaoRiscoBO = motor de risco)
    ├── controller/               # Endpoints REST (GET, POST, PUT, DELETE)
    ├── exception/                # Exceções de domínio + GlobalExceptionHandler
    ├── dto/                      # Objetos de resposta/requisição específicos
    ├── config/                   # Segurança JWT, Swagger e carga de dados simulados
    └── util/                     # Cálculo de distância (Haversine)
```

Fluxo de uma requisição: `Controller → BO → DAO → Oracle`. Erros em qualquer camada viram uma resposta JSON padronizada no `GlobalExceptionHandler`.

| Camada | Pasta | Responsabilidade |
|---|---|---|
| Model | `model/` | 9 entidades (8 do domínio + `Usuario`) mapeadas nas tabelas `TB_SS_*` e métodos de domínio (`escalarPara`, `confirmar`, `nivelAcimaDoLimite`...) |
| DAO | `dao/` | Interfaces `JpaRepository`. O Spring Data gera o SQL a partir do nome dos métodos. |
| BO | `bo/` | Validações e regras de negócio. `AvaliacaoRiscoBO` concentra o motor de risco. |
| Controller | `controller/` | Rotas REST e códigos HTTP |
| Exceções | `exception/` | `RecursoNaoEncontradoException` (404), `RegraNegocioException` (422), validação (400), login inválido (401), integridade (409) |
| Segurança | `config/` | `SecurityConfig` (regras de acesso, BCrypt, geração e validação do JWT) e `OpenApiConfig` (botão Authorize do Swagger) |

---

## Tecnologias e versões

- Java JDK **21**
- Spring Boot **3.3.5** (Web, Data JPA, Validation, Security)
- Spring Security + OAuth2 Resource Server (**JWT**)
- Banco **Oracle** (servidor FIAP) com driver `ojdbc11`
- Maven 3.9+
- springdoc-openapi 2.6.0 (Swagger UI)
- Docker (deploy no Render)
- IDE: IntelliJ IDEA

## Como executar localmente

1. Clone o repositório e abra a pasta no **IntelliJ IDEA**. O Maven baixa as dependências sozinho.
2. Configure as variáveis de ambiente em *Run → Edit Configurations → Environment variables*:
   ```
   DB_USERNAME=rmXXXXXX
   DB_PASSWORD=sua_senha_oracle
   ```
   Opcional: `DB_URL` (padrão `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`).
3. (Opcional) Execute `database/ddl.sql` no SQL Developer. Se não executar, a API cria as tabelas sozinha.
4. Rode `SampaSenseApplication`. Na primeira execução, dados simulados são carregados: 3 pontos, 9 sensores, 6 pontos seguros e o usuário de demonstração.
5. Acesse `http://localhost:8080/swagger-ui.html`, faça login em `POST /api/auth/login` e clique em **Authorize** com o token recebido.

Pelo terminal: `mvn spring-boot:run` (com as variáveis exportadas).

## Deploy no Render

A aplicação está publicada em **https://gs-java-sampasense.onrender.com**. Para reproduzir o deploy:

1. No Render, crie um serviço em **New → Web Service** e conecte o repositório `GS_Java_SampaSense` (branch `main`).
2. Em **Language**, escolha **Docker** (o `Dockerfile` da raiz é detectado). Plano: Free. Região: Ohio (US East).
3. Em **Environment Variables**, cadastre `DB_USERNAME` e `DB_PASSWORD` (e `DB_URL`, se for diferente do padrão). Recomendado: `JWT_SECRET` com uma chave Base64 de 32 bytes.
4. Clique em **Deploy Web Service** e aguarde o status **Live**.
5. Teste em `https://gs-java-sampasense.onrender.com/swagger-ui.html`.

> O plano gratuito "dorme" sem uso. A primeira requisição depois disso pode levar cerca de 2 minutos.

## Endpoints

Tabela completa em [`docs/endpoints.md`](docs/endpoints.md). Principais:

| Método | Endpoint | Função |
|---|---|---|
| POST | `/api/auth/login` | Login: devolve o token JWT (pública) |
| POST | `/api/usuarios` | Cadastra um usuário (pública) |
| POST | `/api/leituras` | Registra uma medição e roda o motor de risco |
| GET | `/api/pontos/{id}/risco` | Nível atual, orientação e pontos seguros |
| GET | `/api/pontos/{id}/pontos-seguros?acessivel=true` | Pontos seguros por distância |
| POST | `/api/eventos/{id}/confirmacoes` | Morador confirma `ALAGOU` ou `BAIXOU` |
| PUT | `/api/bloqueios/{id}/confirmar` | Operador fecha os semáforos |
| CRUD | `/api/pontos`, `/api/sensores`, `/api/pontos-seguros` | Cadastros |
