package com.taba.common.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtil {

    private static MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    /**
     * 사용자 언어에 따라 메시지를 가져옵니다.
     * 
     * @param code 메시지 코드
     * @param language 사용자 언어 (ko, en, ja)
     * @param args 메시지 파라미터
     * @return 로컬라이징된 메시지
     */
    public static String getMessage(String code, String language, Object... args) {
        Locale locale = getLocaleFromLanguage(language);
        return messageSource.getMessage(code, args, code, locale);
    }

    /**
     * 현재 요청의 Locale을 기반으로 메시지를 가져옵니다.
     * 
     * @param code 메시지 코드
     * @param args 메시지 파라미터
     * @return 로컬라이징된 메시지
     */
    public static String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, code, locale);
    }

    /**
     * 언어 코드를 Locale로 변환합니다.
     * 
     * @param language 언어 코드 (ko, en, ja)
     * @return Locale 객체
     */
    private static Locale getLocaleFromLanguage(String language) {
        if (language == null || language.isEmpty()) {
            return Locale.KOREAN; // 기본값: 한국어
        }

        return switch (language.toLowerCase()) {
            case "ko" -> Locale.KOREAN;
            case "en" -> Locale.ENGLISH;
            case "ja" -> Locale.JAPANESE;
            default -> Locale.KOREAN; // 기본값: 한국어
        };
    }
}

