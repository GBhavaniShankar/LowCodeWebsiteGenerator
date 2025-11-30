#include "java_core_generator.h"
#include "file_util.h"
#include <stdio.h>

/* Helper to ensure the base package structure exists */
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

void generate_core_app(const AppConfig *cfg, const char *root)
{
    (void)cfg; // Not needed for this simple file

    char base[512], output_path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(output_path, sizeof(output_path), "%s/Application.java", base);

    // Prepare Data (Empty for now, as it's a static file)
    TemplateData data;
    init_template_data(&data);

    // Execute Template
    write_template_to_file("backend-generator/templates/Application.java.tpl", output_path, &data);

    free_template_data(&data);
    printf("Generated Application.java from template.\n");
}