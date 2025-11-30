package com.example.app.mail;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
    void sendVerificationEmail(String to, String token);
}