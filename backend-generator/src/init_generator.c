#include "init_generator.h"
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

void generate_init(const AppConfig *cfg, const char *root)
{
    (void)cfg; // Config not needed, relies on application.yml

    char base[512], cfgDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(cfgDir, sizeof(cfgDir), "%s/config", base);
    ensure_dir(cfgDir);

    snprintf(path, sizeof(path), "%s/DataInitializer.java", cfgDir);

    TemplateData data;
    init_template_data(&data);
    
    write_template_to_file("backend-generator/templates/DataInitializer.java.tpl", path, &data);
    
    free_template_data(&data);
    printf("Generated DataInitializer from template.\n");
}