# API — ops_logs

> Status: draft | Spec: [spec.md](spec.md)

## Base
- Base path sugerido: `/api/ops-logs`. JSON, pt-BR nas mensagens.

## Endpoints (alvo)
| Método | Rota | Papel/escopo | Resposta |
|--------|------|--------------|----------|
| POST | `/api/ops-logs` | operador (próprio escopo) | 201 + DTO |
| GET | `/api/ops-logs/{id}` | operador/chefe/comandante (escopo) | 200 / 404 ProblemDetail |
| GET | `/api/ops-logs?esquadrao=&de=&ate=` | chefe (esquadrão), comandante (global) | 200 lista paginada |
| PUT | `/api/ops-logs/{id}` | operador (dono) / chefe (esquadrão) | 200 / 403 |
| DELETE | `/api/ops-logs/{id}` | chefe (esquadrão) | 204 / 403 |

## DTOs
- Request: `operadorId, assistenteId?, instrutorId?, localidadeId, ordemInstrucaoId, funcaoId, isReal, data, quantidade, flightControl?, iff?`.
- Response: mesmos + `id, createdAt, createdBy, updatedAt, updatedBy`.

## Erros
- RFC ProblemDetail; 400 validação (ex: IFF inválido), 403 fora do escopo, 404 inexistente.
