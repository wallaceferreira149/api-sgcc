# Arquitetura — Visão Geral

> Status: draft | Ref: `AGENTS.md`

- Monólito modular: `dev.arcanus.api_sgcc` com `domain/`, `application/`, `infra/` + `modules/<nome>/{entities,value_objects,repositories,services,controllers,dtos}`.
- Comunicação entre módulos via REST com contratos claros; sem microsserviços por enquanto.
- Stack: Java 26, Spring Boot 4.1.1, JPA + Validation, Flyway + PostgreSQL (prod/test), H2 (dev, `create-drop`, Flyway off).
- Perfis: `application.yaml` + `application-{dev,test,prod}.yaml`. Ver [operations/environments](../operations/environments.md).
- Transversais (alvo): auth @fab.mil.br + 2FA, RBAC por escopo, auditoria, ProblemDetail, testes por módulo.
