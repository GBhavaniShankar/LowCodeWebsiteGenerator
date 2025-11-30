#include "auth_generator.h"
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

void generate_auth(const AppConfig *cfg, const char *root)
{
    char base[512], authDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(authDir, sizeof(authDir), "%s/auth", base);
    ensure_dir(authDir);

    // 1. Generate DTOs (AuthRequest, RegisterRequest, AuthResponse)
    TemplateData emptyData;
    init_template_data(&emptyData);

    snprintf(path, sizeof(path), "%s/AuthRequest.java", authDir);
    write_template_to_file("backend-generator/templates/AuthRequest.java.tpl", path, &emptyData);

    snprintf(path, sizeof(path), "%s/RegisterRequest.java", authDir);
    write_template_to_file("backend-generator/templates/RegisterRequest.java.tpl", path, &emptyData);

    snprintf(path, sizeof(path), "%s/AuthResponse.java", authDir);
    write_template_to_file("backend-generator/templates/AuthResponse.java.tpl", path, &emptyData);
    
    free_template_data(&emptyData);


    // 2. Generate AuthController
    TemplateData ctrlData;
    init_template_data(&ctrlData);
    
    if (cfg->mail_verification) {
        add_replacement(&ctrlData, "{{VerifyEndpoint}}",
            "    @GetMapping(\"/verify\")\n"
            "    public ResponseEntity<String> verify(@RequestParam(\"token\") String token) {\n"
            "        authService.verifyEmail(token);\n"
            "        return ResponseEntity.ok(\"Email verified successfully. You can now log in.\");\n"
            "    }");
    } else {
        add_replacement(&ctrlData, "{{VerifyEndpoint}}", "");
    }
    
    snprintf(path, sizeof(path), "%s/AuthController.java", authDir);
    write_template_to_file("backend-generator/templates/AuthController.java.tpl", path, &ctrlData);
    free_template_data(&ctrlData);


    // 3. Generate AuthService
    TemplateData svcData;
    init_template_data(&svcData);

    // --- Build Dynamic Service Logic ---
    
    // A. Fields & Imports
    char imports[1024] = "";
    char fields[1024] = "";
    char ctorArgs[1024] = "";
    char ctorAssign[1024] = "";

    if (cfg->auth_type == AUTH_JWT) {
        strcat(imports, "import com.example.app.security.JwtService;\n");
        strcat(fields,  "    private final JwtService jwtService;\n");
        strcat(ctorArgs, ",\n                       JwtService jwtService");
        strcat(ctorAssign, "        this.jwtService = jwtService;\n");
    }

    if (cfg->mail_verification) {
        strcat(imports, "import com.example.app.mail.EmailService;\n");
        strcat(fields,  "    private final VerificationTokenRepository verificationTokenRepository;\n"
                        "    private final EmailService emailService;\n");
        strcat(ctorArgs, ",\n                       VerificationTokenRepository verificationTokenRepository,\n"
                         "                       EmailService emailService");
        strcat(ctorAssign, "        this.verificationTokenRepository = verificationTokenRepository;\n"
                           "        this.emailService = emailService;\n");
    }

    add_replacement(&svcData, "{{ExtraImports}}", imports);
    add_replacement(&svcData, "{{ExtraFields}}", fields);
    add_replacement(&svcData, "{{ConstructorArgs}}", ctorArgs);
    add_replacement(&svcData, "{{ConstructorAssignment}}", ctorAssign);

    // B. Register Logic
    if (cfg->mail_verification) {
        add_replacement(&svcData, "{{RegisterLogic}}", "        user.setEnabled(false); // wait for verification");
        add_replacement(&svcData, "{{RegisterPostSave}}",
            "        VerificationToken token = new VerificationToken(user);\n"
            "        verificationTokenRepository.save(token);\n"
            "        emailService.sendVerificationEmail(user.getEmail(), token.getToken());");
    } else {
        add_replacement(&svcData, "{{RegisterLogic}}", "        user.setEnabled(true);");
        add_replacement(&svcData, "{{RegisterPostSave}}", "");
    }

    // C. Login Logic
    char loginLogic[1024] = "";
    if (cfg->mail_verification) {
        strcat(loginLogic,
            "        if (!user.isEnabled()) {\n"
            "            throw new IllegalStateException(\"Email not verified\");\n"
            "        }\n");
    }

    if (cfg->auth_type == AUTH_JWT) {
        strcat(loginLogic,
            "        String token = jwtService.generateToken(user);\n"
            "        return new AuthResponse(token);");
    } else {
        strcat(loginLogic, "        return new AuthResponse(\"basic-auth-active\");");
    }
    add_replacement(&svcData, "{{LoginLogic}}", loginLogic);

    // D. Verify Method
    if (cfg->mail_verification) {
        add_replacement(&svcData, "{{VerifyMethod}}",
            "    @Transactional\n"
            "    public void verifyEmail(String tokenValue) {\n"
            "        VerificationToken token = verificationTokenRepository.findByToken(tokenValue)\n"
            "                .orElseThrow(() -> new IllegalArgumentException(\"Invalid verification token\"));\n"
            "\n"
            "        if (token.isExpired()) {\n"
            "            throw new IllegalStateException(\"Verification token expired\");\n"
            "        }\n"
            "        User user = token.getUser();\n"
            "        user.setEnabled(true);\n"
            "        userRepository.save(user);\n"
            "        verificationTokenRepository.delete(token);\n"
            "    }");
    } else {
        add_replacement(&svcData, "{{VerifyMethod}}", "");
    }

    snprintf(path, sizeof(path), "%s/AuthService.java", authDir);
    write_template_to_file("backend-generator/templates/AuthService.java.tpl", path, &svcData);
    free_template_data(&svcData);

    printf("Generated Auth Module from templates.\n");
}