package dev.arcanus.api_sgcc.application.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
//        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return Optional.empty();
//        }
//
//        return Optional.ofNullable((Long) authentication.getPrincipal());
//        TODO: Após implementar o Spring Security, implementar o getCurrentAuditor()
        return Optional.of(1L);
    }
}
