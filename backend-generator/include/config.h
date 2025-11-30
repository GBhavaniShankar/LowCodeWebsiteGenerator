#ifndef CONFIG_H
#define CONFIG_H

#include <stddef.h>

/* --- Enums --- */
typedef enum {
    AUTH_JWT,
    AUTH_BASIC
} AuthType;

typedef enum {
    FT_AUTO_ID,
    FT_TEXT,
    FT_LONGTEXT,
    FT_DATE,
    FT_CHOICE,
    FT_REF          /* Foreign Key */
} FieldType;

/* --- Structs for Tables --- */

typedef struct {
    char *name;
    FieldType type;
    
    /* For CHOICE */
    char **choices;      
    int choice_count;

    /* For REF */
    char *ref_table;     /* e.g. "Category" */
    char *ref_column;    /* e.g. "id" (New) */
} FieldDef;

typedef struct {
    char *name;
    FieldDef *fields;
    int field_count;
} TableDef;

/* --- Structs for Permissions --- */

typedef struct {
    char *role;          /* "Admin" or "User" */
    char *table_name;
    char **actions;      /* e.g. "view-all", "create-own" */
    int action_count;
} PermissionDef;

/* --- Main Config --- */

typedef struct {
    char *project_name;
    AuthType auth_type;
    int mail_verification;
    char *admin_email;
    char *admin_password;

    char *portal_start;
    char *portal_end;

    TableDef *tables;
    int table_count;

    PermissionDef *permissions;
    int permission_count;
} AppConfig;

extern AppConfig g_config;

#endif /* CONFIG_H */