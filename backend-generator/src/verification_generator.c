#include "verification_generator.h"
#include "file_util.h"
#include <stdio.h>

static void make_java_base(char *base, size_t n, const char *root)
{
    char dir[512];
    ensure_dir(root);
    snprintf(dir, sizeof(dir), "%s/src", root);                  ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main", root);             ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main/java", root);        ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main/java/com", root);    ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main/java/com/example", root); ensure_dir(dir);
    snprintf(base, n, "%s/src/main/java/com/example/app", root); ensure_dir(base);
}

void generate_verification(const AppConfig *cfg, const char *root)
{
    // If mail verification is disabled, we don't need these tokens
    if (!cfg->mail_verification) {
        return;
    }

    char base[512], authDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    // These entities belong in the 'auth' package
    snprintf(authDir, sizeof(authDir), "%s/auth", base);
    ensure_dir(authDir);

    TemplateData data;
    init_template_data(&data);

    // 1. VerificationToken.java
    snprintf(path, sizeof(path), "%s/VerificationToken.java", authDir);
    write_template_to_file("backend-generator/templates/VerificationToken.java.tpl", path, &data);

    // 2. VerificationTokenRepository.java
    snprintf(path, sizeof(path), "%s/VerificationTokenRepository.java", authDir);
    write_template_to_file("backend-generator/templates/VerificationTokenRepository.java.tpl", path, &data);

    free_template_data(&data);
    printf("Generated Verification Module from templates.\n");
}