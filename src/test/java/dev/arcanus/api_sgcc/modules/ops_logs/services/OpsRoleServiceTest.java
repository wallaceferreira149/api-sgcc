package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCResourceAlreadyExists;
import dev.arcanus.api_sgcc.modules.ops_logs.dtos.OpsRoleRequestDto;
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

            when(opsRoleRepository.existsByName(validRequest.name()))
                .thenReturn(Boolean.FALSE);

            when(opsRoleMapper.toEntity(validRequest))
                .thenReturn(validOpsRole);

            when(opsRoleRepository.save(any(OpsRole.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            OpsRole opsRoleCreated = opsRoleService.create(validRequest);

            assertNotNull(opsRoleCreated);
            assertEquals(validOpsRole.getName(), opsRoleCreated.getName());
            assertEquals(validOpsRole.getDaysToExpire(), opsRoleCreated.getDaysToExpire());
            assertEquals("", opsRoleCreated.getDescription());
            verify(opsRoleRepository).save(validOpsRole);
        }

        @Test
        @DisplayName("Não deve criar qualificação operacional com nome já cadastrado")
        void shouldNotCreateOpsRoleWithDuplicatedName() {

            when(opsRoleRepository.existsByName(validRequest.name()))
                .thenReturn(Boolean.TRUE);

            assertThrows(SGCCResourceAlreadyExists.class,
                () -> opsRoleService.create(validRequest));

            verify(opsRoleRepository, never()).save(any(OpsRole.class));
        }
    }


}
