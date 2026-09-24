package dev.arcanus.api_sgcc.modules.ops_logs.repositories;

import dev.arcanus.api_sgcc.modules.ops_logs.entities.OperationalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationalLogRepository extends JpaRepository<OperationalLog, Long> {
}
