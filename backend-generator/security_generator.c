#include "security_generator.h"
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

void generate_security(const AppConfig *cfg, const char *root)
{
    char base[512], secDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(secDir, sizeof(secDir), "%s/security", base);
    ensure_dir(secDir);

    /* SecurityConfig.java */
    snprintf(path, sizeof(path), "%s/SecurityConfig.java", secDir);
    FILE *f = fopen(path, "w");
    if (!f)
    {
        perror("SecurityConfig.java");
        return;
    }

    if (cfg->auth_type == AUTH_JWT)
    {
        fprintf(f,
                "package com.example.app.security;\n"
                "\n"
                "import com.example.app.user.UserRepository;\n"
                "import org.springframework.context.annotation.Bean;\n"
                "import org.springframework.context.annotation.Configuration;\n"
                "import org.springframework.security.authentication.AuthenticationManager;\n"
                "import org.springframework.security.authentication.AuthenticationProvider;\n"
                "import org.springframework.security.authentication.dao.DaoAuthenticationProvider;\n"
                "import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;\n"
                "import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;\n"
                "import org.springframework.security.config.annotation.web.builders.HttpSecurity;\n"
                "import org.springframework.security.config.http.SessionCreationPolicy;\n"
                "import org.springframework.security.core.userdetails.UserDetailsService;\n"
                "import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;\n"
                "import org.springframework.security.crypto.password.PasswordEncoder;\n"
                "import org.springframework.security.web.SecurityFilterChain;\n"
                "import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;\n"
                "\n"
                "@Configuration\n"
                "@EnableMethodSecurity\n"
                "public class SecurityConfig {\n"
                "    private final JwtAuthFilter jwtAuthFilter;\n"
                "    private final UserRepository userRepository;\n"
                "\n"
                "    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserRepository userRepository) {\n"
                "        this.jwtAuthFilter = jwtAuthFilter;\n"
                "        this.userRepository = userRepository;\n"
                "    }\n"
                "\n"
                "    @Bean\n"
                "    public UserDetailsService userDetailsService() {\n"
                "        return username -> userRepository.findByEmail(username)\n"
                "                .orElseThrow(() -> new RuntimeException(\"User not found\"));\n"
                "    }\n"
                "\n"
                "    @Bean\n"
                "    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }\n"
                "\n"
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
                "    }\n"
                "\n"
                "    @Bean\n"
                "    public SecurityFilterChain securityFilterChain(HttpSecurity http,\n"
                "                                                   AuthenticationProvider authenticationProvider) throws Exception {\n"
                "        http\n"
                "            .csrf(csrf -> csrf.disable())\n"
                "            .authorizeHttpRequests(auth -> auth\n"
                "                .requestMatchers(\"/api/auth/**\", \"/h2-console/**\", \"/v3/api-docs/**\", \"/swagger-ui.html\", \"/swagger-ui/**\").permitAll()\n"
                "                .anyRequest().authenticated())\n"
                "            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))\n"
                "            .authenticationProvider(authenticationProvider)\n"
                "            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);\n"
                "\n"
                "        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));\n"
                "        return http.build();\n"
                "    }\n"
                "}\n");
    }
    else
    {
        fprintf(f,
                "package com.example.app.security;\n"
                "\n"
                "import com.example.app.user.UserRepository;\n"
                "import org.springframework.context.annotation.Bean;\n"
                "import org.springframework.context.annotation.Configuration;\n"
                "import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;\n"
                "import org.springframework.security.config.annotation.web.builders.HttpSecurity;\n"
                "import org.springframework.security.config.http.SessionCreationPolicy;\n"
                "import org.springframework.security.core.userdetails.UserDetailsService;\n"
                "import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;\n"
                "import org.springframework.security.crypto.password.PasswordEncoder;\n"
                "import org.springframework.security.web.SecurityFilterChain;\n"
                "\n"
                "@Configuration\n"
                "@EnableMethodSecurity\n"
                "public class SecurityConfig {\n"
                "    private final UserRepository userRepository;\n"
                "\n"
                "    public SecurityConfig(UserRepository userRepository) { this.userRepository = userRepository; }\n"
                "\n"
                "    @Bean\n"
                "    public UserDetailsService userDetailsService() {\n"
                "        return username -> userRepository.findByEmail(username)\n"
                "                .orElseThrow(() -> new RuntimeException(\"User not found\"));\n"
                "    }\n"
                "\n"
                "    @Bean\n"
                "    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }\n"
                "\n"
                "    @Bean\n"
                "    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {\n"
                "        http\n"
                "            .csrf(csrf -> csrf.disable())\n"
                "            .authorizeHttpRequests(auth -> auth\n"
                "                .requestMatchers(\"/api/auth/**\", \"/h2-console/**\", \"/v3/api-docs/**\", \"/swagger-ui.html\", \"/swagger-ui/**\").permitAll()\n"
                "                .anyRequest().authenticated())\n"
                "            .httpBasic(httpBasic -> {})\n"
                "            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));\n"
                "\n"
                "        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));\n"
                "        return http.build();\n"
                "    }\n"
                "}\n");
    }
    fclose(f);

    if (cfg->auth_type == AUTH_JWT)
    {
        /* JwtService.java */
        snprintf(path, sizeof(path), "%s/JwtService.java", secDir);
        f = fopen(path, "w");
        if (!f)
        {
            perror("JwtService.java");
            return;
        }
        fprintf(f,
                "package com.example.app.security;\n"
                "\n"
                "import io.jsonwebtoken.*;\n"
                "import io.jsonwebtoken.security.Keys;\n"
                "import org.springframework.beans.factory.annotation.Value;\n"
                "import org.springframework.security.core.userdetails.UserDetails;\n"
                "import org.springframework.stereotype.Service;\n"
                "\n"
                "import java.security.Key;\n"
                "import java.util.Date;\n"
                "\n"
                "@Service\n"
                "public class JwtService {\n"
                "    private final Key key;\n"
                "    private final long jwtExpirationMs;\n"
                "\n"
                "    public JwtService(@Value(\"${app.jwt.secret}\") String secret,\n"
                "                      @Value(\"${app.jwt.expiration-ms}\") long jwtExpirationMs) {\n"
                "        this.key = Keys.hmacShaKeyFor(secret.getBytes());\n"
                "        this.jwtExpirationMs = jwtExpirationMs;\n"
                "    }\n"
                "\n"
                "    public String generateToken(UserDetails userDetails) {\n"
                "        Date now = new Date();\n"
                "        Date expiry = new Date(now.getTime() + jwtExpirationMs);\n"
                "        return Jwts.builder()\n"
                "                .setSubject(userDetails.getUsername())\n"
                "                .setIssuedAt(now)\n"
                "                .setExpiration(expiry)\n"
                "                .signWith(key, SignatureAlgorithm.HS256)\n"
                "                .compact();\n"
                "    }\n"
                "\n"
                "    public String extractUsername(String token) {\n"
                "        return parseClaims(token).getBody().getSubject();\n"
                "    }\n"
                "\n"
                "    public boolean isTokenValid(String token, UserDetails userDetails) {\n"
                "        String username = extractUsername(token);\n"
                "        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);\n"
                "    }\n"
                "\n"
                "    private boolean isTokenExpired(String token) {\n"
                "        Date expiration = parseClaims(token).getBody().getExpiration();\n"
                "        return expiration.before(new Date());\n"
                "    }\n"
                "\n"
                "    private Jws<Claims> parseClaims(String token) {\n"
                "        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);\n"
                "    }\n"
                "}\n");
        fclose(f);

        /* JwtAuthFilter.java */
        snprintf(path, sizeof(path), "%s/JwtAuthFilter.java", secDir);
        f = fopen(path, "w");
        if (!f)
        {
            perror("JwtAuthFilter.java");
            return;
        }
        fprintf(f,
                "package com.example.app.security;\n"
                "\n"
                "import com.example.app.user.UserRepository;\n"
                "import jakarta.servlet.FilterChain;\n"
                "import jakarta.servlet.ServletException;\n"
                "import jakarta.servlet.http.HttpServletRequest;\n"
                "import jakarta.servlet.http.HttpServletResponse;\n"
                "import org.springframework.http.HttpHeaders;\n"
                "import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;\n"
                "import org.springframework.security.core.context.SecurityContextHolder;\n"
                "import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;\n"
                "import org.springframework.stereotype.Component;\n"
                "import org.springframework.web.filter.OncePerRequestFilter;\n"
                "\n"
                "import java.io.IOException;\n"
                "\n"
                "@Component\n"
                "public class JwtAuthFilter extends OncePerRequestFilter {\n"
                "    private final JwtService jwtService;\n"
                "    private final UserRepository userRepository;\n"
                "\n"
                "    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {\n"
                "        this.jwtService = jwtService;\n"
                "        this.userRepository = userRepository;\n"
                "    }\n"
                "\n"
                "    @Override\n"
                "    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,\n"
                "                                    FilterChain filterChain) throws ServletException, IOException {\n"
                "        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);\n"
                "        if (authHeader == null || !authHeader.startsWith(\"Bearer \")) {\n"
                "            filterChain.doFilter(request, response);\n"
                "            return;\n"
                "        }\n"
                "        String jwt = authHeader.substring(7);\n"
                "        String userEmail;\n"
                "        try { userEmail = jwtService.extractUsername(jwt); }\n"
                "        catch (Exception e) { filterChain.doFilter(request, response); return; }\n"
                "\n"
                "        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {\n"
                "            var userOpt = userRepository.findByEmail(userEmail);\n"
                "            if (userOpt.isPresent() && jwtService.isTokenValid(jwt, userOpt.get())) {\n"
                "                var user = userOpt.get();\n"
                "                var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());\n"
                "                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));\n"
                "                SecurityContextHolder.getContext().setAuthentication(authToken);\n"
                "            }\n"
                "        }\n"
                "        filterChain.doFilter(request, response);\n"
                "    }\n"
                "}\n");
        fclose(f);
    }
}
