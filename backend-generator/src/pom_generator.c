#include "pom_generator.h"
#include "file_util.h"
#include <stdio.h>
#include <string.h>

void generate_pom(const AppConfig *cfg, const char *root)
{
    // 1. Setup Paths
    char output_path[512];
    snprintf(output_path, sizeof(output_path), "%s/pom.xml", root);
    ensure_dir(root);

    // 2. Prepare Data Context
    TemplateData data;
    init_template_data(&data);

    const char *appName = cfg->project_name ? cfg->project_name : "demo-app";
    add_replacement(&data, "{{ArtifactId}}", appName);
    add_replacement(&data, "{{Name}}", appName);

    // 3. Handle Conditional Dependencies (JWT)
    if (cfg->auth_type == AUTH_JWT) {
        const char *jwtDeps = 
            "    \n"
            "    <dependency>\n"
            "      <groupId>io.jsonwebtoken</groupId>\n"
            "      <artifactId>jjwt-api</artifactId>\n"
            "      <version>0.11.5</version>\n"
            "    </dependency>\n"
            "    <dependency>\n"
            "      <groupId>io.jsonwebtoken</groupId>\n"
            "      <artifactId>jjwt-impl</artifactId>\n"
            "      <version>0.11.5</version>\n"
            "      <scope>runtime</scope>\n"
            "    </dependency>\n"
            "    <dependency>\n"
            "      <groupId>io.jsonwebtoken</groupId>\n"
            "      <artifactId>jjwt-jackson</artifactId>\n"
            "      <version>0.11.5</version>\n"
            "      <scope>runtime</scope>\n"
            "    </dependency>";
        add_replacement(&data, "{{JwtDependencies}}", jwtDeps);
    } else {
        add_replacement(&data, "{{JwtDependencies}}", "");
    }

    // 4. Handle Conditional Dependencies (Mail)
    if (cfg->mail_verification) {
        const char *mailDeps = 
            "    \n"
            "    <dependency>\n"
            "      <groupId>org.springframework.boot</groupId>\n"
            "      <artifactId>spring-boot-starter-mail</artifactId>\n"
            "    </dependency>";
        add_replacement(&data, "{{MailDependencies}}", mailDeps);
    } else {
        add_replacement(&data, "{{MailDependencies}}", "");
    }

    // 5. Execute Template Engine
    // Assumes you run ./backendgen from the project root
    write_template_to_file("backend-generator/templates/pom.xml.tpl", output_path, &data);

    // 6. Cleanup
    free_template_data(&data);
    
    printf("Generated pom.xml from template.\n");
}