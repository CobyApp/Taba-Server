package com.taba.auth.config;

import com.taba.auth.filter.JwtAuthenticationFilter;
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
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\":false,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\"}}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\":false,\"error\":{\"code\":\"FORBIDDEN\",\"message\":\"권한이 없습니다.\"}}");
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
}

