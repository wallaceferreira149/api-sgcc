package dev.arcanus.api_sgcc.modules.ops_logs.controllers;

import dev.arcanus.api_sgcc.modules.ops_logs.dtos.InstructionOrderRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.InstructionOrderResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.services.InstructionOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ops-logs/instruction-orders")
@Validated
public class InstructionOrderController {

    private final InstructionOrderService service;

    public InstructionOrderController(InstructionOrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<InstructionOrderResponseDto> createInstructionOrder(
        @Valid @RequestBody InstructionOrderRequestDto request,
        UriComponentsBuilder uriBuilder) {

        InstructionOrderResponseDto responseDto = service.create(request);
        URI uri = uriBuilder.path("/api/v1/ops-logs/instruction-orders/{id}")
            .buildAndExpand(responseDto.id()).toUri();

        return ResponseEntity.created(uri).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<InstructionOrderResponseDto>> listAllInstructionOrders() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructionOrderResponseDto> findInstructionOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructionOrderResponseDto> updateInstructionOrder(
        @PathVariable Long id,
        @Valid @RequestBody InstructionOrderRequestDto request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstructionOrder(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
