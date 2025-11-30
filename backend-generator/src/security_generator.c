#include "security_generator.h"
#include "file_util.h"
#include <stdio.h>
#include <string.h>

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

void generate_security(const AppConfig *cfg, const char *root)
{
    char base[512], secDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(secDir, sizeof(secDir), "%s/security", base);
    ensure_dir(secDir);

    /* ---------------------------------------------------------
       1. Generate SecurityConfig.java
       --------------------------------------------------------- */
    snprintf(path, sizeof(path), "%s/SecurityConfig.java", secDir);

    TemplateData data;
    init_template_data(&data);

    if (cfg->auth_type == AUTH_JWT)
    {
        // --- JWT Configuration ---
        add_replacement(&data, "{{ExtraImports}}",
            "import org.springframework.security.authentication.AuthenticationManager;\n"
            "import org.springframework.security.authentication.AuthenticationProvider;\n"
            "import org.springframework.security.authentication.dao.DaoAuthenticationProvider;\n"
            "import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;\n"
            "import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;");

        add_replacement(&data, "{{FieldsAndConstructor}}",
            "    private final JwtAuthFilter jwtAuthFilter;\n"
            "    private final UserRepository userRepository;\n"
            "\n"
            "    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserRepository userRepository) {\n"
            "        this.jwtAuthFilter = jwtAuthFilter;\n"
            "        this.userRepository = userRepository;\n"
            "    }");

        add_replacement(&data, "{{ExtraBeans}}",
            "    @Bean\n"
            "    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,\n"
            "                                                         PasswordEncoder passwordEncoder) {\n"
            "        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();\n"
            "        provider.setUserDetailsService(userDetailsService);\n"
            "        provider.setPasswordEncoder(passwordEncoder);\n"
            "        return provider;\n"
            "    }\n"
            "\n"
            "    @Bean\n"
            "    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {\n"
            "        return configuration.getAuthenticationManager();\n"
            "    }");

        add_replacement(&data, "{{FilterChainArgs}}", ",\n                                                   AuthenticationProvider authenticationProvider");
        
        add_replacement(&data, "{{FilterChainConfig}}",
            "            .authenticationProvider(authenticationProvider)\n"
            "            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)");
    }
    else
    {
        // --- Basic Auth Configuration ---
        add_replacement(&data, "{{ExtraImports}}", ""); // No extra imports needed

        add_replacement(&data, "{{FieldsAndConstructor}}",
            "    private final UserRepository userRepository;\n"
            "\n"
            "    public SecurityConfig(UserRepository userRepository) { this.userRepository = userRepository; }");

        add_replacement(&data, "{{ExtraBeans}}", ""); // No extra beans needed

        add_replacement(&data, "{{FilterChainArgs}}", "");

        add_replacement(&data, "{{FilterChainConfig}}",
            "            .httpBasic(httpBasic -> {})");
    }

    write_template_to_file("backend-generator/templates/SecurityConfig.java.tpl", path, &data);
    free_template_data(&data);

    /* ---------------------------------------------------------
       2. Generate JWT Support Files (Conditional)
       --------------------------------------------------------- */
    if (cfg->auth_type == AUTH_JWT)
    {
        TemplateData jwtData;
        init_template_data(&jwtData); // Empty context, purely static templates

        // A. Generate JwtService.java
        snprintf(path, sizeof(path), "%s/JwtService.java", secDir);
        write_template_to_file("backend-generator/templates/JwtService.java.tpl", path, &jwtData);
        printf("Generated JwtService.java from template.\n");

        // B. Generate JwtAuthFilter.java
        snprintf(path, sizeof(path), "%s/JwtAuthFilter.java", secDir);
        write_template_to_file("backend-generator/templates/JwtAuthFilter.java.tpl", path, &jwtData);
        printf("Generated JwtAuthFilter.java from template.\n");

        free_template_data(&jwtData);
    }
    
    printf("Generated SecurityConfig.java from template.\n");
}