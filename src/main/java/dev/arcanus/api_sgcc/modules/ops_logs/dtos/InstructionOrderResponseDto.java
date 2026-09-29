package dev.arcanus.api_sgcc.modules.ops_logs.dtos;

public record InstructionOrderResponseDto(
    Long id,
    String code,
    int quantity,
    String description,
    Long ops_role_id,
    String ops_role_name
) {
}
