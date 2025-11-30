#include "crud_generator.h"
#include "file_util.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

/* --- Helpers --- */
static void make_base_dirs(char *base, size_t n, const char *root)
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

static void to_class_name(char *dest, const char *src)
{
    strcpy(dest, src);
    if (dest[0] >= 'a' && dest[0] <= 'z')
        dest[0] -= 32;
}

/* --- Logic Builders --- */

static void build_fields_string(char *buf, size_t size, const TableDef *table) {
    buf[0] = '\0';
    char temp[1024];

    for (int i = 0; i < table->field_count; i++) {
        FieldDef fd = table->fields[i];
        
        if (fd.type == FT_AUTO_ID) {
            snprintf(temp, sizeof(temp), "    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long %s;\n\n", fd.name);
        } else if (fd.type == FT_TEXT) {
            snprintf(temp, sizeof(temp), "    private String %s;\n\n", fd.name);
        } else if (fd.type == FT_LONGTEXT) {
            snprintf(temp, sizeof(temp), "    @Column(columnDefinition = \"TEXT\")\n    private String %s;\n\n", fd.name);
        } else if (fd.type == FT_DATE) {
            snprintf(temp, sizeof(temp), "    private LocalDate %s;\n\n", fd.name);
        } else if (fd.type == FT_CHOICE) {
            char enumName[128];
            to_class_name(enumName, fd.name);
            char choicesStr[512] = "";
            for(int k=0; k<fd.choice_count; k++) {
                strcat(choicesStr, fd.choices[k]);
                if(k < fd.choice_count-1) strcat(choicesStr, ", ");
            }
            snprintf(temp, sizeof(temp),
                "    @Enumerated(EnumType.STRING)\n    private %sEnum %s;\n\n    public enum %sEnum { %s }\n\n", 
                enumName, fd.name, enumName, choicesStr);
        } else if (fd.type == FT_REF) {
            char refClass[128];
            to_class_name(refClass, fd.ref_table);
            snprintf(temp, sizeof(temp), "    @ManyToOne\n    @JoinColumn(name = \"%s_id\")\n    private %s %s;\n\n", fd.name, refClass, fd.name);
        }
        strncat(buf, temp, size - strlen(buf) - 1);
    }
}

static void build_methods_string(char *buf, size_t size, const TableDef *table) {
    buf[0] = '\0';
    char temp[1024];
    for (int i = 0; i < table->field_count; i++) {
        FieldDef fd = table->fields[i];
        char capName[128];
        to_class_name(capName, fd.name);
        char typeStr[128] = "String";
        if (fd.type == FT_AUTO_ID) strcpy(typeStr, "Long");
        if (fd.type == FT_DATE)    strcpy(typeStr, "LocalDate");
        if (fd.type == FT_REF)     to_class_name(typeStr, fd.ref_table);
        if (fd.type == FT_CHOICE) {
            char enumName[128];
            to_class_name(enumName, fd.name);
            snprintf(typeStr, sizeof(typeStr), "%sEnum", enumName);
        }
        snprintf(temp, sizeof(temp),
            "    public %s get%s() { return %s; }\n    public void set%s(%s %s) { this.%s = %s; }\n\n",
            typeStr, capName, fd.name, capName, typeStr, fd.name, fd.name, fd.name);
        strncat(buf, temp, size - strlen(buf) - 1);
    }
}

/* --- Controller Logic Builder --- */

static void build_controller_methods(char *buf, size_t size, const TableDef *table, const AppConfig *cfg) {
    buf[0] = '\0';
    char className[128];
    to_class_name(className, table->name);

    int admin_view_all = 0, admin_view_own = 0, admin_create = 0;
    int user_view_all = 0, user_view_own = 0, user_create = 0;

    // 1. Analyze Permissions
    for (int i = 0; i < cfg->permission_count; i++) {
        PermissionDef p = cfg->permissions[i];
        if (strcmp(p.table_name, table->name) == 0) {
            int is_admin = (strcasecmp(p.role, "ADMIN") == 0);
            for (int j = 0; j < p.action_count; j++) {
                if (strcmp(p.actions[j], "view-all") == 0) {
                    if (is_admin) admin_view_all++;
                    else user_view_all++;
                }
                if (strcmp(p.actions[j], "view-own") == 0) {
                    if (is_admin) admin_view_own++;
                    else user_view_own++;
                }
                if (strcmp(p.actions[j], "create") == 0 || strcmp(p.actions[j], "create-own") == 0) {
                    if (is_admin) admin_create++;
                    else user_create++;
                }
            }
        }
    }

    char temp[2048];

    // 2. Generate GET All
    if (admin_view_all || user_view_all) {
        const char *auth = "";
        if (admin_view_all && !user_view_all) auth = "    @PreAuthorize(\"hasAuthority('ROLE_ADMIN')\")\n";
        else if (!admin_view_all && user_view_all) auth = "    @PreAuthorize(\"hasAuthority('ROLE_USER')\")\n";
        
        snprintf(temp, sizeof(temp),
            "%s    @GetMapping(\"\")\n"
            "    public List<%s> getAll() {\n"
            "        return repository.findAll();\n"
            "    }\n\n", auth, className);
        strncat(buf, temp, size - strlen(buf) - 1);
    }

    // 3. Generate GET My
    if (admin_view_own || user_view_own) {
        const char *auth = "";
        if (admin_view_own && !user_view_own) auth = "    @PreAuthorize(\"hasAuthority('ROLE_ADMIN')\")\n";
        else if (!admin_view_own && user_view_own) auth = "    @PreAuthorize(\"hasAuthority('ROLE_USER')\")\n";

        snprintf(temp, sizeof(temp),
            "%s    @GetMapping(\"/my\")\n"
            "    public List<%s> getMine() {\n"
            "        return repository.findByOwner(getCurrentUser());\n"
            "    }\n\n", auth, className);
        strncat(buf, temp, size - strlen(buf) - 1);
    }

    // 4. Generate POST Create
    if (admin_create || user_create) {
        const char *auth = "";
        if (admin_create && !user_create) auth = "    @PreAuthorize(\"hasAuthority('ROLE_ADMIN')\")\n";
        else if (!admin_create && user_create) auth = "    @PreAuthorize(\"hasAuthority('ROLE_USER')\")\n";

        snprintf(temp, sizeof(temp),
            "%s    @PostMapping\n"
            "    public %s create(@RequestBody %s entity) {\n"
            "        entity.setOwner(getCurrentUser());\n"
            "        return repository.save(entity);\n"
            "    }\n\n", auth, className, className);
        strncat(buf, temp, size - strlen(buf) - 1);
    }
}

/* --- Generators --- */

static void generate_entity(const char *basePath, const TableDef *table)
{
    char path[512], className[128];
    to_class_name(className, table->name);
    snprintf(path, sizeof(path), "%s/entity/%s.java", basePath, className);

    char fieldsStr[8192]; build_fields_string(fieldsStr, sizeof(fieldsStr), table);
    char methodsStr[8192]; build_methods_string(methodsStr, sizeof(methodsStr), table);

    TemplateData data; init_template_data(&data);
    add_replacement(&data, "{{PackageName}}", "com.example.app.entity");
    add_replacement(&data, "{{TableName}}", table->name);
    add_replacement(&data, "{{ClassName}}", className);
    add_replacement(&data, "{{Fields}}", fieldsStr);
    add_replacement(&data, "{{Methods}}", methodsStr);
    add_replacement(&data, "{{ExtraImports}}", ""); 

    write_template_to_file("backend-generator/templates/Entity.java.tpl", path, &data);
    free_template_data(&data);
}

static void generate_repository(const char *basePath, const TableDef *table)
{
    char path[512], className[128];
    to_class_name(className, table->name);
    snprintf(path, sizeof(path), "%s/repository/%sRepository.java", basePath, className);

    TemplateData data; init_template_data(&data);
    add_replacement(&data, "{{ClassName}}", className);

    write_template_to_file("backend-generator/templates/Repository.java.tpl", path, &data);
    free_template_data(&data);
}

static void generate_controller(const char *basePath, const TableDef *table, const AppConfig *cfg)
{
    char path[512], className[128];
    to_class_name(className, table->name);
    snprintf(path, sizeof(path), "%s/controller/%sController.java", basePath, className);

    // 1. Build the dynamic methods string based on permissions
    char methodsStr[8192];
    build_controller_methods(methodsStr, sizeof(methodsStr), table, cfg);

    // 2. Inject into template
    TemplateData data; init_template_data(&data);
    add_replacement(&data, "{{ClassName}}", className);
    add_replacement(&data, "{{TableName}}", table->name);
    add_replacement(&data, "{{ControllerMethods}}", methodsStr);

    write_template_to_file("backend-generator/templates/Controller.java.tpl", path, &data);
    free_template_data(&data);
}

void generate_crud(const AppConfig *cfg, const char *root)
{
    char base[512], entityDir[512], repoDir[512], ctrlDir[512];
    make_base_dirs(base, sizeof(base), root);

    snprintf(entityDir, sizeof(entityDir), "%s/entity", base); ensure_dir(entityDir);
    snprintf(repoDir, sizeof(repoDir), "%s/repository", base); ensure_dir(repoDir);
    snprintf(ctrlDir, sizeof(ctrlDir), "%s/controller", base); ensure_dir(ctrlDir);

    for (int i = 0; i < cfg->table_count; i++) {
        generate_entity(base, &cfg->tables[i]);
        generate_repository(base, &cfg->tables[i]);
        generate_controller(base, &cfg->tables[i], cfg);
    }
}