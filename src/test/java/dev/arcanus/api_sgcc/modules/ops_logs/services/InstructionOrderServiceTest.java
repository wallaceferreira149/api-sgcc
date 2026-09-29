package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCInvalidRequestException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.InstructionOrderRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.InstructionOrderResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.InstructionOrder;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsRole;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.InstructionOrderMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.InstructionOrderRepository;
import dev.arcanus.api_sgcc.modules.ops_logs.repositories.OpsRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstructionOrderServiceTest {

    @Mock private InstructionOrderRepository instructionOrderRepository;
    @Mock private OpsRoleRepository opsRoleRepository;
    @Mock private InstructionOrderMapper instructionOrderMapper;
    @InjectMocks private InstructionOrderService instructionOrderService;

    private OpsRole validOpsRole;
    private InstructionOrder validInstructionOrder;
    private InstructionOrderRequestDto validRequest;
    private InstructionOrderResponseDto validResponse;

    @BeforeEach
    void setUp() {
        validOpsRole = new OpsRole("CC", "Controle de tráfego aéreo", 120L);

        validInstructionOrder = new InstructionOrder("OI-001", 10, "Ordem de Instrução 1", validOpsRole);

        validRequest = new InstructionOrderRequestDto("OI-001", 10, "Ordem de Instrução 1", 1L);
        validResponse = new InstructionOrderResponseDto(1L, "OI-001", 10, "Ordem de Instrução 1", 1L, "CC");
    }

    @Nested
    @DisplayName("Caso de uso: criar ordem de instrução")
    class CreateInstructionOrderUseCase {

        @Test
        @DisplayName("Deve criar uma ordem de instrução com sucesso")
        void shouldCreateInstructionOrderSuccessfully() {
            // Given
            when(instructionOrderRepository.existsByCode("OI-001")).thenReturn(false);
            when(instructionOrderMapper.toEntity(validRequest)).thenReturn(validInstructionOrder);
            when(opsRoleRepository.findById(1L)).thenReturn(Optional.of(validOpsRole));
            when(instructionOrderRepository.save(any(InstructionOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(instructionOrderMapper.toResponse(any(InstructionOrder.class))).thenReturn(validResponse);

            // When
            InstructionOrderResponseDto result = instructionOrderService.create(validRequest);

            // Then
            assertNotNull(result);
            assertEquals(validResponse.code(), result.code());
            assertEquals(validResponse.quantity(), result.quantity());
            assertEquals(validResponse.description(), result.description());
            assertEquals(validResponse.ops_role_id(), result.ops_role_id());
            assertEquals(validResponse.ops_role_name(), result.ops_role_name());

            verify(instructionOrderRepository).existsByCode("OI-001");
            verify(instructionOrderMapper).toEntity(validRequest);
            verify(opsRoleRepository).findById(1L);
            verify(instructionOrderRepository).save(validInstructionOrder);
            verify(instructionOrderMapper).toResponse(validInstructionOrder);
        }

        @Test
        @DisplayName("Não deve criar ordem de instrução com código já cadastrado")
        void shouldNotCreateInstructionOrderWithDuplicatedCode() {
            // Given
            when(instructionOrderMapper.toEntity(validRequest)).thenReturn(validInstructionOrder);
            when(instructionOrderRepository.existsByCode("OI-001")).thenReturn(true);

            // When / Then
            assertThrows(SGCCResourceAlreadyExists.class,
                () -> instructionOrderService.create(validRequest));

            verify(instructionOrderMapper).toEntity(validRequest);
            verify(instructionOrderRepository).existsByCode("OI-001");
            verify(instructionOrderRepository, never()).save(any(InstructionOrder.class));
            verify(opsRoleRepository, never()).findById(any(Long.class));
        }

        @Test
        @DisplayName("Não deve criar ordem de instrução quando a qualificação operacional não existir")
        void shouldNotCreateInstructionOrderWhenOpsRoleNotFound() {
            // Given
            when(instructionOrderRepository.existsByCode("OI-001")).thenReturn(false);
            when(instructionOrderMapper.toEntity(validRequest)).thenReturn(validInstructionOrder);
            when(opsRoleRepository.findById(1L)).thenReturn(Optional.empty());

            // When / Then
            assertThrows(SGCCResourceNotFoundException.class,
                () -> instructionOrderService.create(validRequest));

            verify(instructionOrderRepository).existsByCode("OI-001");
            verify(opsRoleRepository).findById(1L);
            verify(instructionOrderRepository, never()).save(any(InstructionOrder.class));
        }
    }

    @Nested
    @DisplayName("Caso de uso: listar ordens de instrução")
    class ListInstructionOrdersUseCase {

        private InstructionOrder secondInstructionOrder;
        private InstructionOrderResponseDto secondResponse;

        @BeforeEach
        void setUp() {
            OpsRole secondOpsRole = new OpsRole("AJCC", "Assistente", 120L);

            secondInstructionOrder = new InstructionOrder("OI-002", 5, "Segunda OI", secondOpsRole);

            secondResponse = new InstructionOrderResponseDto(2L, "OI-002", 5, "Segunda OI", 2L, "AJCC");
        }

        @Test
        @DisplayName("Deve listar todas as ordens de instrução")
        void shouldListAllInstructionOrders() {
            // Given
            when(instructionOrderRepository.findAll()).thenReturn(List.of(validInstructionOrder, secondInstructionOrder));
            when(instructionOrderMapper.toResponse(validInstructionOrder)).thenReturn(validResponse);
            when(instructionOrderMapper.toResponse(secondInstructionOrder)).thenReturn(secondResponse);

            // When
            List<InstructionOrderResponseDto> result = instructionOrderService.findAll();

            // Then
            assertEquals(2, result.size());
            assertEquals(validResponse.code(), result.get(0).code());
            assertEquals(secondResponse.code(), result.get(1).code());

            verify(instructionOrderRepository).findAll();
            verify(instructionOrderMapper).toResponse(validInstructionOrder);
            verify(instructionOrderMapper).toResponse(secondInstructionOrder);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem ordens de instrução")
        void shouldReturnEmptyListWhenNoInstructionOrders() {
            // Given
            when(instructionOrderRepository.findAll()).thenReturn(List.of());

            // When
            List<InstructionOrderResponseDto> result = instructionOrderService.findAll();

            // Then
            assertEquals(0, result.size());
            verify(instructionOrderRepository).findAll();
        }

        @Test
        @DisplayName("Deve encontrar uma ordem de instrução pelo ID")
        void shouldFindInstructionOrderById() {
            // Given
            when(instructionOrderRepository.findById(1L)).thenReturn(Optional.of(validInstructionOrder));
            when(instructionOrderMapper.toResponse(validInstructionOrder)).thenReturn(validResponse);

            // When
            InstructionOrderResponseDto result = instructionOrderService.findById(1L);

            // Then
            assertEquals(validResponse.id(), result.id());
            assertEquals(validResponse.code(), result.code());
            assertEquals(validResponse.ops_role_name(), result.ops_role_name());

            verify(instructionOrderRepository).findById(1L);
            verify(instructionOrderMapper).toResponse(validInstructionOrder);
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID não for encontrado")
        void shouldThrowExceptionWhenIdNotFound() {
            // Given
            when(instructionOrderRepository.findById(1L)).thenReturn(Optional.empty());

            // When / Then
            assertThrows(SGCCResourceNotFoundException.class,
                () -> instructionOrderService.findById(1L));

            verify(instructionOrderRepository).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID for nulo")
        void shouldThrowExceptionWhenIdIsNull() {
            // When / Then
            assertThrows(SGCCInvalidRequestException.class,
                () -> instructionOrderService.findById(null));

            verify(instructionOrderRepository, never()).findById(any(Long.class));
        }
    }

    @Nested
    @DisplayName("Caso de uso: atualizar ordem de instrução")
    class UpdateInstructionOrderUseCase {

        private InstructionOrderRequestDto updateRequest;
        private InstructionOrderResponseDto updatedResponse;

        @BeforeEach
        void setUp() {
            updateRequest = new InstructionOrderRequestDto("OI-001-UPDATED", 15, "Atualizada", 1L);
            updatedResponse = new InstructionOrderResponseDto(1L, "OI-001-UPDATED", 15, "Atualizada", 1L, "CC");
        }

        @Test
        @DisplayName("Deve atualizar ordem de instrução com sucesso")
        void shouldUpdateInstructionOrderSuccessfully() {
            // Given
            when(instructionOrderRepository.findById(1L)).thenReturn(Optional.of(validInstructionOrder));
            when(opsRoleRepository.findById(1L)).thenReturn(Optional.of(validOpsRole));
            when(instructionOrderRepository.save(any(InstructionOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(instructionOrderMapper.toResponse(any(InstructionOrder.class))).thenReturn(updatedResponse);

            // When
            InstructionOrderResponseDto result = instructionOrderService.update(1L, updateRequest);

            // Then
            assertEquals(updatedResponse.code(), result.code());
            assertEquals(updatedResponse.quantity(), result.quantity());
            assertEquals(updatedResponse.description(), result.description());

            verify(instructionOrderRepository).findById(1L);
            verify(instructionOrderRepository).save(validInstructionOrder);
            verify(instructionOrderMapper).toResponse(validInstructionOrder);
        }

        @Test
        @DisplayName("Deve atualizar a qualificação operacional quando o ID mudar")
        void shouldUpdateOpsRoleWhenIdChanges() {
            // Given
            OpsRole newOpsRole = new OpsRole("AJCC", "Assistente", 120L);

            InstructionOrderRequestDto requestWithNewRole = new InstructionOrderRequestDto("OI-001", 10, "Atualizada", 2L);
            InstructionOrderResponseDto responseWithNewRole = new InstructionOrderResponseDto(1L, "OI-001", 10, "Atualizada", 2L, "AJCC");

            when(instructionOrderRepository.findById(1L)).thenReturn(Optional.of(validInstructionOrder));
            when(opsRoleRepository.findById(2L)).thenReturn(Optional.of(newOpsRole));
            when(instructionOrderRepository.save(any(InstructionOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(instructionOrderMapper.toResponse(any(InstructionOrder.class))).thenReturn(responseWithNewRole);

            // When
            InstructionOrderResponseDto result = instructionOrderService.update(1L, requestWithNewRole);

            // Then
            assertEquals(2L, result.ops_role_id());
            assertEquals("AJCC", result.ops_role_name());

            verify(instructionOrderRepository).findById(1L);
            verify(opsRoleRepository).findById(2L);
            verify(instructionOrderRepository).save(validInstructionOrder);
            verify(instructionOrderMapper).toResponse(validInstructionOrder);
        }

        @Test
        @DisplayName("Não deve atualizar quando ID da ordem não for encontrado")
        void shouldNotUpdateWhenInstructionOrderNotFound() {
            // Given
            when(instructionOrderRepository.findById(1L)).thenReturn(Optional.empty());

            // When / Then
            assertThrows(SGCCResourceNotFoundException.class,
                () -> instructionOrderService.update(1L, updateRequest));

            verify(instructionOrderRepository).findById(1L);
            verify(instructionOrderRepository, never()).save(any(InstructionOrder.class));
        }

        @Test
        @DisplayName("Não deve atualizar quando ID da qualificação operacional não for encontrado")
        void shouldNotUpdateWhenOpsRoleNotFound() {
            // Given
            when(instructionOrderRepository.findById(1L)).thenReturn(Optional.of(validInstructionOrder));
            when(opsRoleRepository.findById(2L)).thenReturn(Optional.empty());

            InstructionOrderRequestDto requestWithInvalidRole = new InstructionOrderRequestDto("OI-001", 10, "Atualizada", 2L);

            // When / Then
            assertThrows(SGCCResourceNotFoundException.class,
                () -> instructionOrderService.update(1L, requestWithInvalidRole));

            verify(instructionOrderRepository).findById(1L);
            verify(opsRoleRepository).findById(2L);
            verify(instructionOrderRepository, never()).save(any(InstructionOrder.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID for nulo")
        void shouldThrowExceptionWhenIdIsNull() {
            // When / Then
            assertThrows(SGCCInvalidRequestException.class,
                () -> instructionOrderService.update(null, updateRequest));

            verify(instructionOrderRepository, never()).findById(any(Long.class));
        }
    }

    @Nested
    @DisplayName("Caso de uso: deletar ordem de instrução")
    class DeleteInstructionOrderUseCase {

        @Test
        @DisplayName("Deve deletar uma ordem de instrução com sucesso")
        void shouldDeleteInstructionOrderSuccessfully() {
            // Given
            when(instructionOrderRepository.existsById(1L)).thenReturn(true);

            // When
            instructionOrderService.delete(1L);

            // Then
            verify(instructionOrderRepository).existsById(1L);
            verify(instructionOrderRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Não deve deletar quando ID não for encontrado")
        void shouldNotDeleteWhenIdNotFound() {
            // Given
            when(instructionOrderRepository.existsById(1L)).thenReturn(false);

            // When / Then
            assertThrows(SGCCResourceNotFoundException.class,
                () -> instructionOrderService.delete(1L));

            verify(instructionOrderRepository).existsById(1L);
            verify(instructionOrderRepository, never()).deleteById(any(Long.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID for nulo")
        void shouldThrowExceptionWhenIdIsNull() {
            // When / Then
            assertThrows(SGCCInvalidRequestException.class,
                () -> instructionOrderService.delete(null));

            verify(instructionOrderRepository, never()).existsById(any(Long.class));
        }
    }
}