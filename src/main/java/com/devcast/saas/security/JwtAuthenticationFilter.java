package com.devcast.saas.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            er
            String token = extractTokenFromRequest(request);

            if (StringUtils.hasText(token)) {

                String username = jwtService.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // <CHANGE> Create authentication token with user details
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("✅ User authenticated successfully: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("❌ JWT authentication failed: {}", e.getMessage());
        }

        // <CHANGE> Continue filter chain regardless of JWT validation result
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);

            if (!StringUtils.hasText(authHeader)) {
                log.debug("⚠️ Authorization header is missing");
                return null;
            }

            if (!authHeader.startsWith(BEARER_PREFIX)) {
                log.debug("⚠️ Authorization header does not start with Bearer");
                return null;
            }

            String token = authHeader.substring(BEARER_PREFIX.length());

            if (!StringUtils.hasText(token)) {
                log.debug("⚠️ Token is empty after removing Bearer prefix");
                return null;
            }

            log.debug("✅ Token extracted successfully from Authorization header");
            return token;
        } catch (Exception e) {
            log.error("❌ Error extracting token from request: {}", e.getMessage());
            return null;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register");
    }
}