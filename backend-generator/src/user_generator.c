#include "user_generator.h"
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

void generate_user(const AppConfig *cfg, const char *root)
{
    (void)cfg; // Config not needed for standard User files

    char base[512], userDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(userDir, sizeof(userDir), "%s/user", base);
    ensure_dir(userDir);

    TemplateData data;
    init_template_data(&data);

    // 1. Role.java
    snprintf(path, sizeof(path), "%s/Role.java", userDir);
    write_template_to_file("backend-generator/templates/Role.java.tpl", path, &data);

    // 2. User.java
    snprintf(path, sizeof(path), "%s/User.java", userDir);
    write_template_to_file("backend-generator/templates/User.java.tpl", path, &data);

    // 3. UserRepository.java
    snprintf(path, sizeof(path), "%s/UserRepository.java", userDir);
    write_template_to_file("backend-generator/templates/UserRepository.java.tpl", path, &data);

    free_template_data(&data);
    printf("Generated User Module from templates.\n");
}