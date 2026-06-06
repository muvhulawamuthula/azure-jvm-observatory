package com.muvhulawa.observatory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Guards the destructive stress-lab endpoints ({@code /api/load/**}) behind a
 * shared API key supplied in the {@code X-Stress-Key} header.
 *
 * <p>Fails closed: if no key is configured (env var {@code STRESS_LAB_KEY}),
 * every stress-lab request is rejected. This keeps a publicly reachable
 * deployment safe by default rather than relying on the operator remembering to
 * set the key. The key comparison is constant-time to avoid leaking it through
 * response timing.
 *
 * <p>Only {@code /api/load/**} is filtered; the live-metrics API, Prometheus
 * endpoint, and dashboard remain open.
 */
@Component
public class StressLabKeyFilter extends OncePerRequestFilter {

    static final String HEADER = "X-Stress-Key";
    private static final String PROTECTED_PREFIX = "/api/load/";

    private final String configuredKey;

    public StressLabKeyFilter(@Value("${stresslab.key:}") String configuredKey) {
        this.configuredKey = configuredKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(PROTECTED_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!isAuthorized(request.getHeader(HEADER))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Missing or invalid " + HEADER + " header");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthorized(String providedKey) {
        // Fail closed: an unconfigured key means the stress lab is locked, not open.
        if (configuredKey == null || configuredKey.isBlank() || providedKey == null) {
            return false;
        }

        return MessageDigest.isEqual(
                configuredKey.getBytes(StandardCharsets.UTF_8),
                providedKey.getBytes(StandardCharsets.UTF_8));
    }
}
