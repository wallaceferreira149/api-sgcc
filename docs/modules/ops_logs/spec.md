# Spec — ops_logs (domínio)

> Status: draft | PRD: [PRD-001](../../prds/PRD-001-ops-logs.md) | Ref: `AGENTS.md` — Padrões de Implementação

## Entidades (estendem `BaseEntityAudit`)
- `OperationalLog` (`operational_logs`): operator (obrigatório), assistant, instructor, locale (obrigatório), instructionOrder (obrigatório), opsRole (obrigatório), isReal, opslogDate, quantityRegistred, `flightControl` (@Embedded).
- Auxiliares: `OpsUser`, `OpsLocale`, `OpsRole`, `InstructionOrder`, `AircraftType`.
- Regras: ctor `protected` p/ JPA + público com validação; getters públicos, setters privados com validação; `equals/hashCode` por ID ou chave de negócio.

## Regras de negócio
- RN-01: tripulação sem acúmulo de função no mesmo log (validar NPE quando assistant/instructor nulos).
- RN-02: quantidade > 0.
- RN-03: `opslogDate`, operator, locale, instructionOrder, opsRole obrigatórios.

## Value Objects
- `IFF.of(String)`: `^[0-7]{4}$`, `@Embeddable`, `Serializable`, `equals/hashCode/toString`.
- `FlightControl`, `FlightInfo`: documentar formato e validação aqui antes de codar.

## Dívida técnica conhecida
- `BaseEntityAudit.getCreatedBy` não compila; `OperationalLog` com ctor incompleto e campo fantasma `airCraftQuantity`.
