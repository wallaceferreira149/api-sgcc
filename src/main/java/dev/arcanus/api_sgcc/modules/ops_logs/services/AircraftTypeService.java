package dev.arcanus.api_sgcc.modules.ops_logs.services;

import dev.arcanus.api_sgcc.modules.ops_logs.repositories.AircraftTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class AircraftTypeService {

    private final AircraftTypeRepository repository;

    public AircraftTypeService(AircraftTypeRepository repository) {
        this.repository = repository;
    }

}
