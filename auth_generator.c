#include "auth_generator.h"
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

void generate_auth(const AppConfig *cfg, const char *root)
{
    char base[512], authDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(authDir, sizeof(authDir), "%s/auth", base);
    ensure_dir(authDir);

    /* AuthController.java */
    snprintf(path, sizeof(path), "%s/AuthController.java", authDir);
    FILE *f = fopen(path, "w");
    if (!f)
    {
        perror("AuthController.java");
        return;
    }

    fprintf(f,
            "package com.example.app.auth;\n"
            "\n"
            "import jakarta.validation.Valid;\n"
            "import org.springframework.http.ResponseEntity;\n"
            "import org.springframework.web.bind.annotation.*;\n"
            "\n"
            "@RestController\n"
            "@RequestMapping(\"/api/auth\")\n"
            "public class AuthController {\n"
            "    private final AuthService authService;\n"
            "\n"
            "    public AuthController(AuthService authService) { this.authService = authService; }\n"
            "\n"
            "    @PostMapping(\"/register\")\n"
            "    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {\n"
            "        authService.register(request);\n"
            "        return ResponseEntity.ok(\"Registration successful\");\n"
            "    }\n"
            "\n"
            "    @PostMapping(\"/login\")\n"
            "    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {\n"
            "        return ResponseEntity.ok(authService.login(request));\n"
            "    }\n");

    if (cfg->mail_verification)
    {
        fprintf(f,
                "\n"
                "    @GetMapping(\"/verify\")\n"
                "    public ResponseEntity<String> verify(@RequestParam(\"token\") String token) {\n"
                "        authService.verifyEmail(token);\n"
                "        return ResponseEntity.ok(\"Email verified successfully\");\n"
                "    }\n");
    }

    fprintf(f,
            "}\n");
    fclose(f);

    /* AuthService.java */
    snprintf(path, sizeof(path), "%s/AuthService.java", authDir);
    f = fopen(path, "w");
    if (!f)
    {
        perror("AuthService.java");
        return;
    }

    if (cfg->auth_type == AUTH_JWT)
    {
        if (cfg->mail_verification)
        {
            fprintf(f,
                    "package com.example.app.auth;\n"
                    "\n"
                    "import com.example.app.security.JwtService;\n"
                    "import com.example.app.user.User;\n"
                    "import com.example.app.user.UserRepository;\n"
                    "import jakarta.transaction.Transactional;\n"
                    "import org.springframework.security.authentication.AuthenticationManager;\n"
                    "import org.springframework.security.authentication.BadCredentialsException;\n"
                    "import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;\n"
                    "import org.springframework.security.crypto.password.PasswordEncoder;\n"
                    "import org.springframework.stereotype.Service;\n"
                    "\n"
                    "import java.time.Instant;\n"
                    "import java.time.temporal.ChronoUnit;\n"
                    "import java.util.UUID;\n"
                    "\n"
                    "@Service\n"
                    "public class AuthService {\n"
                    "    private final UserRepository userRepository;\n"
                    "    private final PasswordEncoder passwordEncoder;\n"
                    "    private final AuthenticationManager authenticationManager;\n"
                    "    private final JwtService jwtService;\n"
                    "    private final VerificationTokenRepository tokenRepository;\n"
                    "    private final EmailService emailService;\n"
                    "\n"
                    "    public AuthService(UserRepository userRepository,\n"
                    "                       PasswordEncoder passwordEncoder,\n"
                    "                       AuthenticationManager authenticationManager,\n"
                    "                       JwtService jwtService,\n"
                    "                       VerificationTokenRepository tokenRepository,\n"
                    "                       EmailService emailService) {\n"
                    "        this.userRepository = userRepository;\n"
                    "        this.passwordEncoder = passwordEncoder;\n"
                    "        this.authenticationManager = authenticationManager;\n"
                    "        this.jwtService = jwtService;\n"
                    "        this.tokenRepository = tokenRepository;\n"
                    "        this.emailService = emailService;\n"
                    "    }\n"
                    "\n"
                    "    @Transactional\n"
                    "    public void register(RegisterRequest request) {\n"
                    "        if (userRepository.existsByEmail(request.getEmail()))\n"
                    "            throw new IllegalArgumentException(\"Email already in use\");\n"
                    "        User user = new User();\n"
                    "        user.setEmail(request.getEmail());\n"
                    "        user.setPassword(passwordEncoder.encode(request.getPassword()));\n"
                    "        user.setEnabled(false);\n"
                    "        userRepository.save(user);\n"
                    "\n"
                    "        String tokenValue = UUID.randomUUID().toString();\n"
                    "        VerificationToken vt = new VerificationToken();\n"
                    "        vt.setToken(tokenValue);\n"
                    "        vt.setUser(user);\n"
                    "        vt.setExpiryDate(Instant.now().plus(24, ChronoUnit.HOURS));\n"
                    "        tokenRepository.save(vt);\n"
                    "\n"
                    "        emailService.sendVerificationEmail(user.getEmail(), tokenValue);\n"
                    "    }\n"
                    "\n"
                    "    public AuthResponse login(AuthRequest request) {\n"
                    "        var authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());\n"
                    "        try {\n"
                    "            authenticationManager.authenticate(authToken);\n"
                    "        } catch (BadCredentialsException ex) {\n"
                    "            throw new BadCredentialsException(\"Invalid credentials\");\n"
                    "        }\n"
                    "        User user = userRepository.findByEmail(request.getEmail())\n"
                    "                .orElseThrow(() -> new IllegalArgumentException(\"User not found\"));\n"
                    "        if (!user.isEnabled()) {\n"
                    "            throw new IllegalStateException(\"Email not verified\");\n"
                    "        }\n"
                    "        String token = jwtService.generateToken(user);\n"
                    "        return new AuthResponse(token);\n"
                    "    }\n"
                    "\n"
                    "    @Transactional\n"
                    "    public void verifyEmail(String tokenValue) {\n"
                    "        VerificationToken token = tokenRepository.findByToken(tokenValue)\n"
                    "                .orElseThrow(() -> new IllegalArgumentException(\"Invalid verification token\"));\n"
                    "        if (token.getExpiryDate().isBefore(Instant.now())) {\n"
                    "            throw new IllegalArgumentException(\"Verification token expired\");\n"
                    "        }\n"
                    "        User user = token.getUser();\n"
                    "        user.setEnabled(true);\n"
                    "        userRepository.save(user);\n"
                    "        tokenRepository.delete(token);\n"
                    "    }\n"
                    "}\n");
        }
        else
        {
            fprintf(f,
                    "package com.example.app.auth;\n"
                    "\n"
                    "import com.example.app.security.JwtService;\n"
                    "import com.example.app.user.User;\n"
                    "import com.example.app.user.UserRepository;\n"
                    "import jakarta.transaction.Transactional;\n"
                    "import org.springframework.security.authentication.AuthenticationManager;\n"
                    "import org.springframework.security.authentication.BadCredentialsException;\n"
                    "import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;\n"
                    "import org.springframework.security.crypto.password.PasswordEncoder;\n"
                    "import org.springframework.stereotype.Service;\n"
                    "\n"
                    "@Service\n"
                    "public class AuthService {\n"
                    "    private final UserRepository userRepository;\n"
                    "    private final PasswordEncoder passwordEncoder;\n"
                    "    private final AuthenticationManager authenticationManager;\n"
                    "    private final JwtService jwtService;\n"
                    "\n"
                    "    public AuthService(UserRepository userRepository,\n"
                    "                       PasswordEncoder passwordEncoder,\n"
                    "                       AuthenticationManager authenticationManager,\n"
                    "                       JwtService jwtService) {\n"
                    "        this.userRepository = userRepository;\n"
                    "        this.passwordEncoder = passwordEncoder;\n"
                    "        this.authenticationManager = authenticationManager;\n"
                    "        this.jwtService = jwtService;\n"
                    "    }\n"
                    "\n"
                    "    @Transactional\n"
                    "    public void register(RegisterRequest request) {\n"
                    "        if (userRepository.existsByEmail(request.getEmail()))\n"
                    "            throw new IllegalArgumentException(\"Email already in use\");\n"
                    "        User user = new User();\n"
                    "        user.setEmail(request.getEmail());\n"
                    "        user.setPassword(passwordEncoder.encode(request.getPassword()));\n"
                    "        user.setEnabled(true);\n"
                    "        userRepository.save(user);\n"
                    "    }\n"
                    "\n"
                    "    public AuthResponse login(AuthRequest request) {\n"
                    "        try {\n"
                    "            var authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());\n"
                    "            authenticationManager.authenticate(authToken);\n"
                    "        } catch (BadCredentialsException ex) {\n"
                    "            throw new BadCredentialsException(\"Invalid credentials\");\n"
                    "        }\n"
                    "        User user = userRepository.findByEmail(request.getEmail())\n"
                    "                .orElseThrow(() -> new IllegalArgumentException(\"User not found\"));\n"
                    "        String token = jwtService.generateToken(user);\n"
                    "        return new AuthResponse(token);\n"
                    "    }\n"
                    "}\n");
        }
    }
    else
    { /* BASIC auth */
        if (cfg->mail_verification)
        {
            fprintf(f,
                    "package com.example.app.auth;\n"
                    "\n"
                    "import com.example.app.user.User;\n"
                    "import com.example.app.user.UserRepository;\n"
                    "import jakarta.transaction.Transactional;\n"
                    "import org.springframework.security.authentication.AuthenticationManager;\n"
                    "import org.springframework.security.authentication.BadCredentialsException;\n"
                    "import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;\n"
                    "import org.springframework.security.crypto.password.PasswordEncoder;\n"
                    "import org.springframework.stereotype.Service;\n"
                    "\n"
                    "import java.time.Instant;\n"
                    "import java.time.temporal.ChronoUnit;\n"
                    "import java.util.UUID;\n"
                    "\n"
                    "@Service\n"
                    "public class AuthService {\n"
                    "    private final UserRepository userRepository;\n"
                    "    private final PasswordEncoder passwordEncoder;\n"
                    "    private final AuthenticationManager authenticationManager;\n"
                    "    private final VerificationTokenRepository tokenRepository;\n"
                    "    private final EmailService emailService;\n"
                    "\n"
                    "    public AuthService(UserRepository userRepository,\n"
                    "                       PasswordEncoder passwordEncoder,\n"
                    "                       AuthenticationManager authenticationManager,\n"
                    "                       VerificationTokenRepository tokenRepository,\n"
                    "                       EmailService emailService) {\n"
                    "        this.userRepository = userRepository;\n"
                    "        this.passwordEncoder = passwordEncoder;\n"
                    "        this.authenticationManager = authenticationManager;\n"
                    "        this.tokenRepository = tokenRepository;\n"
                    "        this.emailService = emailService;\n"
                    "    }\n"
                    "\n"
                    "    @Transactional\n"
                    "    public void register(RegisterRequest request) {\n"
                    "        if (userRepository.existsByEmail(request.getEmail()))\n"
                    "            throw new IllegalArgumentException(\"Email already in use\");\n"
                    "        User user = new User();\n"
                    "        user.setEmail(request.getEmail());\n"
                    "        user.setPassword(passwordEncoder.encode(request.getPassword()));\n"
                    "        user.setEnabled(false);\n"
                    "        userRepository.save(user);\n"
                    "\n"
                    "        String tokenValue = UUID.randomUUID().toString();\n"
                    "        VerificationToken vt = new VerificationToken();\n"
                    "        vt.setToken(tokenValue);\n"
                    "        vt.setUser(user);\n"
                    "        vt.setExpiryDate(Instant.now().plus(24, ChronoUnit.HOURS));\n"
                    "        tokenRepository.save(vt);\n"
                    "\n"
                    "        emailService.sendVerificationEmail(user.getEmail(), tokenValue);\n"
                    "    }\n"
                    "\n"
                    "    public AuthResponse login(AuthRequest request) {\n"
                    "        try {\n"
                    "            var authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());\n"
                    "            authenticationManager.authenticate(authToken);\n"
                    "        } catch (BadCredentialsException ex) {\n"
                    "            throw new BadCredentialsException(\"Invalid credentials\");\n"
                    "        }\n"
                    "        User user = userRepository.findByEmail(request.getEmail())\n"
                    "                .orElseThrow(() -> new IllegalArgumentException(\"User not found\"));\n"
                    "        if (!user.isEnabled()) {\n"
                    "            throw new IllegalStateException(\"Email not verified\");\n"
                    "        }\n"
                    "        return new AuthResponse(\"basic-auth-active\");\n"
                    "    }\n"
                    "\n"
                    "    @Transactional\n"
                    "    public void verifyEmail(String tokenValue) {\n"
                    "        VerificationToken token = tokenRepository.findByToken(tokenValue)\n"
                    "                .orElseThrow(() -> new IllegalArgumentException(\"Invalid verification token\"));\n"
                    "        if (token.getExpiryDate().isBefore(Instant.now())) {\n"
                    "            throw new IllegalArgumentException(\"Verification token expired\");\n"
                    "        }\n"
                    "        User user = token.getUser();\n"
                    "        user.setEnabled(true);\n"
                    "        userRepository.save(user);\n"
                    "        tokenRepository.delete(token);\n"
                    "    }\n"
                    "}\n");
        }
        else
        {
            fprintf(f,
                    "package com.example.app.auth;\n"
                    "\n"
                    "import com.example.app.user.User;\n"
                    "import com.example.app.user.UserRepository;\n"
                    "import jakarta.transaction.Transactional;\n"
                    "import org.springframework.security.authentication.AuthenticationManager;\n"
                    "import org.springframework.security.authentication.BadCredentialsException;\n"
                    "import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;\n"
                    "import org.springframework.security.crypto.password.PasswordEncoder;\n"
                    "import org.springframework.stereotype.Service;\n"
                    "\n"
                    "@Service\n"
                    "public class AuthService {\n"
                    "    private final UserRepository userRepository;\n"
                    "    private final PasswordEncoder passwordEncoder;\n"
                    "    private final AuthenticationManager authenticationManager;\n"
                    "\n"
                    "    public AuthService(UserRepository userRepository,\n"
                    "                       PasswordEncoder passwordEncoder,\n"
                    "                       AuthenticationManager authenticationManager) {\n"
                    "        this.userRepository = userRepository;\n"
                    "        this.passwordEncoder = passwordEncoder;\n"
                    "        this.authenticationManager = authenticationManager;\n"
                    "    }\n"
                    "\n"
                    "    @Transactional\n"
                    "    public void register(RegisterRequest request) {\n"
                    "        if (userRepository.existsByEmail(request.getEmail()))\n"
                    "            throw new IllegalArgumentException(\"Email already in use\");\n"
                    "        User user = new User();\n"
                    "        user.setEmail(request.getEmail());\n"
                    "        user.setPassword(passwordEncoder.encode(request.getPassword()));\n"
                    "        user.setEnabled(true);\n"
                    "        userRepository.save(user);\n"
                    "    }\n"
                    "\n"
                    "    public AuthResponse login(AuthRequest request) {\n"
                    "        try {\n"
                    "            var authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());\n"
                    "            authenticationManager.authenticate(authToken);\n"
                    "        } catch (BadCredentialsException ex) {\n"
                    "            throw new BadCredentialsException(\"Invalid credentials\");\n"
                    "        }\n"
                    "        return new AuthResponse(\"basic-auth-active\");\n"
                    "    }\n"
                    "}\n");
        }
    }
    fclose(f);

    /* DTOs */
    snprintf(path, sizeof(path), "%s/AuthRequest.java", authDir);
    f = fopen(path, "w");
    fprintf(f,
            "package com.example.app.auth;\n"
            "\n"
            "import jakarta.validation.constraints.Email;\n"
            "import jakarta.validation.constraints.NotBlank;\n"
            "import jakarta.validation.constraints.Size;\n"
            "\n"
            "public class AuthRequest {\n"
            "    @Email @NotBlank\n"
            "    private String email;\n"
            "    @NotBlank @Size(min = 6)\n"
            "    private String password;\n"
            "    public String getEmail() { return email; }\n"
            "    public void setEmail(String email) { this.email = email; }\n"
            "    public String getPassword() { return password; }\n"
            "    public void setPassword(String password) { this.password = password; }\n"
            "}\n");
    fclose(f);

    snprintf(path, sizeof(path), "%s/RegisterRequest.java", authDir);
    f = fopen(path, "w");
    fprintf(f,
            "package com.example.app.auth;\n"
            "\n"
            "import jakarta.validation.constraints.Email;\n"
            "import jakarta.validation.constraints.NotBlank;\n"
            "import jakarta.validation.constraints.Size;\n"
            "\n"
            "public class RegisterRequest {\n"
            "    @Email @NotBlank\n"
            "    private String email;\n"
            "    @NotBlank @Size(min = 6)\n"
            "    private String password;\n"
            "    public String getEmail() { return email; }\n"
            "    public void setEmail(String email) { this.email = email; }\n"
            "    public String getPassword() { return password; }\n"
            "    public void setPassword(String password) { this.password = password; }\n"
            "}\n");
    fclose(f);

    snprintf(path, sizeof(path), "%s/AuthResponse.java", authDir);
    f = fopen(path, "w");
    fprintf(f,
            "package com.example.app.auth;\n"
            "\n"
            "public class AuthResponse {\n"
            "    private String accessToken;\n"
            "    public AuthResponse() {}\n"
            "    public AuthResponse(String accessToken) { this.accessToken = accessToken; }\n"
            "    public String getAccessToken() { return accessToken; }\n"
            "    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }\n"
            "}\n");
    fclose(f);

    /* If mail verification enabled: VerificationToken + repo + EmailService */
    if (cfg->mail_verification)
    {
        /* VerificationToken.java */
        snprintf(path, sizeof(path), "%s/VerificationToken.java", authDir);
        f = fopen(path, "w");
        if (!f)
        {
            perror("VerificationToken.java");
            return;
        }
        fprintf(f,
                "package com.example.app.auth;\n"
                "\n"
                "import com.example.app.user.User;\n"
                "import jakarta.persistence.*;\n"
                "\n"
                "import java.time.Instant;\n"
                "\n"
                "@Entity\n"
                "public class VerificationToken {\n"
                "    @Id\n"
                "    @GeneratedValue(strategy = GenerationType.IDENTITY)\n"
                "    private Long id;\n"
                "\n"
                "    @Column(nullable = false, unique = true)\n"
                "    private String token;\n"
                "\n"
                "    @ManyToOne(optional = false)\n"
                "    private User user;\n"
                "\n"
                "    @Column(nullable = false)\n"
                "    private Instant expiryDate;\n"
                "\n"
                "    public Long getId() { return id; }\n"
                "    public String getToken() { return token; }\n"
                "    public void setToken(String token) { this.token = token; }\n"
                "    public User getUser() { return user; }\n"
                "    public void setUser(User user) { this.user = user; }\n"
                "    public Instant getExpiryDate() { return expiryDate; }\n"
                "    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }\n"
                "}\n");
        fclose(f);

        /* VerificationTokenRepository.java */
        snprintf(path, sizeof(path), "%s/VerificationTokenRepository.java", authDir);
        f = fopen(path, "w");
        if (!f)
        {
            perror("VerificationTokenRepository.java");
            return;
        }
        fprintf(f,
                "package com.example.app.auth;\n"
                "\n"
                "import org.springframework.data.jpa.repository.JpaRepository;\n"
                "import java.util.Optional;\n"
                "\n"
                "public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {\n"
                "    Optional<VerificationToken> findByToken(String token);\n"
                "}\n");
        fclose(f);

        /* EmailService.java */
        snprintf(path, sizeof(path), "%s/EmailService.java", authDir);
        f = fopen(path, "w");
        if (!f)
        {
            perror("EmailService.java");
            return;
        }
        fprintf(f,
                "package com.example.app.auth;\n"
                "\n"
                "import org.springframework.beans.factory.annotation.Value;\n"
                "import org.springframework.mail.SimpleMailMessage;\n"
                "import org.springframework.mail.javamail.JavaMailSender;\n"
                "import org.springframework.stereotype.Service;\n"
                "\n"
                "@Service\n"
                "public class EmailService {\n"
                "    private final JavaMailSender mailSender;\n"
                "    private final String from;\n"
                "    private final String verificationBaseUrl;\n"
                "\n"
                "    public EmailService(JavaMailSender mailSender,\n"
                "                        @Value(\"${app.mail.from}\") String from,\n"
                "                        @Value(\"${app.mail.verification-base-url}\") String verificationBaseUrl) {\n"
                "        this.mailSender = mailSender;\n"
                "        this.from = from;\n"
                "        this.verificationBaseUrl = verificationBaseUrl;\n"
                "    }\n"
                "\n"
                "    public void sendVerificationEmail(String toEmail, String token) {\n"
                "        String verifyLink = verificationBaseUrl + token;\n"
                "        SimpleMailMessage message = new SimpleMailMessage();\n"
                "        message.setFrom(from);\n"
                "        message.setTo(toEmail);\n"
                "        message.setSubject(\"Verify your email\");\n"
                "        message.setText(\"Click the link to verify your email: \" + verifyLink);\n"
                "        mailSender.send(message);\n"
                "    }\n"
                "}\n");
        fclose(f);
    }
}
