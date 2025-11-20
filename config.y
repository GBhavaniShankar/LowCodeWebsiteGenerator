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

%token PROJECT_K AUTH_K MAILVERIF_K
%token COLON NEWLINE
%token YES NO
%token <str> IDENT
%token <str> AUTH_VALUE

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
  ;

project_stmt:
    PROJECT_K COLON IDENT NEWLINE
    { g_config.project_name = $3; }
  ;

auth_stmt:
    AUTH_K COLON AUTH_VALUE NEWLINE
    {
        if (strcmp($3, "jwt") == 0) g_config.auth_type = AUTH_JWT;
        else g_config.auth_type = AUTH_BASIC;
        free($3);
    }
  ;

mail_stmt:
    MAILVERIF_K COLON YES NEWLINE
    { g_config.mail_verification = 1; }
  | MAILVERIF_K COLON NO NEWLINE
    { g_config.mail_verification = 0; }
  ;

%%

void yyerror(const char *s) {
    fprintf(stderr, "Parse error: %s\n", s);
}
