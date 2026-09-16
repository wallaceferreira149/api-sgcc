# claude.md - SGCC: Documento Base para Desenvolvimento de Módulos

## Visão Geral do Projeto SGCC

**SGCC** (Sistema de Gerenciamento do GCC) é um sistema Java/Spring Boot modular criado para digitalizar e centralizar trâmites administrativos, operacionais e técnicos do Centro de Controle de Espaço Aéreo (GCC). O sistema será entregue de forma incremental, com foco em Clean Code, arquitetura modular e boas práticas de mercado.

### Objetivo Estratégico
Digitalizar processos burocráticos manuais para prover **visão gerencial em tempo real** aos comandantes e chefes das divisões (operacional, administrativa e técnica), eliminando a dependência de formulários externos e planilhas (atualmente mais de 3000 registros em Google Forms).

#### Objetivo secundário
A construção do sistema está sendo realizada por um desenvolvedor inesperiente. Sua função será atuar tanto como um professor, quando solicitado, quanto um pair programmer. Os padrões buscados são os usuais do mercado para sistemas de gerenciamentos genéricos. Quando for solicitado para explicar um assunto, aborde com linguagem simples, apresentando o fluxo dentro de um processo de desenvolvimento, quais são as utilizações práticas no mercado, exemplos de código, resumo como fechamento do conhecimento. Utilize formato markdown para que o conhecimento possa ser traduzido em nota para o obisidian.

### Principais Stakeholders
- **Comandantes** do 1º GCC e dos Esquadrões (1º ao 5º/1º GCC): necessidade de indicadores globais de prontidão e andamento
- **Chefes de Seção Operacional**: controle local e gestão dos dados de sua unidade
- **Operadores/Controladores de Voo**: responsáveis pelo lançamento dos registros
- **Divisões de Administrativa e Técnica**: suporte e compliance

### Restrições de Compliance
- Tratamento de dados sensíveis (pessoais e de missões)
- Rigoroso controle de acesso e auditoria
- Acesso institucional @fab.mil.br com suporte a 2FA

---

## Requisitos Arquiteturais Gerais

### Modularidade (Monólito Modularizado)
- **Desacoplamento lógico** entre os módulos das seções
- Cada módulo evolui de forma independente sem a complexidade inicial de microsserviços
- Comunicação entre módulos via **API REST** com contratos claros
- Deploy em ambientes isolados (Dev, Test, Prod) via variáveis de ambiente (.env)
- Deploy em containers Docker com pipeline CI/CD

### Segurança (Cross-Cutting)
- **Autenticação**: e-mail institucional @fab.mil.br + suporte a 2FA
- **Autorização (RBAC)**: Controle de acesso baseado em papéis (comandante, chefe, operador) com **escopo organizacional**
- Acesso limitado conforme a unidade do militar (operador vê só o seu, chefe de seção vê o esquadrão, comandante vê globalmente)
- Auditoria completa de quem criou/alterou excluiu registros

### Performance e Escalabilidade
- Baixa latência para consultas gerenciais
- Preparada para escala horizontal via arquitetura *stateless*
- Cache (Redis) para resultados de consultas pesadas e dashboards
- Health Check dos módulos e dependências (banco de dados, Redis)

### Qualidade de Código
- **Clean Architecture** e princípios **SOLID**
- Padrões consistentes de entidade, VO, repository, service, controller
- Clean Code: nomes significativos, métodos focados, responsabilidade única
- Testes unitários e de integração em todos os módulos

---

## Padrões de Implementação (Base para Todos os Módulos)

### 1. Estrutura de Entidades JPA
Todo novo módulo deve seguir o padrão estabelecido:

- **BaseEntity** (`domain/entities/BaseEntity`): `@MappedSuperclass` com `id` (IDENTITY)
- **BaseEntityAudit** (`domain/entities/BaseEntityAudit`): Extende BaseEntity + , `createdAt`, `createdBy`, `updatedAt`, `updatedBy` + `AuditingEntityListener`
- **Entidades específicas**: Entidades ricas, Construtor vazio `protected`, validação em setters, getters completos, `equals()`/`hashCode()` baseado em ID ou chave de negócio

### 2. Value Objects
- Utilizar `@Embeddable` para valores complexos
- Validação no constructor/factory method
- Padrão `of(String code)` + constructor protegido
- Implementar `Serializable`, `equals`, `hashCode`, `toString`
- Exemplo: `IFF` no módulo ops_logs (código octal de 4 dígitos)

### 3. Tratamento de Erros
- Formato de erro via **RFC ProblemDetail**
- Exceção base: `GlobalSGCCException` com método `toProblemDetail()`
- Manipulador global: `@RestControllerAdvice` que trata `GlobalSGCCException`
- Mensagens consistentes e amigáveis ao usuário

### 4. Auditoria
- Campos `createdBy`, `updatedBy` presentes em todas as entidades
- `SpringSecurityAuditorAware` deve ser implementado para capturar usuário real
- Atualmente retorna placeholder; deve ser conectado ao Spring Security logado
- Logs de alteração obrigatórios para ações de chefia

### 5. Pacote de Tests
- JUnit 5 + Assertions
- Tests de criação e validação de regras de negócio
- Tests de integração para persistência no PostgreSQL
- Tests de RBAC/escopo organizacional

---

## Padrões de Conveniência

### Nomenclatura de Pacotes
```
dev.arcanus.api_sgcc              # raiz
dev.arcanus.api_sgcc.domain       # entidades domain
dev.arcanus.api_sgcc.application  # camada de aplicação global
dev.arcanus.api_sgcc.infra        # camada de infraestrutura global
dev.arcanus.api_sgcc.modules      # módulos do sistema
  └── {module-name}/
      ├── entities/               # entidades JPA do módulo
      ├── value_objects/          # value objects do módulo
      ├── repositories/           # interfaces JpaRepository
      ├── services/               # lógica de negócio
      ├── controllers/            # endpoints REST
      └── dtos/                   # data transfer objects
```

### Padrão de Entidade
```java
@Entity
@Table(name = "{tabela_lowercase}")
public class {Entidade} extends BaseEntityAudit {

    // Atributos do domínio

    // Construtores protegidos com validação
    protected {Entidade}() {}

    public {Entidade}({atributos}) {
        // validações e atribuições
    }

    // Getters públicos
    // Setters privados com validação

    // equals()/hashCode()
    // Métodos de negócio (updateDetails, etc.)
}
```

### Padrão de Value Object
```java
@Embeddable
public class {VO} implements Serializable {

    private static final Pattern PATTERN = Pattern.compile("...");
    private static final long serialVersionUID = 1L;

    private String campo;

    protected VO() {}

    public static {VO} of(String codigo) {
        validateCodigo(codigo);
        return new {VO}(codigo);
    }

    private static void validateCodigo(String codigo) {
        if (codigo == null) throw new IllegalArgumentException("...");
        if (!PATTERN.matcher(codigo).matches()) {
            throw new IllegalArgumentException("...");
        }
    }

    public String getCampo() { return campo; }

    // equals(), hashCode(), toString()
}
```

---

## Estratégia de Desenvolvimento por Módulo

Cada novo módulo segue esta progressão:

### Fase 1: Fundação (Sprint 1)
1. Criar entidade principal estendendo BaseEntityAudit
2. Criar value objects necessários
3. Criar repository básico (JpaRepository)
4. Criar service básico com lógica de validação

### Fase 2: Interface (Sprint 2)
1. Criar DTOs de request/response
2. Criar controller REST com endpoints CRUD
3. Implementar validação de escopo RBAC no service layer

### Fase 3: Qualidade (Sprint 3)
1. Tests unitários para service e repository
2. Tests de integração para persistência
3. Tests de isolamento por escopo organizacional
4. Documentação de API (specificações OpenAPI/JSON)

---

## Como Este Arquivo Será Usado

Este `CLAUDE.md` serve como:

1. **Guia de referência rápido** - padrões, convenções e estruturas padrões
2. **Base para especificação de novos módulos** - cada feature tem sua spec própria
3. **Contexto de decisões arquiteturais** - por que certeiras escolhas foram feitas
4. **Memória de sessão** - o que já foi estabelecido para consistência

**Fluxo recomendado para cada nova feature:**
1. Ler este arquivo para entender padrões vigentes
2. Criar spec específica para o módulo em questão (este documento serve de base)
3. Implementar seguindo os padrões descritos
4. Manter este arquivo atualizado com novos padrões descobertos

---

## Referências Internas

- **Padrões de Entidade**: `domain/entities/BaseEntity.java`, `domain/entities/BaseEntityAudit.java`
- **Value Object Exemplos**: `modules/ops_logs/value_objects/IFF.java`
- **Entidades do Módulo OpsLogs**: `modules/ops_logs/entities/OperationalLog.java`, `InstructionOrder.java`, `OpsRole.java`, `OpsUser.java`, `OpsLocale.java`
- **Tratamento de Erro**: `application/exceptions/GlobalSGCCException.java`, `infra/config/exception/GlobalExceptionHandler.java`
- **Auditoria**: `application/config/SpringSecurityAuditorAware.java`
- **Tests**: `src/test/java/dev/arcanus/api_sgcc/modules/ops_logs/entities`

---

**Arquivo criado para subsidiar o desenvolvimento modular do SGCC.** Este documento será a base sobre a qual cada novo módulo construirá sua própria especificação detalhada, mantendo consistência em todo o projeto.

## Padrões e Convenções Observados no Projeto

### 1. Estrutura de Entidades JPA

O projeto segue padrões JPA com:

- **BaseEntity**: `@MappedSuperclass` com `id` (IDENTITY)
- **BaseEntityAudit**: Extende BaseEntity + `createdAt`, `createdBy`, `updatedAt`, `updatedBy` + adiciona auditing do Spring Data (`AuditingEntityListener`)
- **Entidades específicas**: Cada uma tem construtores `protected`, validação em setters, getters completos, `equals()`/`hashCode()`

**Exemplo visto**: `OpsRole`, `InstructionOrder`, `OpsLocale` seguem esse padrão consistente.

### 2. Value Objects

- **IFF**: `@Embeddable`, validação de código octal de 4 dígitos no constructor/validator, pattern `"^[0-7]{4}$"`
- Padrão fábrica `of(String code)` + constructor protegido
- Implementa `Serializable`, `equals`, `hashCode`, `toString`

### 3. Tratamento de Erros
- O padrão de formato de erros será através do RFC ProblemDetail
- `GlobalSGCCException` - exceção base, tem método `toProblemDetail()`
- `GlobalExceptionHandler` (infra) - `@RestControllerAdvice` que trata `GlobalSGCCException`
- `SGCCGlobalExceptionHandler` (application) - versâo antiga, TODO para log de erro

### 4. Auditoria

- `SpringSecurityAuditorAware` - quase implementado, atualmente retorna `Optional.of(1L)` como placeholder
- Campos `createdBy`, `updatedBy` presentes nas entidades, mas auditoria real não está ativa

### 5. Pacote de testes

- Tests em `src/test/java/dev/arcanus/api_sgcc/modules/ops_logs/entities`
- JUnit 5 + Assertions
- Tests de criação e validação de regras de negócio

### 6. Convenções observadas:

- Nomes de métodos em camelCase
- Getters públicos, setters privados (quando houver lógica de validação)
- Trimming e upper/lower case em setters
- Constants para patterns (ex: `OCTAL_4_DIGITS` no IFF)
- Javadoc mínimo, foco em código auto-explicativo
