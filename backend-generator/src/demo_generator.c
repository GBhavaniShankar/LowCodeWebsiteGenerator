#include "demo_generator.h"
#include "file_util.h"
#include <stdio.h>

static void make_java_base(char *base, size_t n, const char *root)
{
    char dir[512];
    ensure_dir(root);
    // ... ensure standard path ...
    snprintf(dir, sizeof(dir), "%s/src/main/java/com/example/app", root);
    // We assume the structure is created by java_core_generator, but good to ensure
    ensure_dir(dir); 
    snprintf(base, n, "%s", dir);
}

void generate_demo(const AppConfig *cfg, const char *root)
{
    (void)cfg;

    char base[512], output_path[512];
    // Reconstruct the path manually or use the helper if you made it shared
    // For simplicity, we just rebuild the string logic here
    snprintf(base, sizeof(base), "%s/src/main/java/com/example/app", root);
    ensure_dir(base);

    snprintf(output_path, sizeof(output_path), "%s/DemoController.java", base);

    TemplateData data;
    init_template_data(&data);

    write_template_to_file("backend-generator/templates/DemoController.java.tpl", output_path, &data);

    free_template_data(&data);
    printf("Generated DemoController.java from template.\n");
}