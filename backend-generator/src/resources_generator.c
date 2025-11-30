#include "resources_generator.h"
#include "file_util.h"
#include <stdio.h>
#include <string.h>

void generate_resources(const AppConfig *cfg, const char *root)
{
    // 1. Setup Directories & Paths
    char dir[512];
    char output_path[512];

    /* Create directory structure: mybackend/src/main/resources */
    ensure_dir(root);
    snprintf(dir, sizeof(dir), "%s/src", root);              ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main", root);         ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main/resources", root); ensure_dir(dir);

    snprintf(output_path, sizeof(output_path), "%s/application.yml", dir);

    // 2. Prepare Data Context
    TemplateData data;
    init_template_data(&data);

    // --- A. SMTP Config (Defaults) ---
    // You can later pull these from cfg if you add them to the parser
    add_replacement(&data, "{{SmtpHost}}", "smtp-relay.brevo.com");
    add_replacement(&data, "{{SmtpPort}}", "587");
    add_replacement(&data, "{{SmtpUsername}}", "9c0860001@smtp-brevo.com");
    add_replacement(&data, "{{SmtpPassword}}", "xsmtpsib-60511cfbb7fec3c58e439840b79bf76bcf9567d2d84553aa14683429db1da6ef-UMmujCj4pM8Ez1Ra");

    // --- B. Admin & App Config ---
    const char *adminEmail = cfg->admin_email ? cfg->admin_email : "admin@example.com";
    const char *adminPass  = cfg->admin_password ? cfg->admin_password : "admin123";
    
    add_replacement(&data, "{{AdminEmail}}", adminEmail);
    add_replacement(&data, "{{AdminPassword}}", adminPass);
    add_replacement(&data, "{{MailFrom}}", "no-reply@agiletracker.com");
    add_replacement(&data, "{{VerificationBaseUrl}}", "http://localhost:8080");

    // --- C. Conditional JWT Config ---
    if (cfg->auth_type == AUTH_JWT) {
        const char *jwtConfig = 
            "  jwt:\n"
            "    secret: \"CHANGE_ME_TO_A_LONG_RANDOM_SECRET_KEY_32_CHARS_MINIMUM\"\n"
            "    expiration-ms: 3600000";
        add_replacement(&data, "{{JwtConfig}}", jwtConfig);
    } else {
        add_replacement(&data, "{{JwtConfig}}", "");
    }

    // 3. Execute Template Engine
    write_template_to_file("backend-generator/templates/application.yml.tpl", output_path, &data);

    // 4. Cleanup
    free_template_data(&data);

    printf("Generated application.yml from template.\n");
}