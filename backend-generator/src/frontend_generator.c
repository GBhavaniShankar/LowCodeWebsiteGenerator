#include "frontend_bridge.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* Helper to map C types to the JSON types your Node script expects */
static const char* get_json_type(FieldType ft) {
    switch (ft) {
        case FT_AUTO_ID: return "auto_id";
        case FT_TEXT:    return "text";
        case FT_LONGTEXT:return "longtext";
        case FT_DATE:    return "date";
        case FT_CHOICE:  return "choice";
        case FT_REF:     return "ref";
        default:         return "string";
    }
}

void generate_frontend_json(const AppConfig *cfg, const char *root) {
    char path[512];
    // We write app-spec.json to the output directory
    snprintf(path, sizeof(path), "%s/app-spec.json", root);

    FILE *f = fopen(path, "w");
    if (!f) {
        perror("Failed to create app-spec.json");
        return;
    }

    fprintf(f, "{\n");
    fprintf(f, "  \"project_name\": \"%s\",\n", cfg->project_name ? cfg->project_name : "MyApp");
    
    // --- 1. RESOURCES (Tables) ---
    fprintf(f, "  \"resources\": [\n");
    for (int i = 0; i < cfg->table_count; i++) {
        TableDef t = cfg->tables[i];
        fprintf(f, "    {\n");
        fprintf(f, "      \"name\": \"%s\",\n", t.name);
        fprintf(f, "      \"fields\": [\n");
        
        for (int j = 0; j < t.field_count; j++) {
            FieldDef fd = t.fields[j];
            fprintf(f, "        { \"name\": \"%s\", \"type\": \"%s\"", fd.name, get_json_type(fd.type));
            
            // Handle CHOICE options
            if (fd.type == FT_CHOICE && fd.choice_count > 0) {
                fprintf(f, ", \"options\": [");
                for(int k=0; k<fd.choice_count; k++) {
                    fprintf(f, "\"%s\"%s", fd.choices[k], (k < fd.choice_count - 1) ? ", " : "");
                }
                fprintf(f, "]");
            }
            
            // Handle REF table
            if (fd.type == FT_REF && fd.ref_table) {
                fprintf(f, ", \"ref_table\": \"%s\"", fd.ref_table);
            }

            fprintf(f, " }%s\n", (j < t.field_count - 1) ? "," : "");
        }
        fprintf(f, "      ]\n");
        fprintf(f, "    }%s\n", (i < cfg->table_count - 1) ? "," : "");
    }
    fprintf(f, "  ],\n");

    // --- 2. PERMISSIONS (Endpoints by Role) ---
    fprintf(f, "  \"endpoints_by_role\": {\n");

    // We assume two roles for now: ADMIN and USER. 
    // We loop through the permissions config to find what matches.
    const char *roles[] = {"ADMIN", "USER"};
    for (int r = 0; r < 2; r++) {
        const char *current_role = roles[r];
        fprintf(f, "    \"%s\": [\n", current_role);
        
        int first_entry = 1;
        for (int i = 0; i < cfg->permission_count; i++) {
            PermissionDef p = cfg->permissions[i];
            
            // Case-insensitive comparison for role
            if (strcasecmp(p.role, current_role) == 0) {
                for (int j = 0; j < p.action_count; j++) {
                    if (!first_entry) fprintf(f, ",\n");
                    fprintf(f, "      { \"resource\": \"%s\", \"action\": \"%s\" }", 
                            p.table_name, p.actions[j]);
                    first_entry = 0;
                }
            }
        }
        fprintf(f, "\n    ]%s\n", (r < 1) ? "," : "");
    }

    fprintf(f, "  }\n");
    fprintf(f, "}\n");
    fclose(f);
    
    printf("📄 Generated Frontend Spec: %s\n", path);
}