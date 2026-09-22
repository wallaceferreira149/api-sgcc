# Ambientes e Operação

> Ref: `src/main/resources/application*.yaml`

## Perfis
- `dev` (ativo por padrão): H2 `jdbc:h2:mem:devdb`, console `/h2-console`, `ddl-auto: create-drop`, Flyway off.
- `test`/`prod`: PostgreSQL + Flyway on (ver `application-test.yaml` / `application-prod.yaml`).

## Comandos
- `./mvnw test` — testes.
- `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` — rodar local.
- `./mvnw package` — build.
- H2 console: `http://localhost:8080/h2-console` (dev).

## Convenções
- Nunca commitar credencial; usar variável de ambiente por perfil.
- Migration nova a cada mudança de schema pós-fundação (dev usa `create-drop` só na concepção).
