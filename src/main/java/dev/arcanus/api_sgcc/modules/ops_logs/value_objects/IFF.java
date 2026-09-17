package dev.arcanus.api_sgcc.modules.ops_logs.value_objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
public class IFF implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Pattern OCTAL_4_DIGITS = Pattern.compile("^[0-7]{4}$");

    @Column(name = "iff_code", length = 4)
    private String code;

    protected IFF() {}

    private IFF(String code) {
        this.code = code;
    }

    public static IFF of(String code) {
        validateCode(code);
        return new IFF(code);
    }

    private static void validateCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("O código IFF não pode ser vazio.");
        }

        if (!OCTAL_4_DIGITS.matcher(code).matches()) {
            throw new IllegalArgumentException("O código IFF deve conter 4 dígitos octais (0-7)." + code);
        }
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IFF iff)) return false;
        return Objects.equals(code, iff.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }

}
