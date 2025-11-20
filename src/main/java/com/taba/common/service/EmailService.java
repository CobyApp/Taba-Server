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
     */
    public void sendTemporaryPassword(String toEmail, String nickname, String temporaryPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[Taba] 임시 비밀번호가 발급되었습니다");
            message.setText(buildTemporaryPasswordEmailContent(nickname, temporaryPassword));
            
            mailSender.send(message);
            log.info("Temporary password email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send temporary password email to: {}", toEmail, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 비밀번호 재설정 링크를 이메일로 발송
     */
    public void sendPasswordResetLink(String toEmail, String nickname, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[Taba] 비밀번호 재설정 링크");
            message.setText(buildPasswordResetEmailContent(nickname, resetToken));
            
            mailSender.send(message);
            log.info("Password reset link email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset link email to: {}", toEmail, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    private String buildTemporaryPasswordEmailContent(String nickname, String temporaryPassword) {
        return String.format(
            "안녕하세요, %s님.\n\n" +
            "Taba에서 요청하신 임시 비밀번호가 발급되었습니다.\n\n" +
            "임시 비밀번호: %s\n\n" +
            "보안을 위해 로그인 후 반드시 비밀번호를 변경해주세요.\n\n" +
            "감사합니다.\n" +
            "Taba 팀",
            nickname, temporaryPassword
        );
    }

    private String buildPasswordResetEmailContent(String nickname, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        return String.format(
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
    }
}

