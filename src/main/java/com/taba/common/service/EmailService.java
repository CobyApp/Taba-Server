package com.taba.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * 임시 비밀번호를 이메일로 발송
     * @param toEmail 수신자 이메일
     * @param nickname 사용자 닉네임
     * @param temporaryPassword 임시 비밀번호
     * @param language 사용자 언어 설정 (ko, en, ja)
     */
    public void sendTemporaryPassword(String toEmail, String nickname, String temporaryPassword, String language) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(getTemporaryPasswordSubject(language));
            message.setText(buildTemporaryPasswordEmailContent(nickname, temporaryPassword, language));
            
            mailSender.send(message);
            log.info("Temporary password email sent successfully to: {} (language: {})", toEmail, language);
        } catch (Exception e) {
            log.error("Failed to send temporary password email to: {}", toEmail, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 비밀번호 재설정 링크를 이메일로 발송
     * @param toEmail 수신자 이메일
     * @param nickname 사용자 닉네임
     * @param resetToken 재설정 토큰
     * @param language 사용자 언어 설정 (ko, en, ja)
     */
    public void sendPasswordResetLink(String toEmail, String nickname, String resetToken, String language) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(getPasswordResetSubject(language));
            message.setText(buildPasswordResetEmailContent(nickname, resetToken, language));
            
            mailSender.send(message);
            log.info("Password reset link email sent successfully to: {} (language: {})", toEmail, language);
        } catch (Exception e) {
            log.error("Failed to send password reset link email to: {}", toEmail, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    private String getTemporaryPasswordSubject(String language) {
        if (language == null) language = "ko";
        return switch (language) {
            case "en" -> "[Taba] Temporary Password Issued";
            case "ja" -> "[Taba] 仮パスワードが発行されました";
            default -> "[Taba] 임시 비밀번호가 발급되었습니다";
        };
    }

    private String getPasswordResetSubject(String language) {
        if (language == null) language = "ko";
        return switch (language) {
            case "en" -> "[Taba] Password Reset Link";
            case "ja" -> "[Taba] パスワードリセットリンク";
            default -> "[Taba] 비밀번호 재설정 링크";
        };
    }

    private String buildTemporaryPasswordEmailContent(String nickname, String temporaryPassword, String language) {
        if (language == null) language = "ko";
        return switch (language) {
            case "en" -> String.format(
                "Hello, %s.\n\n" +
                "A temporary password has been issued for your Taba account.\n\n" +
                "Temporary Password: %s\n\n" +
                "Please change your password after logging in for security purposes.\n\n" +
                "Thank you.\n" +
                "Taba Team",
                nickname, temporaryPassword
            );
            case "ja" -> String.format(
                "こんにちは、%sさん。\n\n" +
                "Tabaからご要望いただいた仮パスワードが発行されました。\n\n" +
                "仮パスワード: %s\n\n" +
                "セキュリティのため、ログイン後必ずパスワードを変更してください。\n\n" +
                "ありがとうございます。\n" +
                "Tabaチーム",
                nickname, temporaryPassword
            );
            default -> String.format(
                "안녕하세요, %s님.\n\n" +
                "Taba에서 요청하신 임시 비밀번호가 발급되었습니다.\n\n" +
                "임시 비밀번호: %s\n\n" +
                "보안을 위해 로그인 후 반드시 비밀번호를 변경해주세요.\n\n" +
                "감사합니다.\n" +
                "Taba 팀",
                nickname, temporaryPassword
            );
        };
    }

    private String buildPasswordResetEmailContent(String nickname, String resetToken, String language) {
        if (language == null) language = "ko";
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        return switch (language) {
            case "en" -> String.format(
                "Hello, %s.\n\n" +
                "You have requested a password reset for your Taba account.\n\n" +
                "Click the link below to reset your password:\n" +
                "%s\n\n" +
                "This link is valid for 1 hour.\n" +
                "If you did not request a password reset, please ignore this email.\n\n" +
                "Thank you.\n" +
                "Taba Team",
                nickname, resetLink
            );
            case "ja" -> String.format(
                "こんにちは、%sさん。\n\n" +
                "Tabaでパスワードリセットをリクエストされました。\n\n" +
                "以下のリンクをクリックしてパスワードをリセットできます:\n" +
                "%s\n\n" +
                "このリンクは1時間有効です。\n" +
                "パスワードリセットをリクエストしていない場合は、このメールを無視してください。\n\n" +
                "ありがとうございます。\n" +
                "Tabaチーム",
                nickname, resetLink
            );
            default -> String.format(
                "안녕하세요, %s님.\n\n" +
                "Taba에서 비밀번호 재설정을 요청하셨습니다.\n\n" +
                "아래 링크를 클릭하여 비밀번호를 재설정하실 수 있습니다:\n" +
                "%s\n\n" +
                "이 링크는 1시간 동안 유효합니다.\n" +
                "만약 비밀번호 재설정을 요청하지 않으셨다면, 이 이메일을 무시하셔도 됩니다.\n\n" +
                "감사합니다.\n" +
                "Taba 팀",
                nickname, resetLink
            );
        };
    }
}

