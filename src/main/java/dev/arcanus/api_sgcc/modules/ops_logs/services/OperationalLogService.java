package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.modules.ops_logs.repositories.OperationalLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperationalLogService {

    private final OperationalLogRepository opsLogepository;
    private final OperationalLogMapper opsLogMapper;



    @Transactional
    public OperationalLogResponseDto create(OperationalLogRequestDto request) {

    }

}
