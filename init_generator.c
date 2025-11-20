#include "init_generator.h"
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

void generate_init(const AppConfig *cfg, const char *root)
{
    char base[512], cfgDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(cfgDir, sizeof(cfgDir), "%s/config", base);
    ensure_dir(cfgDir);

    snprintf(path, sizeof(path), "%s/DataInitializer.java", cfgDir);
    FILE *f = fopen(path, "w");
    if (!f)
    {
        perror("DataInitializer.java");
        return;
    }

    fprintf(f,
            "package com.example.app.config;\n"
            "\n"
            "import com.example.app.user.Role;\n"
            "import com.example.app.user.User;\n"
            "import com.example.app.user.UserRepository;\n"
            "import org.springframework.beans.factory.annotation.Value;\n"
            "import org.springframework.boot.CommandLineRunner;\n"
            "import org.springframework.security.crypto.password.PasswordEncoder;\n"
            "import org.springframework.stereotype.Component;\n"
            "\n"
            "@Component\n"
            "public class DataInitializer implements CommandLineRunner {\n"
            "\n"
            "    private final UserRepository userRepository;\n"
            "    private final PasswordEncoder passwordEncoder;\n"
            "    private final String adminEmail;\n"
            "    private final String adminPassword;\n"
            "\n"
            "    public DataInitializer(UserRepository userRepository,\n"
            "                           PasswordEncoder passwordEncoder,\n"
            "                           @Value(\"${app.admin.email:admin@example.com}\") String adminEmail,\n"
            "                           @Value(\"${app.admin.password:admin123}\") String adminPassword) {\n"
            "        this.userRepository = userRepository;\n"
            "        this.passwordEncoder = passwordEncoder;\n"
            "        this.adminEmail = adminEmail;\n"
            "        this.adminPassword = adminPassword;\n"
            "    }\n"
            "\n"
            "    @Override\n"
            "    public void run(String... args) {\n"
            "        if (userRepository.existsByEmail(adminEmail)) {\n"
            "            return;\n"
            "        }\n"
            "        User admin = new User();\n"
            "        admin.setEmail(adminEmail);\n"
            "        admin.setPassword(passwordEncoder.encode(adminPassword));\n"
            "        admin.setRole(Role.ADMIN);\n"
            "        admin.setEnabled(true);\n"
            "        userRepository.save(admin);\n"
            "    }\n"
            "}\n");

    fclose(f);
}
