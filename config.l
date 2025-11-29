%{
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "config.h"
#include "config.tab.h"
%}

%x IN_CHOICE

%%

[ \t\r]+         ;       /* skip whitespace */

"Project"        { return PROJECT_K; }
"Auth"           { return AUTH_K; }
"EmailVerify"    { return EMAIL_VERIFY_K; }
"MildVerification" { return EMAIL_VERIFY_K; }
"AdminEmail"     { return ADMIN_EMAIL_K; }
"AdminPassword"  { return ADMIN_PASSWORD_K; }
"PortalStart"    { return PORTAL_START_K; }
"PortalEnd"      { return PORTAL_END_K; }

"Table"          { return TABLE_K; }
"Field"          { return FIELD_K; }

"AUTO-ID"        { return TYPE_AUTO_ID; }
"TEXT"           { return TYPE_TEXT; }
"LONGTEXT"       { return TYPE_LONGTEXT; }
"DATE"           { return TYPE_DATE; }
"CHOICE"         { return TYPE_CHOICE; }
"REF"            { return TYPE_REF; }

"Admin"          { yylval.str = strdup("ADMIN"); return ROLE_ADMIN; }
"User"           { yylval.str = strdup("USER"); return ROLE_USER; }
"on"             { return ON_K; }
"can"            { return CAN_K; }

"["              { BEGIN(IN_CHOICE); return LBRACKET; }
<IN_CHOICE>"]"   { BEGIN(INITIAL); return RBRACKET; }
<IN_CHOICE>"|"   { return PIPE; }
<IN_CHOICE>[a-zA-Z0-9_]+ {
    yylval.str = strdup(yytext);
    return IDENT;
}

":"              { return COLON; }
"."              { return DOT; }
","              { return COMMA; }
"\n"             { return NEWLINE; }

[a-zA-Z0-9_@.\-]+ {
    yylval.str = strdup(yytext);
    return IDENT;
}

"#".* ;

.                ;

%%

int yywrap(void) {
    return 1;
}