%{
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "config.tab.h"
%}

%%

[ \t\r]+         ;       /* skip whitespace */

"Project"        { return PROJECT_K; }
"Auth"           { return AUTH_K; }
"MildVerification" { return MAILVERIF_K; }
"AdminEmail"     { return ADMIN_EMAIL_K; }
"AdminPassword"  { return ADMIN_PASSWORD_K; }

":"              { return COLON; }
"\n"             { return NEWLINE; }

[a-zA-Z0-9_@.\-]+ {
    yylval.str = strdup(yytext);
    return IDENT;
}

"#".*            ;

.                ;

%%

int yywrap(void) {
    return 1;
}
