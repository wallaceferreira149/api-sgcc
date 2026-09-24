package dev.arcanus.api_sgcc.modules.ops_logs.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OpsRoleRequestDto (
    @NotBlank(message = "O nome da qualificação operacional é obrigatório")
    String name,
    String description,
    @NotNull(message = "A duração da qualificação operacional é obrigatória")
    @Min(value = 1, message = "A duração deve ser um período positivo")
    @Max(value = 9999, message = "A duração não pode exceder 9999 dias")
    Long daysToExpire
) {
}
