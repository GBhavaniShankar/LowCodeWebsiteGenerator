#include "verification_generator.h"
#include "file_util.h"
#include <stdio.h>

static void make_java_base(char *base, size_t n, const char *root)
{
    char dir[512];

    ensure_dir(root);

    snprintf(dir, sizeof(dir), "%s/src", root);
    ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main", root);
    ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main/java", root);
    ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main/java/com", root);
    ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main/java/com/example", root);
    ensure_dir(dir);

    snprintf(base, n, "%s/src/main/java/com/example/app", root);
    ensure_dir(base);
}

void generate_verification(const AppConfig *cfg, const char *root)
{
    (void)cfg; // not needed at generation time; we always generate the classes

    char base[512], authDir[512], mailDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    // com/example/app/auth
    snprintf(authDir, sizeof(authDir), "%s/auth", base);
    ensure_dir(authDir);

    // com/example/app/mail
    snprintf(mailDir, sizeof(mailDir), "%s/mail", base);
    ensure_dir(mailDir);

    /* ===================== VerificationToken.java ===================== */
    snprintf(path, sizeof(path), "%s/VerificationToken.java", authDir);
    FILE *f = fopen(path, "w");
    if (!f)
    {
        perror("VerificationToken.java");
        return;
    }

    fprintf(f,
            "package com.example.app.auth;\n"
            "\n"
            "import com.example.app.user.User;\n"
            "import jakarta.persistence.*;\n"
            "\n"
            "import java.time.LocalDateTime;\n"
            "\n"
            "@Entity\n"
            "@Table(name = \"email_verification_tokens\")\n"
            "public class VerificationToken {\n"
            "    @Id\n"
            "    @GeneratedValue(strategy = GenerationType.IDENTITY)\n"
            "    private Long id;\n"
            "\n"
            "    @Column(nullable = false, unique = true, length = 64)\n"
            "    private String token;\n"
            "\n"
            "    @OneToOne(fetch = FetchType.EAGER)\n"
            "    @JoinColumn(name = \"user_id\", nullable = false, unique = true)\n"
            "    private User user;\n"
            "\n"
            "    @Column(nullable = false)\n"
            "    private LocalDateTime expiryDate;\n"
            "\n"
            "    public VerificationToken() {}\n"
            "\n"
            "    public VerificationToken(String token, User user, LocalDateTime expiryDate) {\n"
            "        this.token = token;\n"
            "        this.user = user;\n"
            "        this.expiryDate = expiryDate;\n"
            "    }\n"
            "\n"
            "    public Long getId() { return id; }\n"
            "    public String getToken() { return token; }\n"
            "    public User getUser() { return user; }\n"
            "    public LocalDateTime getExpiryDate() { return expiryDate; }\n"
            "\n"
            "    public boolean isExpired() {\n"
            "        return LocalDateTime.now().isAfter(expiryDate);\n"
            "    }\n"
            "}\n");
    fclose(f);

    /* ===================== VerificationTokenRepository.java ===================== */
    snprintf(path, sizeof(path), "%s/VerificationTokenRepository.java", authDir);
    f = fopen(path, "w");
    if (!f)
    {
        perror("VerificationTokenRepository.java");
        return;
    }

    fprintf(f,
            "package com.example.app.auth;\n"
            "\n"
            "import org.springframework.data.jpa.repository.JpaRepository;\n"
            "\n"
            "import java.util.Optional;\n"
            "\n"
            "public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {\n"
            "    Optional<VerificationToken> findByToken(String token);\n"
            "}\n");
    fclose(f);

    /* ===================== EmailService.java ===================== */
    snprintf(path, sizeof(path), "%s/EmailService.java", mailDir);
    f = fopen(path, "w");
    if (!f)
    {
        perror("EmailService.java");
        return;
    }

    fprintf(f,
            "package com.example.app.mail;\n"
            "\n"
            "public interface EmailService {\n"
            "    void sendEmail(String to, String subject, String text);\n"
            "}\n");
    fclose(f);

    /* ===================== EmailServiceImpl.java ===================== */
    snprintf(path, sizeof(path), "%s/EmailServiceImpl.java", mailDir);
    f = fopen(path, "w");
    if (!f)
    {
        perror("EmailServiceImpl.java");
        return;
    }

    fprintf(f,
            "package com.example.app.mail;\n"
            "\n"
            "import org.slf4j.Logger;\n"
            "import org.slf4j.LoggerFactory;\n"
            "import org.springframework.mail.SimpleMailMessage;\n"
            "import org.springframework.mail.javamail.JavaMailSender;\n"
            "import org.springframework.stereotype.Service;\n"
            "\n"
            "@Service\n"
            "public class EmailServiceImpl implements EmailService {\n"
            "\n"
            "    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);\n"
            "\n"
            "    private final JavaMailSender mailSender;\n"
            "\n"
            "    public EmailServiceImpl(JavaMailSender mailSender) {\n"
            "        this.mailSender = mailSender;\n"
            "    }\n"
            "\n"
            "    @Override\n"
            "    public void sendEmail(String to, String subject, String text) {\n"
            "        try {\n"
            "            SimpleMailMessage message = new SimpleMailMessage();\n"
            "            message.setTo(to);\n"
            "            message.setSubject(subject);\n"
            "            message.setText(text);\n"
            "            mailSender.send(message);\n"
            "            log.info(\"Verification email sent to {}\", to);\n"
            "        } catch (Exception e) {\n"
            "            log.error(\"Failed to send email to {}\", to, e);\n"
            "        }\n"
            "    }\n"
            "}\n");
    fclose(f);
}
