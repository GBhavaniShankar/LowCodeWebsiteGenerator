#include "resources_generator.h"
#include "file_util.h"
#include <stdio.h>

void generate_resources(const AppConfig *cfg, const char *root)
{
    char dir[512];
    char path[512];

    ensure_dir(root);

    snprintf(dir, sizeof(dir), "%s/src", root);
    ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main", root);
    ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main/resources", root);
    ensure_dir(dir);

    snprintf(path, sizeof(path), "%s/application.yml", dir);

    FILE *f = fopen(path, "w");
    if (!f)
    {
        perror("application.yml");
        return;
    }

    fprintf(f,
            "spring:\n"
            "  datasource:\n"
            "    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE\n"
            "    driver-class-name: org.h2.Driver\n"
            "    username: sa\n"
            "    password: \n"
            "  jpa:\n"
            "    hibernate:\n"
            "      ddl-auto: update\n"
            "    show-sql: true\n"
            "  h2:\n"
            "    console:\n"
            "      enabled: true\n"
            "      path: /h2-console\n");

    if (cfg->mail_verification)
    {
        fprintf(f,
                "  mail:\n"
                "    host: smtp-relay.brevo.com\n"
                "    port: 587\n"
                "    username: 9c0860001@smtp-brevo.com\n"
                "    password: xsmtpsib-60511cfbb7fec3c58e439840b79bf76bcf9567d2d84553aa14683429db1da6ef-UMmujCj4pM8Ez1Ra\n"
                "    properties:\n"
                "      mail.smtp.auth: true\n"
                "      mail.smtp.starttls.enable: true\n");
    }

    fprintf(f,
            "\n"
            "server:\n"
            "  port: 8080\n"
            "\n"
            "springdoc:\n"
            "  swagger-ui:\n"
            "    path: /swagger-ui.html\n");

    if (cfg->auth_type == AUTH_JWT)
    {
        fprintf(f,
                "\n"
                "app:\n"
                "  jwt:\n"
                "    secret: \"CHANGE_ME_TO_A_LONG_RANDOM_SECRET_KEY_32_CHARS_MINIMUM\"\n"
                "    expiration-ms: 3600000\n");
        if (cfg->mail_verification)
        {
            fprintf(f,
                    "  mail:\n"
                    "    from: \"112201026@smail.iitpkd.ac.in\"\n"
                    "    verification-base-url: \"http://localhost:8080/api/auth/verify?token=\"\n");
        }
    }
    else if (cfg->mail_verification)
    {
        fprintf(f,
                "\n"
                "app:\n"
                "  mail:\n"
                "    from: \"no-reply@example.com\"\n"
                "    verification-base-url: \"http://localhost:8080/api/auth/verify?token=\"\n");
    }

    fclose(f);
}
