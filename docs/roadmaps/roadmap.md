# Roadmap SGCC

> Status: draft | Atualizar a cada PRD aprovado.

## Fase 1 — Fundação ops_logs (atual)
- [ ] Corrigir `BaseEntityAudit` + `OperationalLog` para compilar.
- [ ] Resolver duplicação `GlobalSGCCException` / handlers.
- [ ] Repository + service básico com validações.

## Fase 2 — API ops_logs
- [ ] DTOs + controller CRUD + ProblemDetail.
- [ ] RBAC por escopo no service + testes de isolamento.

## Fase 3 — Qualidade ops_logs
- [ ] Testes integração Postgres + Flyway.
- [ ] Doc OpenAPI. Critério: PRD-001 aceite verde.

## Próximos módulos (backlog)
- [ ] PRD-002: _definir próxima seção (administrativa/técnica)_.
- [ ] Transversais: Security + 2FA, Redis/cache, Docker/CI, Health Check.
