package com.notification.repository;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class DeviceTokenRepository {
    private final Set<String> tokens = new HashSet<>();

    public void addToken(String token) {
        tokens.add(token);
    }

    public Set<String> getAllTokens() {
        return Collections.unmodifiableSet(tokens);
    }

    public void removeToken(String token) {
        if (tokens.contains(token)) {
            tokens.remove(token);
        } else {
            throw new NoSuchElementException("Token not found: " + token);
        }
    }
}
