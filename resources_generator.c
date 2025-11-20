#include "resources_generator.h"
#include "file_util.h"
#include <stdio.h>

void generate_resources(const AppConfig *cfg, const char *root)
{
    char dir[512];
    char path[512];

    /* mybackend */
    ensure_dir(root);

    /* mybackend/src */
    snprintf(dir, sizeof(dir), "%s/src", root);
    ensure_dir(dir);

    /* mybackend/src/main */
    snprintf(dir, sizeof(dir), "%s/src/main", root);
    ensure_dir(dir);

    /* mybackend/src/main/resources */
    snprintf(dir, sizeof(dir), "%s/src/main/resources", root);
    ensure_dir(dir);

    /* mybackend/src/main/resources/application.yml */
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
            "      path: /h2-console\n"
            "\n"
            "  mail:\n"
            "    host: smtp-relay.brevo.com\n"
            "    port: 587\n"
            "    username: 9c0860001@smtp-brevo.com\n"
            "    password: xsmtpsib-60511cfbb7fec3c58e439840b79bf76bcf9567d2d84553aa14683429db1da6ef-UMmujCj4pM8Ez1Ra\n"
            "    properties:\n"
            "      mail:\n"
            "        smtp:\n"
            "          auth: true\n"
            "          starttls:\n"
            "            enable: true\n"
            "\n"
            "server:\n"
            "  port: 8080\n"
            "\n"
            "springdoc:\n"
            "  swagger-ui:\n"
            "    path: /swagger-ui.html\n"
            "\n"
            "app:\n");

    if (cfg->auth_type == AUTH_JWT)
    {
        fprintf(f,
                "  jwt:\n"
                "    secret: \"CHANGE_ME_TO_A_LONG_RANDOM_SECRET_KEY_32_CHARS_MINIMUM\"\n"
                "    expiration-ms: 3600000\n");
    }

    const char *adminEmail = cfg->admin_email ? cfg->admin_email : "admin@example.com";
    const char *adminPassword = cfg->admin_password ? cfg->admin_password : "admin123";

    fprintf(f,
            "\n"
            "  admin:\n"
            "    email: %s\n"
            "    password: %s\n"
            "\n"
            "  mail:\n"
            "    from: 112201026@smail.iitpkd.ac.in\n"
            "    verification-base-url: http://localhost:8080\n",
            adminEmail,
            adminPassword);

    fclose(f);
}
