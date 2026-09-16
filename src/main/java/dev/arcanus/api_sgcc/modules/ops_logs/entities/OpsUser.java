package dev.arcanus.api_sgcc.modules.ops_logs.entities;

import dev.arcanus.api_sgcc.domain.entities.BaseEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ops_users")
public class OpsUser extends BaseEntity {

    @OneToMany(mappedBy = "operator", fetch = FetchType.LAZY)
    private Set<OperationalLog> operationalLogsAsOperator = new HashSet<>();

    @OneToMany(mappedBy = "assistant", fetch = FetchType.LAZY)
    private Set<OperationalLog> operationalLogsAsAssistant = new HashSet<>();

    @OneToMany(mappedBy = "instructor", fetch = FetchType.LAZY)
    private Set<OperationalLog> operationalLogsAsInstructor = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ops_role_id")
    private OpsRole opsRole;

}
