#include "demo_generator.h"
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

void generate_demo(const AppConfig *cfg, const char *root)
{
    (void)cfg;

    char base[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(path, sizeof(path), "%s/DemoController.java", base);
    FILE *f = fopen(path, "w");
    if (!f)
    {
        perror("DemoController.java");
        return;
    }

    fprintf(f,
            "package com.example.app;\n"
            "\n"
            "import org.springframework.web.bind.annotation.GetMapping;\n"
            "import org.springframework.web.bind.annotation.RestController;\n"
            "\n"
            "@RestController\n"
            "public class DemoController {\n"
            "    @GetMapping(\"/api/demo/hello\")\n"
            "    public String hello() {\n"
            "        return \"Hello from generated backend!\";\n"
            "    }\n"
            "}\n");

    fclose(f);
}
