%{
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "config.tab.h"
%}

%%

[ \t\r]+         ;

"Project"        { return PROJECT_K; }
"Auth"           { return AUTH_K; }
"MildVerification" { return MAILVERIF_K; }

":"              { return COLON; }
"\n"             { return NEWLINE; }

"Yes"            { return YES; }
"No"             { return NO; }

"jwt"|"basic"    { yylval.str = strdup(yytext); return AUTH_VALUE; }

[a-zA-Z_][a-zA-Z0-9_]* { yylval.str = strdup(yytext); return IDENT; }

"#".*            ;

.                ;

%%

int yywrap(void) { return 1; }
