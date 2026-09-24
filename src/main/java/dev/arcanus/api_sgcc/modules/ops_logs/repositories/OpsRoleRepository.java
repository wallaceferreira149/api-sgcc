package dev.arcanus.api_sgcc.modules.ops_logs.repositories;

import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpsRoleRepository extends JpaRepository<OpsRole, Long> {
    boolean existsByNameIgnoreCase(String name);
}
