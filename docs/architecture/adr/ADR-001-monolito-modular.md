# ADR-001 — Monólito Modular em vez de Microsserviços

> Status: aceito | Data: _preencher_ | Contexto: início do SGCC, time pequeno, entrega incremental

## Contexto
Precisamos evoluir por seções (módulos) sem custo operacional de distribuído.

## Decisão
Monólito modular com desacoplamento lógico por módulo e comunicação via REST.

## Consequências
- Pró: deploy simples, refatoração fácil, testes integrados.
- Contra: acoplamento acidental se importar classes entre módulos; escalar é por inteiro.
- Salvaguardas: nada de `import` direto entre `modules/*`; contrato via API/DTO; CI roda `./mvnw test`.
