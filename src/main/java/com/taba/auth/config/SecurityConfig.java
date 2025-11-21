package com.taba.auth.config;

import com.taba.auth.filter.JwtAuthenticationFilter;
import com.taba.common.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/signup",
                                "/auth/login",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/webjars/**",
                                "/swagger-ui/index.html",
                                "/swagger-ui/swagger-initializer.js",
                                "/swagger-ui/favicon-32x32.png",
                                "/swagger-ui/favicon-16x16.png",
                                "/swagger-ui/swagger-ui-bundle.js",
                                "/swagger-ui/swagger-ui-standalone-preset.js",
                                "/swagger-ui/swagger-ui.css",
                                "/error",
                                "/actuator/**",  // actuator 엔드포인트 전체 허용 (헬스체크 포함)
                                "/uploads/**"  // 업로드된 이미지 파일 접근 허용
                        ).permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 요청 허용
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/v3/api-docs/**").permitAll() // OpenAPI 문서 허용
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            String language = getLanguageFromRequest(request);
                            String message = MessageUtil.getMessage("error.unauthorized", language);
                            response.setStatus(401);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(String.format("{\"success\":false,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"%s\"}}", message));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            String language = getLanguageFromRequest(request);
                            String message = MessageUtil.getMessage("error.forbidden", language);
                            response.setStatus(403);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(String.format("{\"success\":false,\"error\":{\"code\":\"FORBIDDEN\",\"message\":\"%s\"}}", message));
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 모든 Origin 허용 (패턴 사용)
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(List.of("*"));
        
        // 노출할 헤더
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", 
                "Content-Type", 
                "X-Total-Count",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Methods",
                "Access-Control-Allow-Headers"
        ));
        
        // Credentials 허용 여부 (Swagger UI에서 사용할 때는 false)
        configuration.setAllowCredentials(false);
        
        // Preflight 요청 캐시 시간
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // 모든 경로에 CORS 설정 적용
        source.registerCorsConfiguration("/**", configuration);
        
        // Swagger UI 관련 경로에 추가로 명시적 설정
        CorsConfiguration swaggerConfig = new CorsConfiguration();
        swaggerConfig.setAllowedOriginPatterns(List.of("*"));
        swaggerConfig.setAllowedMethods(Arrays.asList("GET", "OPTIONS", "HEAD"));
        swaggerConfig.setAllowedHeaders(List.of("*"));
        swaggerConfig.setAllowCredentials(false);
        swaggerConfig.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/v3/api-docs/**", swaggerConfig);
        source.registerCorsConfiguration("/swagger-ui/**", swaggerConfig);
        source.registerCorsConfiguration("/swagger-resources/**", swaggerConfig);
        source.registerCorsConfiguration("/webjars/**", swaggerConfig);
        
        return source;
    }
    
    /**
     * 요청에서 언어 정보를 가져옵니다.
     * Accept-Language 헤더 또는 사용자 언어 설정을 확인합니다.
     */
    private String getLanguageFromRequest(HttpServletRequest request) {
        // Accept-Language 헤더 확인
        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
            // Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8 형식 처리
            String[] languages = acceptLanguage.split(",");
            if (languages.length > 0) {
                String primaryLanguage = languages[0].split(";")[0].trim().toLowerCase();
                if (primaryLanguage.startsWith("ko")) {
                    return "ko";
                } else if (primaryLanguage.startsWith("en")) {
                    return "en";
                } else if (primaryLanguage.startsWith("ja")) {
                    return "ja";
                }
            }
        }
        return "ko"; // 기본값: 한국어
    }
}

