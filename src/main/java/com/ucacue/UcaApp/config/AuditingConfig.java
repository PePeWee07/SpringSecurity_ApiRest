package com.ucacue.UcaApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.ucacue.UcaApp.auditing.AuditorAwareImpl;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<String> currentSuditorProvider() {
        return new AuditorAwareImpl();
    }
}
