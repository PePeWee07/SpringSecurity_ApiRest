package com.ucacue.UcaApp.service.auditing.springboot.impl;

import java.util.*;
import org.springframework.data.domain.AuditorAware;

import com.ucacue.UcaApp.model.entity.UserEntity;

public class AuditorAwareImpl implements AuditorAware<String> {

    @SuppressWarnings("null")
    @Override
    public Optional<String> getCurrentAuditor() {
        UserEntity currentUser = new UserEntity();
        String username = currentUser.getUsername();

        return Optional.ofNullable(username).or(() -> Optional.of("anonymousUser"));
    }
}
