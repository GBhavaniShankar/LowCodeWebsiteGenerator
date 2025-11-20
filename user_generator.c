#include "user_generator.h"
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

void generate_user(const AppConfig *cfg, const char *root)
{
    (void)cfg;

    char base[512], userDir[512], path[512];
    make_java_base(base, sizeof(base), root);

    snprintf(userDir, sizeof(userDir), "%s/user", base);
    ensure_dir(userDir);

    /* User.java */
    snprintf(path, sizeof(path), "%s/User.java", userDir);
    FILE *f = fopen(path, "w");
    if (!f)
    {
        perror("User.java");
        return;
    }

    fprintf(f,
            "package com.example.app.user;\n"
            "\n"
            "import jakarta.persistence.*;\n"
            "import org.springframework.security.core.GrantedAuthority;\n"
            "import org.springframework.security.core.authority.SimpleGrantedAuthority;\n"
            "import org.springframework.security.core.userdetails.UserDetails;\n"
            "\n"
            "import java.util.Collection;\n"
            "import java.util.List;\n"
            "\n"
            "@Entity\n"
            "@Table(name = \"users\")\n"
            "public class User implements UserDetails {\n"
            "    @Id\n"
            "    @GeneratedValue(strategy = GenerationType.IDENTITY)\n"
            "    private Long id;\n"
            "\n"
            "    @Column(nullable = false, unique = true)\n"
            "    private String email;\n"
            "\n"
            "    @Column(nullable = false)\n"
            "    private String password;\n"
            "\n"
            "    @Column(nullable = false)\n"
            "    private boolean enabled = false;\n"
            "\n"
            "    public Long getId() { return id; }\n"
            "    public String getEmail() { return email; }\n"
            "    public void setEmail(String email) { this.email = email; }\n"
            "    @Override public String getUsername() { return email; }\n"
            "    public void setPassword(String password) { this.password = password; }\n"
            "    @Override public String getPassword() { return password; }\n"
            "    public void setEnabled(boolean enabled) { this.enabled = enabled; }\n"
            "\n"
            "    @Override public Collection<? extends GrantedAuthority> getAuthorities() {\n"
            "        return List.of(new SimpleGrantedAuthority(\"ROLE_USER\"));\n"
            "    }\n"
            "    @Override public boolean isAccountNonExpired() { return true; }\n"
            "    @Override public boolean isAccountNonLocked() { return true; }\n"
            "    @Override public boolean isCredentialsNonExpired() { return true; }\n"
            "    @Override public boolean isEnabled() { return enabled; }\n"
            "}\n");
    fclose(f);

    /* UserRepository.java */
    snprintf(path, sizeof(path), "%s/UserRepository.java", userDir);
    f = fopen(path, "w");
    if (!f)
    {
        perror("UserRepository.java");
        return;
    }

    fprintf(f,
            "package com.example.app.user;\n"
            "\n"
            "import org.springframework.data.jpa.repository.JpaRepository;\n"
            "import java.util.Optional;\n"
            "\n"
            "public interface UserRepository extends JpaRepository<User, Long> {\n"
            "    Optional<User> findByEmail(String email);\n"
            "    boolean existsByEmail(String email);\n"
            "}\n");
    fclose(f);
}
