package dev.arcanus.api_sgcc.modules.ops_logs.value_objects;

import jakarta.persistence.Embeddable;

/*
 * ============================================================
 * VALUE OBJECT — ESQUELETO
 * ============================================================
 *
 * Responsabilidades:
 * - Representar um conceito do domínio por seu VALOR.
 * - Garantir que uma instância seja sempre válida.
 * - Centralizar regras de validação e comportamento do valor.
 *
 * Estrutura padrão:
 *
 * 1. Estado
 *    └── atributos que representam o valor
 *
 * 2. Construtor privado
 *    └── impede criação direta fora da classe
 *
 * 3. Factory Method — of()
 *    └── ponto de entrada para criação do objeto
 *
 * 4. Validação — validate()
 *    └── garante as invariantes do Value Object
 *
 * 5. equals() / hashCode()
 *    └── igualdade baseada no valor, não na identidade
 *
 * 6. toString()
 *    └── representação textual do valor
 *
 * ------------------------------------------------------------
 * EXEMPLO
 * ------------------------------------------------------------
 */

@Embeddable
public class ValueObject {

    // 1. ESTADO
    // ---------------------------------------------------------
    // O atributo representa o valor do objeto.
    //
    // Exemplo:
    // private String value;

    private String value;


    // 2. CONSTRUTOR PARA JPA/HIBERNATE
    // ---------------------------------------------------------
    // Necessário para o framework reconstruir o objeto.
    // Não deve ser utilizado diretamente pela aplicação.

    protected ValueObject() {
    }


    // 3. CONSTRUTOR PRIVADO
    // ---------------------------------------------------------
    // Impede a criação direta:
    //
    // new ValueObject(...)
    //
    // A criação deve passar pelo método of(),
    // garantindo que as regras do objeto sejam aplicadas.

    private ValueObject(String value) {
        this.value = value;
    }


    // 4. FACTORY METHOD — of()
    // ---------------------------------------------------------
    // Ponto de entrada para criação do Value Object.
    //
    // Responsável por:
    // 1. Validar os dados recebidos.
    // 2. Criar e retornar uma instância válida.

    public static ValueObject of(String value) {
        validate(value);

        return new ValueObject(value);
    }


    // 5. VALIDAÇÃO
    // ---------------------------------------------------------
    // Centraliza as invariantes do Value Object.
    //
    // A validação deve impedir que uma instância inválida
    // seja criada.

    private static void validate(String value) {

        if (value == null) {
            throw new IllegalArgumentException(
                "Value não pode ser nulo."
            );
        }

        // Outras regras do domínio...
    }


    // 6. GETTER
    // ---------------------------------------------------------

    public String getValue() {
        return value;
    }


    // 7. IGUALDADE POR VALOR
    // ---------------------------------------------------------
    // Value Objects são comparados pelo valor que representam,
    // e não pela identidade da instância.

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof ValueObject other)) {
            return false;
        }

        return Objects.equals(value, other.value);
    }


    // 8. hashCode()
    // ---------------------------------------------------------
    // Deve utilizar os mesmos atributos considerados no equals().

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }


    // 9. toString()
    // ---------------------------------------------------------
    // Retorna uma representação textual do valor.

    @Override
    public String toString() {
        return value;
    }
}
