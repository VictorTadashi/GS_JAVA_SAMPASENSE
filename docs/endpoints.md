# Tabela de endpoints — SampaSense API

Base local: `http://localhost:8080` · Documentação interativa: `/swagger-ui.html`

**Autenticação:** rotas marcadas com 🔓 são públicas. As demais exigem o cabeçalho `Authorization: Bearer {token}`, obtido em `POST /api/auth/login`.

## Autenticação

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| POST | `/api/auth/login` 🔓 | Login com e-mail e senha; devolve o token JWT | 200 |
| POST | `/api/usuarios` 🔓 | Cadastra um usuário (senha gravada com BCrypt) | 201 |

## Pontos monitorados — `/api/pontos`

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| GET | `/api/pontos` | Lista os pontos monitorados | 200 |
| GET | `/api/pontos/{id}` | Busca um ponto | 200 |
| POST | `/api/pontos` | Cadastra um ponto | 201 |
| PUT | `/api/pontos/{id}` | Atualiza um ponto | 200 |
| DELETE | `/api/pontos/{id}` | Remove um ponto (sem vínculos) | 204 |
| GET | `/api/pontos/{id}/risco` 🔓 | Nível atual, orientação e pontos seguros | 200 |
| GET | `/api/pontos/{id}/pontos-seguros?acessivel=true` 🔓 | Pontos seguros por distância | 200 |

## Sensores — `/api/sensores`

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| GET | `/api/sensores?pontoId=1` | Lista sensores (filtro opcional) | 200 |
| GET | `/api/sensores/{id}` | Busca um sensor | 200 |
| POST | `/api/sensores` | Cadastra um sensor | 201 |
| PUT | `/api/sensores/{id}` | Atualiza um sensor | 200 |
| DELETE | `/api/sensores/{id}` | Remove um sensor (sem leituras) | 204 |

## Leituras — `/api/leituras`

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| GET | `/api/leituras?sensorId=1` | Lista leituras (filtro opcional) | 200 |
| GET | `/api/leituras/{id}` | Busca uma leitura | 200 |
| POST | `/api/leituras` | Registra leitura **e roda o motor de risco** | 201 |
| DELETE | `/api/leituras/{id}` | Remove uma leitura | 204 |

## Eventos de alagamento — `/api/eventos`

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| GET | `/api/eventos?status=ABERTO` | Lista eventos (filtro opcional) | 200 |
| GET | `/api/eventos/{id}` | Busca um evento | 200 |
| PUT | `/api/eventos/{id}/encerrar` | Encerramento manual pelo operador | 200 |
| GET | `/api/eventos/{id}/alertas` | Alertas disparados no evento | 200 |
| GET | `/api/eventos/{id}/confirmacoes` | Confirmações dos moradores | 200 |
| POST | `/api/eventos/{id}/confirmacoes` 🔓 | Morador confirma `ALAGOU` ou `BAIXOU` | 201 |

## Alertas — `/api/alertas` (somente leitura)

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| GET | `/api/alertas?eventoId=1` | Histórico de alertas | 200 |
| GET | `/api/alertas/{id}` | Busca um alerta | 200 |

## Bloqueios — `/api/bloqueios`

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| GET | `/api/bloqueios?status=PENDENTE` | Lista recomendações | 200 |
| GET | `/api/bloqueios/{id}` | Busca uma recomendação | 200 |
| PUT | `/api/bloqueios/{id}/confirmar` | Operador confirma o fechamento | 200 |
| PUT | `/api/bloqueios/{id}/recusar` | Operador recusa o fechamento | 200 |

## Pontos seguros — `/api/pontos-seguros`

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| GET | `/api/pontos-seguros` | Lista pontos seguros | 200 |
| GET | `/api/pontos-seguros/{id}` | Busca um ponto seguro | 200 |
| POST | `/api/pontos-seguros` | Cadastra um ponto seguro | 201 |
| PUT | `/api/pontos-seguros/{id}` | Atualiza um ponto seguro | 200 |
| DELETE | `/api/pontos-seguros/{id}` | Remove um ponto seguro | 204 |

## Códigos de erro (camada de exceções)

| Status | Quando |
|---|---|
| 400 | Campos inválidos, JSON malformado ou valor de enum inexistente |
| 401 | Token ausente, inválido ou expirado, ou login com e-mail/senha errados |
| 404 | Registro ou endpoint inexistente |
| 409 | Remoção de registro com vínculos / violação de restrição do banco |
| 422 | Regra de negócio violada (ex.: sensor inativo, evento encerrado, bloqueio já decidido) |
| 500 | Erro inesperado |
