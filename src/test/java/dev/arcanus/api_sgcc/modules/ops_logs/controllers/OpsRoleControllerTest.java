package dev.arcanus.api_sgcc.modules.ops_logs.controllers;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsRole;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.OpsRoleMapper;
import dev.arcanus.api_sgcc.modules.ops_logs.services.OpsRoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpsRoleController.class)
class OpsRoleControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private OpsRoleService opsRoleService;
    @MockitoBean private OpsRoleMapper opsRoleMapper;

    private static final String ENDPOINT = "/api/v1/ops-roles";

    private static final String VALID_BODY = """
        {
            "name": "cc",
            "description": "controle de trafego aereo",
            "daysToExpire": 120
        }
        """;

    @Test
    @DisplayName("Deve responder 201 Created com header Location")
    void shouldReturn201CreatedWithLocationHeader() throws Exception {
        OpsRole created = new OpsRole("cc", "controle de trafego aereo", 120L);
        ReflectionTestUtils.setField(created, "id", 7L);

        when(opsRoleService.create(any(OpsRoleRequestDto.class))).thenReturn(created);
        when(opsRoleMapper.toResponse(created))
            .thenReturn(new OpsRoleResponseDto("CC", "controle de trafego aereo"));

        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString(ENDPOINT + "/7")))
            .andExpect(jsonPath("$.name").value("CC"))
            .andExpect(jsonPath("$.description").value("controle de trafego aereo"));
    }

    @Test
    @DisplayName("Deve responder 400 quando a validação do DTO falhar")
    void shouldReturn400WhenValidationFails() throws Exception {
        String invalidBody = """
            {
                "name": "   ",
                "daysToExpire": null
            }
            """;

        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("Deve responder 409 Conflict quando a qualificação já existe")
    void shouldReturn409ConflictWhenRoleAlreadyExists() throws Exception {
        when(opsRoleService.create(any(OpsRoleRequestDto.class)))
            .thenThrow(new SGCCResourceAlreadyExists("Essa qualificação operacional já foi cadastrada"));

        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
            .andExpect(status().isConflict())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.title").value("Recurso já cadastrado"))
            .andExpect(jsonPath("$.detail").value("Essa qualificação operacional já foi cadastrada"))
            .andExpect(jsonPath("$.type").value(containsString("/errors/")));
    }
}
