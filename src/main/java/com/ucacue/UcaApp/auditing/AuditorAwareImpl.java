package com.ucacue.UcaApp.auditing;

import java.util.*;
import org.springframework.data.domain.AuditorAware;

import com.ucacue.UcaApp.model.entity.UserEntity;


public class AuditorAwareImpl implements AuditorAware<String>{

    @Override
    public Optional<String> getCurrentAuditor() {
        UserEntity currentUser = new UserEntity();
        String username = currentUser.getUsername();
        
        return Optional.ofNullable(username).or(() -> Optional.of("anonymousUser"));
    }
}
