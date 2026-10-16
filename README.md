# CP05-SOA-Auto-Escola-3ESA

# Integrantes:

- Augusto Ferreira Rogel de Souza / RM 557709
- Heitor Prestes / RM 554823



## Checkpoint 5 – o que foi adicionado

### 1. Consumo de API externa (ViaCEP)
A API consome o serviço público [ViaCEP](https://viacep.com.br) para consultar endereços por CEP.

- Endpoint: `GET /enderecos/cep/{cep}` (aceita com ou sem hífen; exige token JWT)
- Retorna o endereço no mesmo formato (`DadosEndereco`) usado no cadastro de alunos e instrutores
- Respostas: `200` (encontrado), `400` (CEP sem 8 dígitos), `404` (CEP inexistente), `503` (ViaCEP fora do ar)
- Código: `infra/externo/viacep` (cliente `RestClient`, com timeout configurável), `service/EnderecoService` e `controller/EnderecoController`
- Configuração em `application.properties`: `viacep.url` e `viacep.timeout-ms`

### 2. Documentação com Swagger (OpenAPI)
Com a aplicação rodando (porta 8085):

- Swagger UI: http://localhost:8085/swagger-ui.html
- OpenAPI (JSON): http://localhost:8085/v3/api-docs

Para testar os endpoints protegidos: faça `POST /login` (usuário inicial `admin` / `admin123`), copie o `tokenJWT` e clique em **Authorize** informando apenas o token.

### 3. CORS
Configurado em `infra/cors/CorsConfig` e ativado no `SecurityConfig`. As origens permitidas ficam em `api.cors.allowed-origins` (por padrão `localhost:3000`, `4200` e `5173`). Métodos liberados: GET, POST, PUT, DELETE e OPTIONS (preflight).

### 4. Testes automatizados
Rode com `./mvnw test`. Os testes abaixo **não precisam de banco de dados**:

| Entidade | Entidade (domínio) | Service | Controller |
|---|---|---|---|
| Aluno | `AlunoTest` | `AlunoServiceTest` | `AlunoControllerTest` |
| Instrutor | `InstrutorTest` | `InstrutorServiceTest` | `InstrutorControllerTest` |
| Usuário | `UsuarioTest` | `UsuarioServiceTest` | `UsuarioControllerTest` |
| Instrução | `InstrucaoTest` | `AgendaDeInstrucoesTest` | `InstrucaoControllerTest` |
| Endereço/CEP | – | `EnderecoServiceTest` | `EnderecoControllerTest`, `ViaCepClientTest` |

Observação: `AutoEscola3EsaApplicationTests` (já existente) sobe o contexto completo e por isso exige o MySQL configurado em `application.properties`.
