package com.taba.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Taba API")
                        .description("Taba 백엔드 API 명세서")
                        .version("v1"))
                .servers(List.of(
                        new Server().url("/api/v1").description("로컬 서버 (상대 경로)"),
                        new Server().url("http://localhost:8080/api/v1").description("로컬 서버 (절대 경로)"),
                        new Server().url("http://cobyserver.iptime.org:8080/api/v1").description("프로덕션 서버")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .name("Bearer Authentication")));
    }
}

