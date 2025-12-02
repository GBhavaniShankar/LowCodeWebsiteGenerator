#include "resources_generator.h"
#include "file_util.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* --- Helper: Read a specific key from a local .env file --- */
/* Returns 1 if found (and writes to dest), 0 if not found */
int get_env_from_file(const char *filename, const char *key, char *dest, size_t size) {
    FILE *f = fopen(filename, "r");
    if (!f) return 0;

    char line[1024];
    int found = 0;
    size_t key_len = strlen(key);

    while (fgets(line, sizeof(line), f)) {
        // Check if line starts with KEY=
        if (strncmp(line, key, key_len) == 0 && line[key_len] == '=') {
            // Extract value
            char *val = line + key_len + 1;
            // Trim newline
            val[strcspn(val, "\r\n")] = 0;
            
            strncpy(dest, val, size - 1);
            dest[size - 1] = '\0'; // Ensure null termination
            found = 1;
            break;
        }
    }
    fclose(f);
    return found;
}

void generate_env_file(const AppConfig *cfg, const char *root) {
    char path[512];
    snprintf(path, sizeof(path), "%s/.env", root);
    
    FILE *f = fopen(path, "w");
    if (!f) return;

    // Buffers for values
    char smtp_host[256] = "";
    char smtp_port[256] = "";
    char smtp_user[256] = "";
    char smtp_pass[256] = "";
    char mail_from[256] = "";

    // 1. Try to load from local .env file
    // We assume the generator is run from the project root where .env exists
    if (!get_env_from_file(".env", "SMTP_HOST", smtp_host, sizeof(smtp_host))) 
        strcpy(smtp_host, ""); // Fallback empty
        
    if (!get_env_from_file(".env", "SMTP_PORT", smtp_port, sizeof(smtp_port))) 
        strcpy(smtp_port, "587"); // Default port
        
    if (!get_env_from_file(".env", "SMTP_USERNAME", smtp_user, sizeof(smtp_user))) 
        strcpy(smtp_user, "");
        
    if (!get_env_from_file(".env", "SMTP_PASSWORD", smtp_pass, sizeof(smtp_pass))) 
        strcpy(smtp_pass, "");
    
    if (!get_env_from_file(".env", "MAIL_FROM", mail_from, sizeof(mail_from))) 
        strcpy(mail_from, "no-reply@default.com"); // Fallback if missing

    // 2. Write to the generated project's .env
    fprintf(f, "# Generated Environment Configuration\n");
    fprintf(f, "SMTP_HOST=%s\n", smtp_host);
    fprintf(f, "SMTP_PORT=%s\n", smtp_port);
    fprintf(f, "SMTP_USERNAME=%s\n", smtp_user);
    fprintf(f, "SMTP_PASSWORD=%s\n", smtp_pass);
    
    // 3. Write Admin Credentials (from Config File)
    fprintf(f, "ADMIN_EMAIL=%s\n", cfg->admin_email ? cfg->admin_email : "admin@example.com");
    fprintf(f, "ADMIN_PASSWORD=%s\n", cfg->admin_password ? cfg->admin_password : "admin123");

    fprintf(f, "MAIL_FROM=%s\n", mail_from);
    
    fclose(f);
    printf("Generated .env file (Loaded secrets from local .env).\n");
}

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

    // 2. Prepare Data Context for Template
    TemplateData data;
    init_template_data(&data);

    // Handle JWT Config Block
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

    // 4. Cleanup Template Data
    free_template_data(&data);

    printf("Generated application.yml from template.\n");

    // 5. Generate the corresponding .env file
    generate_env_file(cfg, root);
}