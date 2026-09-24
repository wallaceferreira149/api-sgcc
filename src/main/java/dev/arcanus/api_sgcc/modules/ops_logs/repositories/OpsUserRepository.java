package dev.arcanus.api_sgcc.modules.ops_logs.repositories;

import dev.arcanus.api_sgcc.modules.ops_logs.entities.OpsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpsUserRepository extends JpaRepository<OpsUser, Long> {
}
