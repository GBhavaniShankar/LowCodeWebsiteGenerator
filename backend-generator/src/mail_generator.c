#include "mail_generator.h"
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

void generate_mail(const AppConfig *cfg, const char *root)
{
    // If config says No, skip generation
    if (!cfg->mail_verification) {
        return;
    }

    char base[512], mailDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(mailDir, sizeof(mailDir), "%s/mail", base);
    ensure_dir(mailDir);

    // Prepare Template Data (Empty, because the template uses Spring ${properties})
    TemplateData data;
    init_template_data(&data);

    // 1. Generate EmailService.java (Interface)
    snprintf(path, sizeof(path), "%s/EmailService.java", mailDir);
    write_template_to_file("backend-generator/templates/EmailService.java.tpl", path, &data);

    // 2. Generate EmailServiceImpl.java (Implementation)
    snprintf(path, sizeof(path), "%s/EmailServiceImpl.java", mailDir);
    write_template_to_file("backend-generator/templates/EmailServiceImpl.java.tpl", path, &data);

    free_template_data(&data);
    printf("Generated Mail Module from templates.\n");
}