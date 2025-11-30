#include "mail_generator.h"
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

void generate_mail(const AppConfig *cfg, const char *root)
{
    if (!cfg->mail_verification)
    {
        // nothing to generate if email verification is off
        return;
    }

    char base[512], mailDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(mailDir, sizeof(mailDir), "%s/mail", base);
    ensure_dir(mailDir);

    // EmailService.java
    snprintf(path, sizeof(path), "%s/EmailService.java", mailDir);
    FILE *f = fopen(path, "w");
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

    // EmailServiceImpl.java
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
            "            SimpleMailMessage msg = new SimpleMailMessage();\n"
            "            msg.setTo(to);\n"
            "            msg.setSubject(subject);\n"
            "            msg.setText(text);\n"
            "            mailSender.send(msg);\n"
            "            log.info(\"Verification email sent to {}\", to);\n"
            "        } catch (Exception e) {\n"
            "            log.error(\"Failed to send email to {}\", to, e);\n"
            "        }\n"
            "    }\n"
            "}\n");
    fclose(f);
}
