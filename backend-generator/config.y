%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "config.h"

int yylex(void);
void yyerror(const char *s);

extern AppConfig g_config;

/* Helpers */
void add_table(TableDef t);
void add_field_to_current_table(FieldDef f);
void add_permission(PermissionDef p);
%}

%union {
    char *str;
    int val;
    FieldType ftype;
    struct {
        char **list;
        int count;
    } strlist;
}

/* Tokens */
%token PROJECT_K AUTH_K EMAIL_VERIFY_K ADMIN_EMAIL_K ADMIN_PASSWORD_K
%token PORTAL_START_K PORTAL_END_K
%token TABLE_K FIELD_K
%token COLON NEWLINE COMMA LBRACKET RBRACKET PIPE DOT
%token ON_K CAN_K
%token <str> IDENT ROLE_ADMIN ROLE_USER

/* Types */
%token TYPE_AUTO_ID TYPE_TEXT TYPE_LONGTEXT TYPE_DATE TYPE_CHOICE TYPE_REF

/* Non-terminal types */
%type <str> ident_val
%type <str> role_ident
%type <ftype> field_type
%type <strlist> choice_list action_list

%define parse.error verbose

%%

config:
    opt_newlines statements opt_newlines
    ;

opt_newlines:
    /* empty */
  | opt_newlines NEWLINE
  ;

statements:
    statement
  | statements statement
  ;

statement:
    project_stmt
  | auth_stmt
  | email_verify_stmt
  | admin_email_stmt
  | admin_password_stmt
  | portal_start_stmt
  | portal_end_stmt
  | table_def
  | permission_def
  | NEWLINE
  ;

ident_val: IDENT { $$ = $1; };

project_stmt:
    PROJECT_K COLON ident_val NEWLINE { g_config.project_name = $3; }
  ;

auth_stmt:
    AUTH_K COLON ident_val NEWLINE {
        g_config.auth_type = (strcasecmp($3, "jwt") == 0) ? AUTH_JWT : AUTH_BASIC;
        free($3);
    }
  ;

email_verify_stmt:
    EMAIL_VERIFY_K COLON ident_val NEWLINE {
        g_config.mail_verification = (strcasecmp($3, "Yes") == 0);
        free($3);
    }
  ;

admin_email_stmt:
    ADMIN_EMAIL_K COLON ident_val NEWLINE { g_config.admin_email = $3; }
  ;

admin_password_stmt:
    ADMIN_PASSWORD_K COLON ident_val NEWLINE { g_config.admin_password = $3; }
  ;

portal_start_stmt:
    PORTAL_START_K COLON ident_val NEWLINE { g_config.portal_start = $3; }
  ;

portal_end_stmt:
    PORTAL_END_K COLON ident_val NEWLINE { g_config.portal_end = $3; }
  ;

/* --- Table Definitions --- */

table_def:
    TABLE_K ident_val NEWLINE {
        TableDef t;
        t.name = $2;
        t.fields = NULL;
        t.field_count = 0;
        add_table(t);
    }
    field_list
  ;

field_list:
    /* empty */
  | field_list field_line
  ;

field_line:
    FIELD_K ident_val COLON field_type opt_extras NEWLINE {
        FieldDef f;
        f.name = $2;
        f.type = $4;
        
        /* Globals set by opt_extras */
        extern char **g_temp_choices;
        extern int g_temp_choice_count;
        extern char *g_temp_ref_table;
        extern char *g_temp_ref_col;

        f.choices = NULL;
        f.choice_count = 0;
        f.ref_table = NULL;
        f.ref_column = NULL;

        if ($4 == FT_CHOICE) {
            f.choices = g_temp_choices;
            f.choice_count = g_temp_choice_count;
        } else if ($4 == FT_REF) {
            f.ref_table = g_temp_ref_table;
            f.ref_column = g_temp_ref_col;
        }

        /* Reset globals */
        g_temp_choices = NULL;
        g_temp_choice_count = 0;
        g_temp_ref_table = NULL;
        g_temp_ref_col = NULL;

        add_field_to_current_table(f);
    }
  ;

field_type:
    TYPE_AUTO_ID   { $$ = FT_AUTO_ID; }
  | TYPE_TEXT      { $$ = FT_TEXT; }
  | TYPE_LONGTEXT  { $$ = FT_LONGTEXT; }
  | TYPE_DATE      { $$ = FT_DATE; }
  | TYPE_CHOICE    { $$ = FT_CHOICE; }
  | TYPE_REF       { $$ = FT_REF; }
  ;

opt_extras:
    /* empty */
  | LBRACKET choice_list RBRACKET {
      extern char **g_temp_choices;
      extern int g_temp_choice_count;
      g_temp_choices = $2.list;
      g_temp_choice_count = $2.count;
  }
  | ident_val {
      /* REF TableName */
      extern char *g_temp_ref_table;
      extern char *g_temp_ref_col;
      g_temp_ref_table = $1;
      g_temp_ref_col = strdup("id"); // Default if no column specified
  }
  | ident_val DOT ident_val {
      /* REF TableName.ColumnName */
      extern char *g_temp_ref_table;
      extern char *g_temp_ref_col;
      g_temp_ref_table = $1;
      g_temp_ref_col = $3;
  }
  ;

choice_list:
    ident_val {
        $$.count = 1;
        $$.list = malloc(sizeof(char*));
        $$.list[0] = $1;
    }
  | choice_list PIPE ident_val {
        $$.count = $1.count + 1;
        $$.list = realloc($1.list, $$.count * sizeof(char*));
        $$.list[$$.count - 1] = $3;
    }
  ;

/* --- Permissions --- */

permission_def:
    role_ident ON_K ident_val COLON NEWLINE CAN_K action_list NEWLINE {
        PermissionDef p;
        p.role = $1;
        p.table_name = $3;
        p.actions = $7.list;
        p.action_count = $7.count;
        add_permission(p);
    }
  ;

role_ident:
    ROLE_ADMIN { $$ = $1; }
  | ROLE_USER  { $$ = $1; }
  ;

action_list:
    ident_val {
        $$.count = 1;
        $$.list = malloc(sizeof(char*));
        $$.list[0] = $1;
    }
  | action_list COMMA ident_val {
        $$.count = $1.count + 1;
        $$.list = realloc($1.list, $$.count * sizeof(char*));
        $$.list[$$.count - 1] = $3;
    }
  ;

%%

/* Globals for parsing state */
char **g_temp_choices = NULL;
int g_temp_choice_count = 0;
char *g_temp_ref_table = NULL;
char *g_temp_ref_col = NULL;

void yyerror(const char *s) {
    fprintf(stderr, "Parse error: %s\n", s);
}

/* Helper implementations */

void add_table(TableDef t) {
    g_config.table_count++;
    g_config.tables = realloc(g_config.tables, g_config.table_count * sizeof(TableDef));
    g_config.tables[g_config.table_count - 1] = t;
}

void add_field_to_current_table(FieldDef f) {
    if (g_config.table_count == 0) return;
    TableDef *t = &g_config.tables[g_config.table_count - 1];
    t->field_count++;
    t->fields = realloc(t->fields, t->field_count * sizeof(FieldDef));
    t->fields[t->field_count - 1] = f;
}

void add_permission(PermissionDef p) {
    g_config.permission_count++;
    g_config.permissions = realloc(g_config.permissions, g_config.permission_count * sizeof(PermissionDef));
    g_config.permissions[g_config.permission_count - 1] = p;
}