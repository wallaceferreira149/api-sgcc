package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceNotFoundException;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleRequestDto;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleResponseDto;
import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsRole;
import dev.arcanus.api_sgcc.modules.ops_logs.mappers.OpsRoleMapper;
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
class OpsRoleServiceTest {

    @Mock private OpsRoleRepository opsRoleRepository;
    @Mock private OpsRoleMapper opsRoleMapper;
    @InjectMocks private OpsRoleService opsRoleService;


    @Nested
    @DisplayName("Caso de uso: criar qualificação operacional")
    class CreateOpsRoleUseCase {

        private OpsRoleRequestDto validRequest;
        private OpsRole validOpsRole;

        @BeforeEach
        void setUp() {
            validRequest = new OpsRoleRequestDto("cc", null, 120L);
            validOpsRole = new OpsRole("cc", null, 120L);
        }

        @Test
        @DisplayName("Deve criar uma qualificação operacional com sucesso")
        void shouldCreateOpsRoleSucessfully() {

            when(opsRoleRepository.existsByNameIgnoreCase("CC"))
                .thenReturn(Boolean.FALSE);

            when(opsRoleMapper.toEntity(validRequest))
                .thenReturn(validOpsRole);

            when(opsRoleMapper.toResponse(validOpsRole))
                .thenReturn(new OpsRoleResponseDto(null, "CC", ""));

            when(opsRoleRepository.save(any(OpsRole.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            OpsRoleResponseDto opsRoleCreated = opsRoleService.create(validRequest);

            assertNotNull(opsRoleCreated);
            assertEquals(validOpsRole.getName(), opsRoleCreated.name());
            assertEquals("", opsRoleCreated.description());
            verify(opsRoleRepository).existsByNameIgnoreCase("CC");
            verify(opsRoleRepository).save(validOpsRole);
        }

        @Test
        @DisplayName("Não deve criar qualificação operacional com nome já cadastrado")
        void shouldNotCreateOpsRoleWithDuplicatedName() {

            when(opsRoleRepository.existsByNameIgnoreCase("CC"))
                .thenReturn(Boolean.TRUE);

            assertThrows(SGCCResourceAlreadyExists.class,
                () -> opsRoleService.create(validRequest));

            verify(opsRoleRepository, never()).save(any(OpsRole.class));
        }
    }

    @Nested
    @DisplayName("Caso de uso: listar qualificações operacionais")
    class ListOpsRolesUseCase {

        private OpsRole validOpsRoleCC = new OpsRole("cc", null, 120L);
        private OpsRole validOpsRoleAJCC = new OpsRole("ajcc", null, 120L);
        private OpsRole validOpsRoleCOAM = new OpsRole("coam", null, 60L);

        private OpsRoleResponseDto validOpsRoleResponseCC = new OpsRoleResponseDto(1L, "CC", null);
        private OpsRoleResponseDto validOpsRoleResponseAJCC = new OpsRoleResponseDto(2L, "AJCC", null);
        private OpsRoleResponseDto validOpsRoleResponseCOAM = new OpsRoleResponseDto(3L, "COAM", null);


        @Test
        @DisplayName("Deve listar todas as qualificações operacionais")
        void shouldListAllOpsRoles() {

            when(opsRoleMapper.toResponse(validOpsRoleCC)).thenReturn(validOpsRoleResponseCC);
            when(opsRoleMapper.toResponse(validOpsRoleAJCC)).thenReturn(validOpsRoleResponseAJCC);
            when(opsRoleMapper.toResponse(validOpsRoleCOAM)).thenReturn(validOpsRoleResponseCOAM);

            when(opsRoleRepository.findAll())
                .thenReturn(List.of(validOpsRoleCC, validOpsRoleAJCC, validOpsRoleCOAM));


            List<OpsRoleResponseDto> opsRoles = opsRoleService.findAll();

            assertEquals(3, opsRoles.size());
            assertEquals(validOpsRoleCC.getName(), opsRoles.getFirst().name());
        }

        @Test
        @DisplayName("Deve retornar um array vazio quando não existir qualificações operacionais")
        void shouldReturnEmptyArrayWhenNoOpsRoles() {
            when(opsRoleService.findAll())
                .thenReturn(List.of());

            List<OpsRoleResponseDto> opsRoles = opsRoleService.findAll();

            assertEquals(0, opsRoles.size());
        }

        @Test
        @DisplayName("Deve encontrar uma qualificação operacional pelo ID")
        void shouldFindOpsRoleById() {
            List<OpsRole> opsRoleList = List.of(validOpsRoleCC, validOpsRoleAJCC, validOpsRoleCOAM);
            when(opsRoleMapper.toResponse(validOpsRoleCC)).thenReturn(new OpsRoleResponseDto(1L, "CC", null));
            when(opsRoleRepository.findById(1L)).thenReturn(Optional.of(opsRoleList.getFirst()));

            OpsRoleResponseDto opsRole = opsRoleService.findById(1L);

            assertEquals(validOpsRoleCC.getName(), opsRole.name());
            assertEquals(1L, opsRole.id());
        }

        @Test
        @DisplayName("Deve lançar exceção quando Id não for encontrado")
        void shouldThrowExceptionWhenIdNotFound() {
            when(opsRoleRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(SGCCResourceNotFoundException.class,
                () -> opsRoleService.findById(1L));
        }
    }

    @Nested
    @DisplayName("Caso de uso: atualizar qualificações operacionais")
    class UpdateOpsRoleUseCase {

        private OpsRole validOpsRoleCC = new OpsRole("cc", null, 120L);
        private OpsRole updatedOpsRoleCC = new OpsRole("CC", "chefe controlador", 60L);
        private OpsRoleRequestDto request = new OpsRoleRequestDto("cc", "Chefe Controlador", 60L);
        private OpsRoleResponseDto response = new OpsRoleResponseDto(1L, "CC", "Chefe Controlador");

        @Test
        @DisplayName("Deve atualizar qualificação operacional com sucesso")
        void shouldUpdateOpsRoleSuccessfully() {
            when(opsRoleMapper.toResponse(validOpsRoleCC)).thenReturn(response);
            when(opsRoleRepository.findById(1L)).thenReturn(Optional.of(validOpsRoleCC));
            when(opsRoleRepository.save(validOpsRoleCC)).thenReturn(updatedOpsRoleCC);

            OpsRoleResponseDto opsRoleUpdated = opsRoleService.update(1L, request);

            assertEquals(response.id(), opsRoleUpdated.id());
            assertEquals(response.name(), opsRoleUpdated.name());
            assertEquals(response.description(), opsRoleUpdated.description());
        }

        @Test
        @DisplayName("Não deve atualizar uma Ordem de Instrução com código inválido")
        void shouldNotUpdateOpsRoleWithWrogId() {
            when(opsRoleRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(SGCCResourceNotFoundException.class, () -> {
                OpsRoleResponseDto opsRoleUpdated = opsRoleService.update(1L, request);
            });

        }
    }

    @Nested
    @DisplayName("Caso de uso: deletar qualificações operacionais")
    class DeleteOpsRoleUseCase {

        @Test
        @DisplayName("Deve deletar uma qualificação operacional com sucesso")
        void shouldDeleteOpsRoleSuccessfully() {
            when(opsRoleRepository.existsById(1L))
                .thenReturn(true);

            opsRoleService.delete(1L);

            verify(opsRoleRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Não deve atualizar qualificação operacional quando o Id não for encontrado")
        void shouldNotDeleteWhenIdWrong() {
            when(opsRoleRepository.existsById(1L)).thenReturn(false);

            assertThrows(SGCCResourceNotFoundException.class, () -> {
                opsRoleService.delete(1L);
            });
        }
    }

}
