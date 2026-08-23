package com.apms.mdm.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SecurityProbeController {

    @GetMapping("/api/v1/security/probe")
    public Map<String, Object> probe(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();
        return Map.of(
                "authenticated", authentication.isAuthenticated(),
                "principal", authentication.getName(),
                "authorities", authorities
        );
    }
}
