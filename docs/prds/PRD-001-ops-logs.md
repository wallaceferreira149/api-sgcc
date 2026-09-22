# PRD-001 — Ops Logs

> Status: draft | Dono: _preencher_ | Data: _preencher_ | Módulo: `ops_logs` | Spec: [modules/ops_logs/spec.md](../modules/ops_logs/spec.md)

## 1. Problema
Registros operacionais dispersos em Forms/planilhas, sem padronização nem visão gerencial por esquadrão.

## 2. Usuários
- Operador: lança/edita seus registros.
- Chefe de Seção: acompanha e valida o esquadrão.
- Comandante: consulta indicadores globais.

## 3. Metas
- 100% dos novos registros no SGCC.
- Consulta por esquadrão/período em baixa latência.

## 4. Requisitos funcionais
- [ ] RF-01: CRUD de `OperationalLog` (operador, localidade, OI, função, isReal, quantidade, data).
- [ ] RF-02: VO `IFF` validado (4 dígitos octais); demais VOs (`FlightControl`, `FlightInfo`) conforme spec.
- [ ] RF-03: Validação de tripulação (mesmo usuário não acumula funções no mesmo registro).
- [ ] RF-04: RBAC por escopo (operador/chefe/comandante).
- [ ] RF-05: Auditoria createdBy/updatedBy em todo registro.

## 5. Requisitos não-funcionais
- Erros em RFC ProblemDetail, mensagens pt-BR.
- Testes unitários de entidade + integração de persistência.

## 6. Fora de escopo
- Dashboards gerenciais (PRD futuro), Redis/cache, 2FA.

## 7. Critérios de aceite
- [ ] CRUD persiste em H2 (dev) e Postgres (prod) via Flyway.
- [ ] IFF inválido rejeitado com 400 + ProblemDetail.
- [ ] Operador não enxerga registro de outro escopo (teste).
