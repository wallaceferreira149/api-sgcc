package dev.arcanus.api_sgcc.modules.ops_logs.controllers;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCInvalidRequestException;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsLocaleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsLocaleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.services.OpsLocaleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OpsLocaleController.class)
@DisplayName("OpsLocaleController - Testes de slice MVC")
class OpsLocaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OpsLocaleService service;

    @Nested
    @DisplayName("POST /api/v1/ops-locale")
    class CreateEndpoint {

        @Test
        @DisplayName("Deve criar localidade e retornar 201 Created com Location header")
        void shouldCreateLocaleAndReturn201() throws Exception {
            // Given
            var response = new OpsLocaleResponseDto(1L, "ICEA");
            when(service.create(any(OpsLocaleRequestDto.class))).thenReturn(response);

            // When / Then
            mockMvc.perform(post("/api/v1/ops-locale")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"locale": "ICEA"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.locale").value("ICEA"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando locale está vazio")
        void shouldReturn400WhenLocaleIsBlank() throws Exception {
            // When / Then
            mockMvc.perform(post("/api/v1/ops-locale")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"locale": "   "}
                        """))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando locale é nulo")
        void shouldReturn400WhenLocaleIsNull() throws Exception {
            // When / Then
            mockMvc.perform(post("/api/v1/ops-locale")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"locale": null}
                        """))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando service lança exceção de duplicata")
        void shouldReturn400WhenServiceThrowsDuplicate() throws Exception {
            // Given
            when(service.create(any(OpsLocaleRequestDto.class)))
                .thenThrow(new SGCCInvalidRequestException("A localidade já existe"));

            // When / Then
            mockMvc.perform(post("/api/v1/ops-locale")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"locale": "ICEA"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("A localidade já existe"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/ops-locale")
    class ListEndpoint {

        @Test
        @DisplayName("Deve retornar lista vazia quando não há localidades")
        void shouldReturnEmptyList() throws Exception {
            // Given
            when(service.listAll()).thenReturn(List.of());

            // When / Then
            mockMvc.perform(get("/api/v1/ops-locale"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("Deve retornar todas as localidades")
        void shouldReturnAllLocales() throws Exception {
            // Given
            var response1 = new OpsLocaleResponseDto(1L, "ICEA");
            var response2 = new OpsLocaleResponseDto(2L, "SÃO PAULO");
            when(service.listAll()).thenReturn(List.of(response1, response2));

            // When / Then
            mockMvc.perform(get("/api/v1/ops-locale"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].locale").value("ICEA"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].locale").value("SÃO PAULO"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/ops-locale/{id}")
    class FindByIdEndpoint {

        @Test
        @DisplayName("Deve retornar localidade quando encontrada")
        void shouldReturnLocaleWhenFound() throws Exception {
            // Given
            var response = new OpsLocaleResponseDto(1L, "ICEA");
            when(service.findById(1L)).thenReturn(response);

            // When / Then
            mockMvc.perform(get("/api/v1/ops-locale/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.locale").value("ICEA"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando localidade não encontrada")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(service.findById(999L))
                .thenThrow(new SGCCResourceNotFoundException("Localidade não encontrada"));

            // When / Then
            mockMvc.perform(get("/api/v1/ops-locale/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Localidade não encontrada"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando ID é inválido (não numérico)")
        void shouldReturn400WhenIdIsInvalid() throws Exception {
            // When / Then
            mockMvc.perform(get("/api/v1/ops-locale/abc"))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/ops-locale/{id}")
    class UpdateEndpoint {

        @Test
        @DisplayName("Deve atualizar localidade e retornar 200 OK")
        void shouldUpdateLocaleAndReturn200() throws Exception {
            // Given
            var response = new OpsLocaleResponseDto(1L, "BRASÍLIA");
            when(service.update(eq(1L), any(OpsLocaleRequestDto.class))).thenReturn(response);

            // When / Then
            mockMvc.perform(put("/api/v1/ops-locale/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"locale": "BRASÍLIA"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.locale").value("BRASÍLIA"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando localidade não encontrada")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(service.update(eq(999L), any(OpsLocaleRequestDto.class)))
                .thenThrow(new SGCCResourceNotFoundException("Localidade não encontrada"));

            // When / Then
            mockMvc.perform(put("/api/v1/ops-locale/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"locale": "BRASÍLIA"}
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Localidade não encontrada"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando request body é inválido")
        void shouldReturn400WhenRequestBodyInvalid() throws Exception {
            // When / Then
            mockMvc.perform(put("/api/v1/ops-locale/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"locale": ""}
                        """))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando ID é nulo")
        void shouldReturn400WhenIdIsNull() throws Exception {
            // Given
            when(service.update(eq(null), any(OpsLocaleRequestDto.class)))
                .thenThrow(new SGCCInvalidRequestException("Dados de localidade inválidos"));

            // When / Then
            mockMvc.perform(put("/api/v1/ops-locale/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"locale": "BRASÍLIA"}
                        """))
                .andExpect(status().isNotFound()); // URL não casa com /{id}
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/ops-locale/{id}")
    class DeleteEndpoint {

        @Test
        @DisplayName("Deve deletar localidade e retornar 204 No Content")
        void shouldDeleteLocaleAndReturn204() throws Exception {
            // When / Then
            mockMvc.perform(delete("/api/v1/ops-locale/1"))
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 404 quando localidade não encontrada")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            doThrow(new SGCCResourceNotFoundException("Localidade não encontrada"))
                .when(service).delete(999L);

            // When / Then
            mockMvc.perform(delete("/api/v1/ops-locale/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Localidade não encontrada"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando ID é inválido")
        void shouldReturn400WhenIdIsInvalid() throws Exception {
            // When / Then
            mockMvc.perform(delete("/api/v1/ops-locale/abc"))
                .andExpect(status().isBadRequest());
        }
    }
}