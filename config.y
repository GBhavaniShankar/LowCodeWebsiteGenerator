%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "config.h"

int yylex(void);
void yyerror(const char *s);

extern AppConfig g_config;
%}

%union {
    char *str;
}

/* tokens */
%token PROJECT_K AUTH_K MAILVERIF_K ADMIN_EMAIL_K ADMIN_PASSWORD_K
%token COLON NEWLINE
%token <str> IDENT

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
  | mail_stmt
  | admin_email_stmt
  | admin_password_stmt
  ;

project_stmt:
    PROJECT_K COLON IDENT NEWLINE
    {
        g_config.project_name = $3;
    }
  ;

auth_stmt:
    AUTH_K COLON IDENT NEWLINE
    {
        if (strcmp($3, "jwt") == 0) {
            g_config.auth_type = AUTH_JWT;
        } else {
            g_config.auth_type = AUTH_BASIC;
        }
        free($3);
    }
  ;

mail_stmt:
    MAILVERIF_K COLON IDENT NEWLINE
    {
        if (strcmp($3, "Yes") == 0 || strcmp($3, "yes") == 0) {
            g_config.mail_verification = 1;
        } else {
            g_config.mail_verification = 0;
        }
        free($3);
    }
  ;

admin_email_stmt:
    ADMIN_EMAIL_K COLON IDENT NEWLINE
    {
        g_config.admin_email = $3;  /* keep string */
    }
  ;

admin_password_stmt:
    ADMIN_PASSWORD_K COLON IDENT NEWLINE
    {
        g_config.admin_password = $3; /* keep string */
    }
  ;

%%

void yyerror(const char *s) {
    fprintf(stderr, "Parse error: %s\n", s);
}
