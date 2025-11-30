#include "java_core_generator.h"
#include "file_util.h"
#include <stdio.h>

static void make_java_base(char *base, size_t n, const char *root)
{
    char dir[512];

    ensure_dir(root);

    snprintf(dir, sizeof(dir), "%s/src", root);
    ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main", root);
    ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main/java", root);
    ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main/java/com", root);
    ensure_dir(dir);
    snprintf(dir, sizeof(dir), "%s/src/main/java/com/example", root);
    ensure_dir(dir);

    snprintf(base, n, "%s/src/main/java/com/example/app", root);
    ensure_dir(base);
}

void generate_core_app(const AppConfig *cfg, const char *root)
{
    (void)cfg;

    char base[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(path, sizeof(path), "%s/Application.java", base);
    FILE *f = fopen(path, "w");
    if (!f)
    {
        perror("Application.java");
        return;
    }

    fprintf(f,
            "package com.example.app;\n"
            "\n"
            "import org.springframework.boot.SpringApplication;\n"
            "import org.springframework.boot.autoconfigure.SpringBootApplication;\n"
            "\n"
            "@SpringBootApplication\n"
            "public class Application {\n"
            "    public static void main(String[] args) {\n"
            "        SpringApplication.run(Application.class, args);\n"
            "    }\n"
            "}\n");

    fclose(f);
}
