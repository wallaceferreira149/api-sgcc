package dev.arcanus.api_sgcc.modules.ops_logs.repositories;

import dev.arcanus.api_sgcc.modules.ops_logs.entities.AircraftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AircraftTypeRepository extends JpaRepository<AircraftType, Long> {

    /**
     * O mesmo tipo de aeronave pode ser informado de formas diferentes: com ou sem
     * hifen (A-29 / A29 / A 29) e com maiusculas ou minusculas. A coluna code tem
     * unique, portanto ela sozinha nao impede "A-29" e "A29" de virar dois registros.
     * Estas queries comparam a chave do codigo (sem hifens e sem espaco, em
     * maiusculas) para garantir um unico registro por tipo de aeronave. O parametro
     * tambem e normalizado na query, para o resultado nao depender de o chamador ter
     * normalizado o codigo antes.
     */
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM AircraftType a
        WHERE REPLACE(REPLACE(UPPER(a.code), '-', ''), ' ', '')
            = REPLACE(REPLACE(UPPER(:codeKey), '-', ''), ' ', '')
        """)
    boolean existsByCodeKey(@Param("codeKey") String codeKey);

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM AircraftType a
        WHERE REPLACE(REPLACE(UPPER(a.code), '-', ''), ' ', '')
            = REPLACE(REPLACE(UPPER(:codeKey), '-', ''), ' ', '')
          AND a.id <> :id
        """)
    boolean existsByCodeKeyAndIdNot(@Param("codeKey") String codeKey, @Param("id") Long id);
}
