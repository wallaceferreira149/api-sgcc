package dev.arcanus.api_sgcc.modules.ops_logs.controllers;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.AircraftTypeResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.services.AircraftTypeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
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

@WebMvcTest(AircraftTypeController.class)
@ActiveProfiles("test")
@DisplayName("Controller de tipos de aeronave")
class AircraftTypeControllerTest {

    private static final String ENDPOINT = "/api/v1/aircraft-types";
    private static final long AIRCRAFT_TYPE_ID = 3L;

    private static final AircraftTypeRequestDto VALID_REQUEST = new AircraftTypeRequestDto("a-29");
    private static final AircraftTypeResponseDto VALID_RESPONSE =
        new AircraftTypeResponseDto(AIRCRAFT_TYPE_ID, "A-29");

    private static final String VALID_BODY = """
        {
            "code": "a-29"
        }
        """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AircraftTypeService service;

    @Nested
    @DisplayName("POST /api/v1/aircraft-types")
    class CreateEndpoint {

        @Test
        @DisplayName("Deve criar um tipo de aeronave e responder 201 Created com Location")
        void shouldCreateAircraftType() throws Exception {
            // Given
            when(service.create(VALID_REQUEST)).thenReturn(VALID_RESPONSE);

            // When / Then
            mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith(ENDPOINT + "/" + AIRCRAFT_TYPE_ID)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.code").value("A-29"));

            verify(service).create(VALID_REQUEST);
        }

        @ParameterizedTest(name = "Deve responder 400 quando o {0}")
        @MethodSource("dev.arcanus.api_sgcc.modules.ops_logs.controllers"
            + ".AircraftTypeControllerTestBodies#invalidRequestBodies")
        @DisplayName("Deve responder 400 quando os dados de criação forem inválidos")
        void shouldReturnBadRequestWhenCreateValidationFails(String description, String invalidBody)
            throws Exception {
            // When / Then
            mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));

            verifyNoInteractions(service);
        }

        @ParameterizedTest(name = "Deve responder 400 quando o body tiver {0}")
        @MethodSource("dev.arcanus.api_sgcc.modules.ops_logs.controllers"
            + ".AircraftTypeControllerTestBodies#malformedJsonBodies")
        @DisplayName("Deve responder 400 quando o body não for um JSON válido")
        void shouldReturnBadRequestWhenBodyIsNotValidJson(String description, String malformedBody)
            throws Exception {
            // When / Then
            mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(malformedBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Deve responder 415 quando o Content-Type não for application/json")
        void shouldReturnUnsupportedMediaTypeWhenContentTypeIsInvalid() throws Exception {
            // When / Then
            mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.TEXT_PLAIN)
                    .content(VALID_BODY))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Deve responder 409 quando o tipo de aeronave já existir")
        void shouldReturnConflictWhenAircraftTypeAlreadyExists() throws Exception {
            // Given
            when(service.create(VALID_REQUEST))
                .thenThrow(new SGCCResourceAlreadyExists("O tipo de aeronave já existe."));

            // When / Then
            mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Recurso já cadastrado"))
                .andExpect(jsonPath("$.detail").value("O tipo de aeronave já existe."))
                .andExpect(jsonPath("$.type").value(containsString("/errors/")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/aircraft-types")
    class ListEndpoint {

        @Test
        @DisplayName("Deve listar todos os tipos de aeronave")
        void shouldFindAllAircraftTypes() throws Exception {
            // Given
            AircraftTypeResponseDto secondResponse = new AircraftTypeResponseDto(4L, "F-5");
            when(service.findAll()).thenReturn(List.of(VALID_RESPONSE, secondResponse));

            // When / Then
            mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].code").value("A-29"))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].code").value("F-5"));

            verify(service).findAll();
        }

        @Test
        @DisplayName("Deve responder com uma lista vazia quando não houver tipos de aeronave")
        void shouldReturnEmptyListWhenNoAircraftTypesExist() throws Exception {
            // Given
            when(service.findAll()).thenReturn(List.of());

            // When / Then
            mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

            verify(service).findAll();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/aircraft-types/{id}")
    class FindByIdEndpoint {

        @Test
        @DisplayName("Deve encontrar um tipo de aeronave pelo Id")
        void shouldFindAircraftTypeById() throws Exception {
            // Given
            when(service.findById(AIRCRAFT_TYPE_ID)).thenReturn(VALID_RESPONSE);

            // When / Then
            mockMvc.perform(get(ENDPOINT + "/{id}", AIRCRAFT_TYPE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.code").value("A-29"));

            verify(service).findById(AIRCRAFT_TYPE_ID);
        }

        @Test
        @DisplayName("Deve responder 404 quando o tipo de aeronave não for encontrado")
        void shouldReturnNotFoundWhenAircraftTypeDoesNotExist() throws Exception {
            // Given
            when(service.findById(AIRCRAFT_TYPE_ID))
                .thenThrow(new SGCCResourceNotFoundException("O tipo de aeronave não foi encontrado."));

            // When / Then
            mockMvc.perform(get(ENDPOINT + "/{id}", AIRCRAFT_TYPE_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("O tipo de aeronave não foi encontrado."))
                .andExpect(jsonPath("$.detail").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.type").value(containsString("/errors/")));

            verify(service).findById(AIRCRAFT_TYPE_ID);
        }

        @ParameterizedTest(name = "Deve responder 400 quando o Id for \"{0}\"")
        @ValueSource(strings = {"abc", "3.5", "1a", "1e3"})
        @DisplayName("Deve responder 400 quando o Id não for um número inteiro válido")
        void shouldReturnBadRequestWhenIdIsNotNumeric(String invalidId) throws Exception {
            // When / Then
            mockMvc.perform(get(ENDPOINT + "/{id}", invalidId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/aircraft-types/{id}")
    class UpdateEndpoint {

        @Test
        @DisplayName("Deve atualizar um tipo de aeronave")
        void shouldUpdateAircraftType() throws Exception {
            // Given
            AircraftTypeResponseDto updatedResponse = new AircraftTypeResponseDto(AIRCRAFT_TYPE_ID, "C-105");
            when(service.update(AIRCRAFT_TYPE_ID, VALID_REQUEST)).thenReturn(updatedResponse);

            // When / Then
            mockMvc.perform(put(ENDPOINT + "/{id}", AIRCRAFT_TYPE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.code").value("C-105"));

            verify(service).update(AIRCRAFT_TYPE_ID, VALID_REQUEST);
        }

        @ParameterizedTest(name = "Deve responder 400 quando o {0}")
        @MethodSource("dev.arcanus.api_sgcc.modules.ops_logs.controllers"
            + ".AircraftTypeControllerTestBodies#invalidRequestBodies")
        @DisplayName("Deve responder 400 quando os dados de atualização forem inválidos")
        void shouldReturnBadRequestWhenUpdateValidationFails(String description, String invalidBody)
            throws Exception {
            // When / Then
            mockMvc.perform(put(ENDPOINT + "/{id}", AIRCRAFT_TYPE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Deve responder 404 quando o tipo de aeronave não for encontrado")
        void shouldReturnNotFoundWhenUpdateAircraftTypeDoesNotExist() throws Exception {
            // Given
            when(service.update(AIRCRAFT_TYPE_ID, VALID_REQUEST))
                .thenThrow(new SGCCResourceNotFoundException("O tipo de aeronave não foi encontrado."));

            // When / Then
            mockMvc.perform(put(ENDPOINT + "/{id}", AIRCRAFT_TYPE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("O tipo de aeronave não foi encontrado."));

            verify(service).update(AIRCRAFT_TYPE_ID, VALID_REQUEST);
        }

        @Test
        @DisplayName("Deve responder 409 quando o código atualizado já pertencer a outro registro")
        void shouldReturnConflictWhenUpdatedCodeAlreadyExists() throws Exception {
            // Given
            when(service.update(AIRCRAFT_TYPE_ID, VALID_REQUEST))
                .thenThrow(new SGCCResourceAlreadyExists("O tipo de aeronave já existe."));

            // When / Then
            mockMvc.perform(put(ENDPOINT + "/{id}", AIRCRAFT_TYPE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Recurso já cadastrado"))
                .andExpect(jsonPath("$.detail").value("O tipo de aeronave já existe."));

            verify(service).update(AIRCRAFT_TYPE_ID, VALID_REQUEST);
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/aircraft-types/{id}")
    class DeleteEndpoint {

        @Test
        @DisplayName("Deve excluir um tipo de aeronave e responder 204 No Content")
        void shouldDeleteAircraftType() throws Exception {
            // When / Then
            mockMvc.perform(delete(ENDPOINT + "/{id}", AIRCRAFT_TYPE_ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

            verify(service).delete(AIRCRAFT_TYPE_ID);
        }

        @Test
        @DisplayName("Deve responder 404 quando o tipo de aeronave não for encontrado")
        void shouldReturnNotFoundWhenDeleteAircraftTypeDoesNotExist() throws Exception {
            // Given
            doThrow(new SGCCResourceNotFoundException("O tipo de aeronave não foi encontrado."))
                .when(service)
                .delete(AIRCRAFT_TYPE_ID);

            // When / Then
            mockMvc.perform(delete(ENDPOINT + "/{id}", AIRCRAFT_TYPE_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("O tipo de aeronave não foi encontrado."));

            verify(service).delete(AIRCRAFT_TYPE_ID);
        }
    }
}

/**
 * Fontes de dados compartilhadas entre os cenários aninhados: em @Nested o JUnit só
 * procura métodos de fábrica na própria classe aninhada, então ficam numa classe auxiliar
 * referenciada pelo nome totalmente qualificado.
 */
class AircraftTypeControllerTestBodies {

    private AircraftTypeControllerTestBodies() {}

    static Stream<Arguments> invalidRequestBodies() {
        return Stream.of(
            Arguments.of("codigo vazio", "{\"code\": \"\"}"),
            Arguments.of("codigo em branco", "{\"code\": \"   \"}"),
            Arguments.of("codigo nulo", "{\"code\": null}"),
            Arguments.of("codigo ausente", "{}")
        );
    }

    static Stream<Arguments> malformedJsonBodies() {
        return Stream.of(
            Arguments.of("chave sem valor", "{\"code\": }"),
            Arguments.of("texto fora do json", "a-29"),
            Arguments.of("json truncado", "{\"code\": \"a-29\"")
        );
    }
}
