package com.example.app.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String verificationBaseUrl;

    public EmailServiceImpl(JavaMailSender mailSender,
                            @Value("${app.mail.from}") String fromAddress,
                            @Value("${app.mail.verification-base-url}") String verificationBaseUrl) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.verificationBaseUrl = verificationBaseUrl;
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
        }
    }

    @Override
    public void sendVerificationEmail(String to, String token) {
        String link = verificationBaseUrl + "/api/auth/verify?token=" + token;
        String text = "Please click the link to verify your account: " + link;
        sendEmail(to, "Verify your email", text);
    }
}