package dev.arcanus.api_sgcc.modules.ops_logs.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InstructionOrderRequestDto(
    @NotBlank
    String code,
    @NotNull
    int quantity,
    @Nullable
    String description,
    @NotNull
    Long ops_role_id
) {
}
