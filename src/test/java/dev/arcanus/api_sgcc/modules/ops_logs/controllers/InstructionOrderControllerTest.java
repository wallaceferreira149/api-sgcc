package dev.arcanus.api_sgcc.modules.ops_logs.controllers;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.InstructionOrderRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.InstructionOrderResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.InstructionOrderMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.services.InstructionOrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InstructionOrderController.class)
class InstructionOrderControllerTest {

    private static final String ENDPOINT = "/api/v1/ops-logs/instruction-orders";
    private static final long INSTRUCTION_ORDER_ID = 1L;

    private static final InstructionOrderRequestDto VALID_REQUEST = new InstructionOrderRequestDto(
            "OI-001",
            10,
            "Ordem de Instrução 1",
            1L
    );

    private static final InstructionOrderResponseDto VALID_RESPONSE = new InstructionOrderResponseDto(
            INSTRUCTION_ORDER_ID,
            "OI-001",
            10,
            "Ordem de Instrução 1",
            1L,
            "CC"
    );

    private static final String VALID_BODY = """
            {
                "code": "OI-001",
                "quantity": 10,
                "description": "Ordem de Instrução 1",
                "ops_role_id": 1
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InstructionOrderService instructionOrderService;

    @MockitoBean
    private InstructionOrderMapper instructionOrderMapper;

    @Test
    @DisplayName("Deve criar uma ordem de instrução e responder 201 Created")
    void shouldCreateInstructionOrder() throws Exception {
        // Given
        when(instructionOrderService.create(VALID_REQUEST)).thenReturn(VALID_RESPONSE);

        // When / Then
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(ENDPOINT + "/" + INSTRUCTION_ORDER_ID)))
                .andExpect(jsonPath("$.id").value(INSTRUCTION_ORDER_ID))
                .andExpect(jsonPath("$.code").value("OI-001"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.description").value("Ordem de Instrução 1"))
                .andExpect(jsonPath("$.ops_role_id").value(1))
                .andExpect(jsonPath("$.ops_role_name").value("CC"));

        verify(instructionOrderService).create(VALID_REQUEST);
        verifyNoInteractions(instructionOrderMapper);
    }

    @Test
    @DisplayName("Deve responder 400 quando os dados da ordem forem inválidos")
    void shouldReturnBadRequestWhenCreateValidationFails() throws Exception {
        // Given
        String invalidBody = """
                {
                    "code": "   ",
                    "quantity": 0,
                    "ops_role_id": null
                }
                """;

        // When / Then
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(instructionOrderService, instructionOrderMapper);
    }

    @Test
    @DisplayName("Deve responder 409 quando a ordem de instrução já existir")
    void shouldReturnConflictWhenInstructionOrderAlreadyExists() throws Exception {
        // Given
        when(instructionOrderService.create(VALID_REQUEST))
                .thenThrow(new SGCCResourceAlreadyExists(
                        "O código da Ordem de Instrução já foi cadastrado"
                ));

        // When / Then
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Recurso já cadastrado"))
                .andExpect(jsonPath("$.detail").value("O código da Ordem de Instrução já foi cadastrado"))
                .andExpect(jsonPath("$.type").value(containsString("/errors/")));
    }

    @Test
    @DisplayName("Deve listar todas as ordens de instrução")
    void shouldFindAllInstructionOrders() throws Exception {
        // Given
        InstructionOrderResponseDto secondResponse = new InstructionOrderResponseDto(
                2L,
                "OI-002",
                5,
                "Segunda OI",
                2L,
                "AJCC"
        );
        when(instructionOrderService.findAll()).thenReturn(List.of(VALID_RESPONSE, secondResponse));

        // When / Then
        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(INSTRUCTION_ORDER_ID))
                .andExpect(jsonPath("$[0].code").value("OI-001"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].code").value("OI-002"));

        verify(instructionOrderService).findAll();
        verifyNoInteractions(instructionOrderMapper);
    }

    @Test
    @DisplayName("Deve responder com uma lista vazia quando não houver ordens de instrução")
    void shouldReturnEmptyListWhenNoInstructionOrdersExist() throws Exception {
        // Given
        when(instructionOrderService.findAll()).thenReturn(List.of());

        // When / Then
        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(instructionOrderService).findAll();
    }

    @Test
    @DisplayName("Deve encontrar uma ordem de instrução pelo ID")
    void shouldFindInstructionOrderById() throws Exception {
        // Given
        when(instructionOrderService.findById(INSTRUCTION_ORDER_ID)).thenReturn(VALID_RESPONSE);

        // When / Then
        mockMvc.perform(get(ENDPOINT + "/{id}", INSTRUCTION_ORDER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(INSTRUCTION_ORDER_ID))
                .andExpect(jsonPath("$.code").value("OI-001"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.description").value("Ordem de Instrução 1"))
                .andExpect(jsonPath("$.ops_role_id").value(1))
                .andExpect(jsonPath("$.ops_role_name").value("CC"));

        verify(instructionOrderService).findById(INSTRUCTION_ORDER_ID);
        verifyNoInteractions(instructionOrderMapper);
    }

    @Test
    @DisplayName("Deve responder 404 quando a ordem de instrução não for encontrada")
    void shouldReturnNotFoundWhenInstructionOrderDoesNotExist() throws Exception {
        // Given
        when(instructionOrderService.findById(INSTRUCTION_ORDER_ID))
                .thenThrow(new SGCCResourceNotFoundException(
                        "Ordem de Instrução não encontrada"
                ));

        // When / Then
        mockMvc.perform(get(ENDPOINT + "/{id}", INSTRUCTION_ORDER_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Ordem de Instrução não encontrada"))
                .andExpect(jsonPath("$.detail").value("Recurso não encontrado"));
    }

    @Test
    @DisplayName("Deve atualizar uma ordem de instrução")
    void shouldUpdateInstructionOrder() throws Exception {
        // Given
        InstructionOrderResponseDto updatedResponse = new InstructionOrderResponseDto(
                INSTRUCTION_ORDER_ID,
                "OI-001-UPDATED",
                15,
                "Atualizada",
                1L,
                "CC"
        );
        when(instructionOrderService.update(INSTRUCTION_ORDER_ID, VALID_REQUEST)).thenReturn(updatedResponse);

        // When / Then
        mockMvc.perform(put(ENDPOINT + "/{id}", INSTRUCTION_ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(INSTRUCTION_ORDER_ID))
                .andExpect(jsonPath("$.code").value("OI-001-UPDATED"))
                .andExpect(jsonPath("$.quantity").value(15))
                .andExpect(jsonPath("$.description").value("Atualizada"))
                .andExpect(jsonPath("$.ops_role_id").value(1))
                .andExpect(jsonPath("$.ops_role_name").value("CC"));

        verify(instructionOrderService).update(INSTRUCTION_ORDER_ID, VALID_REQUEST);
        verifyNoInteractions(instructionOrderMapper);
    }

    @Test
    @DisplayName("Deve responder 400 quando os dados de atualização forem inválidos")
    void shouldReturnBadRequestWhenUpdateValidationFails() throws Exception {
        // Given
        String invalidBody = """
                {
                    "code": "",
                    "quantity": -1,
                    "ops_role_id": 0
                }
                """;

        // When / Then
        mockMvc.perform(put(ENDPOINT + "/{id}", INSTRUCTION_ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(instructionOrderService, instructionOrderMapper);
    }

    @Test
    @DisplayName("Deve excluir uma ordem de instrução")
    void shouldDeleteInstructionOrder() throws Exception {
        // When / Then
        mockMvc.perform(delete(ENDPOINT + "/{id}", INSTRUCTION_ORDER_ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(instructionOrderService).delete(INSTRUCTION_ORDER_ID);
        verifyNoInteractions(instructionOrderMapper);
    }

    @Test
    @DisplayName("Deve responder 404 quando a ordem não for encontrada para exclusão")
    void shouldReturnNotFoundWhenDeleteInstructionOrderDoesNotExist() throws Exception {
        // Given
        doThrow(new SGCCResourceNotFoundException("Ordem de Instrução não encontrada"))
                .when(instructionOrderService)
                .delete(INSTRUCTION_ORDER_ID);

        // When / Then
        mockMvc.perform(delete(ENDPOINT + "/{id}", INSTRUCTION_ORDER_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));

        verify(instructionOrderService).delete(INSTRUCTION_ORDER_ID);
    }
}