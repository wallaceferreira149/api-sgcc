# Compliance e Segurança

> Status: draft | Ref: `AGENTS.md` — Segurança (alvo, ainda não implementado)

## Requisitos
- Acesso institucional `@fab.mil.br` + 2FA (alvo).
- RBAC com escopo organizacional (operador/chefe/comandante).
- Auditoria completa: quem criou/alterou/excluiu (`BaseEntityAudit` + `SpringSecurityAuditorAware`).
- Dados sensíveis (pessoal, missões): acesso mínimo necessário, sem exposição em logs.

## Estado atual
- Sem Spring Security; `SpringSecurityAuditorAware` retorna placeholder `1L`.
- Auditoria real pendente de implementação.

## Checklist por módulo
- [ ] Endpoints exigem autenticação
- [ ] Escopo organizacional validado no service layer
- [ ] Ações de chefia geram log de alteração
- [ ] Nenhum dado sensível em URL ou log
