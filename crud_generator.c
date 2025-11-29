#include "crud_generator.h"
#include "file_util.h"
#include <stdio.h>
#include <string.h>
#include <ctype.h>

static void make_base_dirs(char *base, size_t n, const char *root)
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

/* Helper to uppercase first letter */
static void to_class_name(char *dest, const char *src)
{
    strcpy(dest, src);
    if (dest[0] >= 'a' && dest[0] <= 'z')
        dest[0] -= 32;
}

static void generate_entity(const char *basePath, const TableDef *table)
{
    char path[512];
    char className[128];
    to_class_name(className, table->name);

    snprintf(path, sizeof(path), "%s/entity/%s.java", basePath, className);
    FILE *f = fopen(path, "w");
    if (!f)
        return;

    fprintf(f, "package com.example.app.entity;\n\n");
    fprintf(f, "import jakarta.persistence.*;\n");
    fprintf(f, "import java.time.LocalDate;\n");
    fprintf(f, "import com.example.app.user.User;\n");
    fprintf(f, "import com.fasterxml.jackson.annotation.JsonProperty;\n\n");

    fprintf(f, "@Entity\n@Table(name = \"%s_tbl\")\n", table->name);
    fprintf(f, "public class %s {\n", className);

    // Fields
    for (int i = 0; i < table->field_count; i++)
    {
        FieldDef fd = table->fields[i];

        if (fd.type == FT_AUTO_ID)
        {
            fprintf(f, "    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
            fprintf(f, "    private Long %s;\n\n", fd.name);
        }
        else if (fd.type == FT_TEXT)
        {
            fprintf(f, "    private String %s;\n\n", fd.name);
        }
        else if (fd.type == FT_LONGTEXT)
        {
            fprintf(f, "    @Column(columnDefinition = \"TEXT\")\n");
            fprintf(f, "    private String %s;\n\n", fd.name);
        }
        else if (fd.type == FT_DATE)
        {
            fprintf(f, "    private LocalDate %s;\n\n", fd.name);
        }
        else if (fd.type == FT_CHOICE)
        {
            char enumName[128];
            to_class_name(enumName, fd.name);
            fprintf(f, "    @Enumerated(EnumType.STRING)\n");
            fprintf(f, "    private %sEnum %s;\n\n", enumName, fd.name);

            fprintf(f, "    public enum %sEnum { ", enumName);
            for (int k = 0; k < fd.choice_count; k++)
            {
                fprintf(f, "%s%s", fd.choices[k], (k < fd.choice_count - 1) ? ", " : "");
            }
            fprintf(f, " }\n\n");
        }
        else if (fd.type == FT_REF)
        {
            // Foreign Key
            char refClass[128];
            to_class_name(refClass, fd.ref_table);
            fprintf(f, "    @ManyToOne\n");

            if (fd.ref_column && strcmp(fd.ref_column, "id") != 0)
            {
                fprintf(f, "    @JoinColumn(name = \"%s_%s\", referencedColumnName = \"%s\")\n",
                        fd.name, fd.ref_column, fd.ref_column);
            }
            else
            {
                fprintf(f, "    @JoinColumn(name = \"%s_id\")\n", fd.name);
            }

            fprintf(f, "    private %s %s;\n\n", refClass, fd.name);
        }
    }

    // Owner field
    fprintf(f, "    @ManyToOne\n    @JoinColumn(name = \"user_id\")\n");
    fprintf(f, "    @JsonProperty(access = JsonProperty.Access.READ_ONLY)\n");
    fprintf(f, "    private User owner;\n\n");

    // Getters Setters
    fprintf(f, "    public User getOwner() { return owner; }\n");
    fprintf(f, "    public void setOwner(User owner) { this.owner = owner; }\n");

    for (int i = 0; i < table->field_count; i++)
    {
        FieldDef fd = table->fields[i];

        char typeStr[128] = "String";
        if (fd.type == FT_AUTO_ID)
            strcpy(typeStr, "Long");
        if (fd.type == FT_DATE)
            strcpy(typeStr, "LocalDate");
        if (fd.type == FT_CHOICE)
        {
            char enumName[128];
            to_class_name(enumName, fd.name);
            snprintf(typeStr, sizeof(typeStr), "%sEnum", enumName);
        }
        if (fd.type == FT_REF)
        {
            to_class_name(typeStr, fd.ref_table);
        }

        char capName[128];
        to_class_name(capName, fd.name);
        fprintf(f, "    public %s get%s() { return %s; }\n", typeStr, capName, fd.name);
        fprintf(f, "    public void set%s(%s %s) { this.%s = %s; }\n",
                capName, typeStr, fd.name, fd.name, fd.name);
    }

    fprintf(f, "}\n");
    fclose(f);
}

static void generate_repository(const char *basePath, const TableDef *table)
{
    char path[512];
    char className[128];
    to_class_name(className, table->name);

    snprintf(path, sizeof(path), "%s/repository/%sRepository.java", basePath, className);
    FILE *f = fopen(path, "w");
    if (!f)
        return;

    fprintf(f, "package com.example.app.repository;\n\n");
    fprintf(f, "import com.example.app.entity.%s;\n", className);
    fprintf(f, "import com.example.app.user.User;\n");
    fprintf(f, "import org.springframework.data.jpa.repository.JpaRepository;\n");
    fprintf(f, "import java.util.List;\n\n");

    fprintf(f, "public interface %sRepository extends JpaRepository<%s, Long> {\n", className, className);
    fprintf(f, "    List<%s> findByOwner(User owner);\n", className);
    fprintf(f, "}\n");
    fclose(f);
}

static void generate_controller(const char *basePath, const TableDef *table, const AppConfig *cfg)
{
    char path[512];
    char className[128];
    to_class_name(className, table->name);

    snprintf(path, sizeof(path), "%s/controller/%sController.java", basePath, className);
    FILE *f = fopen(path, "w");
    if (!f)
        return;

    fprintf(f, "package com.example.app.controller;\n\n");
    fprintf(f, "import com.example.app.entity.%s;\n", className);
    fprintf(f, "import com.example.app.repository.%sRepository;\n", className);
    fprintf(f, "import com.example.app.user.User;\n");
    fprintf(f, "import com.example.app.user.UserRepository;\n");
    fprintf(f, "import org.springframework.web.bind.annotation.*;\n");
    fprintf(f, "import org.springframework.security.access.prepost.PreAuthorize;\n"); // NEW IMPORT
    fprintf(f, "import org.springframework.security.core.Authentication;\n");
    fprintf(f, "import org.springframework.security.core.context.SecurityContextHolder;\n");
    fprintf(f, "import java.util.List;\n\n");

    fprintf(f, "@RestController\n@RequestMapping(\"/api/%s\")\n", table->name);
    fprintf(f, "public class %sController {\n\n", className);

    fprintf(f, "    private final %sRepository repository;\n", className);
    fprintf(f, "    private final UserRepository userRepository;\n\n");

    fprintf(f, "    public %sController(%sRepository repository, UserRepository userRepository) {\n", className, className);
    fprintf(f, "        this.repository = repository;\n");
    fprintf(f, "        this.userRepository = userRepository;\n");
    fprintf(f, "    }\n\n");

    fprintf(f, "    private User getCurrentUser() {\n");
    fprintf(f, "        String email = SecurityContextHolder.getContext().getAuthentication().getName();\n");
    fprintf(f, "        return userRepository.findByEmail(email).orElseThrow();\n");
    fprintf(f, "    }\n\n");

    /* NEW LOGIC: Track Admin vs User permissions separately
     */
    int admin_view_all = 0;
    int admin_view_own = 0;
    int admin_create = 0;

    int user_view_all = 0;
    int user_view_own = 0;
    int user_create = 0;

    for (int i = 0; i < cfg->permission_count; i++)
    {
        PermissionDef p = cfg->permissions[i];
        if (strcmp(p.table_name, table->name) == 0)
        {
            int is_admin = (strcasecmp(p.role, "ADMIN") == 0);

            for (int j = 0; j < p.action_count; j++)
            {
                if (strcmp(p.actions[j], "view-all") == 0)
                {
                    if (is_admin)
                        admin_view_all = 1;
                    else
                        user_view_all = 1;
                }
                if (strcmp(p.actions[j], "view-own") == 0)
                {
                    if (is_admin)
                        admin_view_own = 1;
                    else
                        user_view_own = 1;
                }
                if (strcmp(p.actions[j], "create") == 0 || strcmp(p.actions[j], "create-own") == 0)
                {
                    if (is_admin)
                        admin_create = 1;
                    else
                        user_create = 1;
                }
            }
        }
    }

    /* --- Generate GET All --- */
    if (admin_view_all || user_view_all)
    {
        // If only admin can view, lock it to ADMIN
        if (admin_view_all && !user_view_all)
        {
            fprintf(f, "    @PreAuthorize(\"hasAuthority('ROLE_ADMIN')\")\n");
        }
        // If only user can view (unlikely, but possible), lock to USER
        else if (!admin_view_all && user_view_all)
        {
            fprintf(f, "    @PreAuthorize(\"hasAuthority('ROLE_USER')\")\n");
        }
        // If both can view, no PreAuthorize needed (just authenticated)

        fprintf(f, "    @GetMapping(\"\")\n");
        fprintf(f, "    public List<%s> getAll() {\n", className);
        fprintf(f, "        return repository.findAll();\n");
        fprintf(f, "    }\n\n");
    }

    /* --- Generate GET My --- */
    if (admin_view_own || user_view_own)
    {
        if (admin_view_own && !user_view_own)
            fprintf(f, "    @PreAuthorize(\"hasAuthority('ROLE_ADMIN')\")\n");
        else if (!admin_view_own && user_view_own)
            fprintf(f, "    @PreAuthorize(\"hasAuthority('ROLE_USER')\")\n");

        fprintf(f, "    @GetMapping(\"/my\")\n");
        fprintf(f, "    public List<%s> getMine() {\n", className);
        fprintf(f, "        return repository.findByOwner(getCurrentUser());\n");
        fprintf(f, "    }\n\n");
    }

    /* --- Generate POST Create --- */
    if (admin_create || user_create)
    {
        if (admin_create && !user_create)
        {
            fprintf(f, "    @PreAuthorize(\"hasAuthority('ROLE_ADMIN')\")\n");
        }
        else if (!admin_create && user_create)
        {
            fprintf(f, "    @PreAuthorize(\"hasAuthority('ROLE_USER')\")\n");
        }

        fprintf(f, "    @PostMapping\n");
        fprintf(f, "    public %s create(@RequestBody %s entity) {\n", className, className);
        fprintf(f, "        entity.setOwner(getCurrentUser());\n");
        fprintf(f, "        return repository.save(entity);\n");
        fprintf(f, "    }\n\n");
    }

    fprintf(f, "}\n");
    fclose(f);
}

void generate_crud(const AppConfig *cfg, const char *root)
{
    char base[512], entityDir[512], repoDir[512], ctrlDir[512];
    make_base_dirs(base, sizeof(base), root);

    snprintf(entityDir, sizeof(entityDir), "%s/entity", base);
    ensure_dir(entityDir);
    snprintf(repoDir, sizeof(repoDir), "%s/repository", base);
    ensure_dir(repoDir);
    snprintf(ctrlDir, sizeof(ctrlDir), "%s/controller", base);
    ensure_dir(ctrlDir);

    for (int i = 0; i < cfg->table_count; i++)
    {
        generate_entity(base, &cfg->tables[i]);
        generate_repository(base, &cfg->tables[i]);
        generate_controller(base, &cfg->tables[i], cfg);
    }
}