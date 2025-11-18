package com.taba.auth.filter;

import com.taba.auth.service.TokenBlacklistService;
import com.taba.auth.util.JwtTokenProvider;
import com.taba.user.entity.User;
import com.taba.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        
        // permitAll 경로는 필터를 건너뛰기 (SecurityConfig에서 이미 처리됨)
        // 하지만 명시적으로 체크하여 불필요한 처리 방지
        if (shouldSkipFilter(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                // Redis 블랙리스트 체크 (Redis가 없어도 동작)
                try {
                    if (tokenBlacklistService.isBlacklisted(jwt)) {
                        log.debug("Token is blacklisted, skipping authentication");
                        filterChain.doFilter(request, response);
                        return;
                    }
                } catch (Exception e) {
                    log.debug("Redis blacklist check failed, continuing with token validation: {}", e.getMessage());
                }

                String userId = jwtTokenProvider.getUserIdFromToken(jwt);
                User user = userRepository.findActiveUserById(userId).orElse(null);

                if (user != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }
    
    private boolean shouldSkipFilter(String path) {
        // permitAll 경로 체크 (context-path 포함)
        return path.startsWith("/api/v1/auth/signup") ||
               path.startsWith("/api/v1/auth/login") ||
               path.startsWith("/api/v1/auth/forgot-password") ||
               path.startsWith("/api/v1/auth/reset-password") ||
               path.startsWith("/api/v1/swagger-ui") ||
               path.startsWith("/api/v1/v3/api-docs") ||
               path.startsWith("/api/v1/swagger-resources") ||
               path.startsWith("/api/v1/webjars") ||
               path.startsWith("/api/v1/actuator") ||
               path.startsWith("/api/v1/uploads") ||
               path.equals("/error") ||
               // context-path 없이도 체크
               path.startsWith("/auth/signup") ||
               path.startsWith("/auth/login") ||
               path.startsWith("/auth/forgot-password") ||
               path.startsWith("/auth/reset-password") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars") ||
               path.startsWith("/actuator") ||
               path.startsWith("/uploads") ||
               path.equals("/error");
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

