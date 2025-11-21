package com.taba.common.config;

import com.taba.common.util.SecurityUtil;
import com.taba.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    @Bean
    public LocaleResolver localeResolver() {
        return new UserLanguageLocaleResolver();
    }

    /**
     * 사용자 언어 설정에 따라 Locale을 결정하는 커스텀 LocaleResolver
     */
    private static class UserLanguageLocaleResolver extends SessionLocaleResolver {
        @Override
        public Locale resolveLocale(HttpServletRequest request) {
            try {
                User currentUser = SecurityUtil.getCurrentUser();
                if (currentUser != null && currentUser.getLanguage() != null) {
                    String language = currentUser.getLanguage();
                    return switch (language.toLowerCase()) {
                        case "ko" -> Locale.KOREAN;
                        case "en" -> Locale.ENGLISH;
                        case "ja" -> Locale.JAPANESE;
                        default -> Locale.KOREAN;
                    };
                }
            } catch (Exception e) {
                // 사용자 정보를 가져올 수 없는 경우 기본값 사용
            }
            return Locale.KOREAN; // 기본값: 한국어
        }
    }
}

