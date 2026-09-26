package dev.arcanus.api_sgcc.modules.ops_logs.controllers;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.OpsRoleMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.services.OpsRoleService;
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

@WebMvcTest(OpsRoleController.class)
class OpsRoleControllerTest {

    private static final String ENDPOINT = "/api/v1/ops-roles";
    private static final long ROLE_ID = 7L;

    private static final OpsRoleRequestDto VALID_REQUEST = new OpsRoleRequestDto(
            "cc",
            "controle de trafego aereo",
            120L
    );

    private static final OpsRoleResponseDto ROLE_RESPONSE = new OpsRoleResponseDto(
            ROLE_ID,
            "CC",
            "controle de trafego aereo"
    );

    private static final String VALID_BODY = """
            {
                "name": "cc",
                "description": "controle de trafego aereo",
                "daysToExpire": 120
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OpsRoleService opsRoleService;

    @MockitoBean
    private OpsRoleMapper opsRoleMapper;

    @Test
    @DisplayName("Deve criar uma qualificação operacional e responder 201 Created")
    void shouldCreateOpsRole() throws Exception {
        // Given
        when(opsRoleService.create(VALID_REQUEST)).thenReturn(ROLE_RESPONSE);

        // When / Then
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(ENDPOINT + "/" + ROLE_ID)))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("CC"))
                .andExpect(jsonPath("$.description").value("controle de trafego aereo"));

        verify(opsRoleService).create(VALID_REQUEST);
        verifyNoInteractions(opsRoleMapper);
    }

    @Test
    @DisplayName("Deve responder 400 quando os dados da qualificação forem inválidos")
    void shouldReturnBadRequestWhenCreateValidationFails() throws Exception {
        // Given
        String invalidBody = """
                {
                    "name": "   ",
                    "daysToExpire": null
                }
                """;

        // When / Then
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(opsRoleService, opsRoleMapper);
    }

    @Test
    @DisplayName("Deve responder 409 quando a qualificação já existir")
    void shouldReturnConflictWhenRoleAlreadyExists() throws Exception {
        // Given
        when(opsRoleService.create(VALID_REQUEST))
                .thenThrow(new SGCCResourceAlreadyExists(
                        "Essa qualificação operacional já foi cadastrada"
                ));

        // When / Then
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Recurso já cadastrado"))
                .andExpect(jsonPath("$.detail").value("Essa qualificação operacional já foi cadastrada"))
                .andExpect(jsonPath("$.type").value(containsString("/errors/")));
    }

    @Test
    @DisplayName("Deve listar todas as qualificações operacionais")
    void shouldFindAllOpsRoles() throws Exception {
        // Given
        OpsRoleResponseDto secondResponse = new OpsRoleResponseDto(
                8L,
                "AJCC",
                "assistente de controle de trafego aereo"
        );
        when(opsRoleService.findAll()).thenReturn(List.of(ROLE_RESPONSE, secondResponse));

        // When / Then
        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].name").value("CC"))
                .andExpect(jsonPath("$[1].id").value(8))
                .andExpect(jsonPath("$[1].name").value("AJCC"));

        verify(opsRoleService).findAll();
        verifyNoInteractions(opsRoleMapper);
    }

    @Test
    @DisplayName("Deve responder com uma lista vazia quando não houver qualificações")
    void shouldReturnEmptyListWhenNoOpsRolesExist() throws Exception {
        // Given
        when(opsRoleService.findAll()).thenReturn(List.of());

        // When / Then
        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(opsRoleService).findAll();
    }

    @Test
    @DisplayName("Deve encontrar uma qualificação operacional pelo ID")
    void shouldFindOpsRoleById() throws Exception {
        // Given
        when(opsRoleService.findById(ROLE_ID)).thenReturn(ROLE_RESPONSE);

        // When / Then
        mockMvc.perform(get(ENDPOINT + "/{id}", ROLE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("CC"))
                .andExpect(jsonPath("$.description").value("controle de trafego aereo"));

        verify(opsRoleService).findById(ROLE_ID);
        verifyNoInteractions(opsRoleMapper);
    }

    @Test
    @DisplayName("Deve responder 404 quando a qualificação não for encontrada")
    void shouldReturnNotFoundWhenRoleDoesNotExist() throws Exception {
        // Given
        when(opsRoleService.findById(ROLE_ID))
                .thenThrow(new SGCCResourceNotFoundException(
                        "Qualificação operacional não encontrada"
                ));

        // When / Then
        mockMvc.perform(get(ENDPOINT + "/{id}", ROLE_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Qualificação operacional não encontrada"))
                .andExpect(jsonPath("$.detail").value("Recurso não encontrado"));
    }

    @Test
    @DisplayName("Deve atualizar uma qualificação operacional")
    void shouldUpdateOpsRole() throws Exception {
        // Given
        OpsRoleResponseDto updatedResponse = new OpsRoleResponseDto(
                ROLE_ID,
                "AJCC",
                "assistente de controle de trafego aereo"
        );
        when(opsRoleService.update(ROLE_ID, VALID_REQUEST)).thenReturn(updatedResponse);

        // When / Then
        mockMvc.perform(put(ENDPOINT + "/{id}", ROLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("AJCC"))
                .andExpect(jsonPath("$.description").value("assistente de controle de trafego aereo"));

        verify(opsRoleService).update(ROLE_ID, VALID_REQUEST);
        verifyNoInteractions(opsRoleMapper);
    }

    @Test
    @DisplayName("Deve responder 400 quando os dados de atualização forem inválidos")
    void shouldReturnBadRequestWhenUpdateValidationFails() throws Exception {
        // Given
        String invalidBody = """
                {
                    "name": "",
                    "daysToExpire": 0
                }
                """;

        // When / Then
        mockMvc.perform(put(ENDPOINT + "/{id}", ROLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(opsRoleService, opsRoleMapper);
    }

    @Test
    @DisplayName("Deve excluir uma qualificação operacional")
    void shouldDeleteOpsRole() throws Exception {
        // When / Then
        mockMvc.perform(delete(ENDPOINT + "/{id}", ROLE_ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(opsRoleService).delete(ROLE_ID);
        verifyNoInteractions(opsRoleMapper);
    }

    @Test
    @DisplayName("Deve responder 404 quando a qualificação não for encontrada para exclusão")
    void shouldReturnNotFoundWhenDeleteRoleDoesNotExist() throws Exception {
        // Given
        doThrow(new SGCCResourceNotFoundException("Qualificação operacional não encontrada"))
                .when(opsRoleService)
                .delete(ROLE_ID);

        // When / Then
        mockMvc.perform(delete(ENDPOINT + "/{id}", ROLE_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));

        verify(opsRoleService).delete(ROLE_ID);
    }
}
