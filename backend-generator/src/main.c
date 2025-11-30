#include <stdio.h>
#include <stdlib.h>
#include "config.h"
#include "frontend_bridge.h"

/* generators */
#include "file_util.h"
#include "pom_generator.h"
#include "resources_generator.h"
#include "java_core_generator.h"
#include "security_generator.h"
#include "user_generator.h"
#include "auth_generator.h"
#include "demo_generator.h"
#include "init_generator.h"
#include "crud_generator.h" /* New */


/* parser stuff */
int yyparse(void);
extern FILE *yyin;

/* Global config instance */
AppConfig g_config;


int main(int argc, char **argv)
{
    if (argc != 3)
    {
        fprintf(stderr, "Usage: %s <config.cfg> <output-project-dir>\n", argv[0]);
        return 1;
    }

    const char *configPath = argv[1];
    const char *root = argv[2];

    yyin = fopen(configPath, "r");
    if (!yyin)
    {
        perror("config file");
        return 1;
    }

    /* Initialize defaults */
    g_config.project_name = NULL;
    g_config.auth_type = AUTH_JWT;
    g_config.mail_verification = 0;
    g_config.admin_email = NULL;
    g_config.admin_password = NULL;
    g_config.portal_start = NULL;
    g_config.portal_end = NULL;
    g_config.tables = NULL;
    g_config.table_count = 0;
    g_config.permissions = NULL;
    g_config.permission_count = 0;

    if (yyparse() != 0)
    {
        fprintf(stderr, "Parsing failed.\n");
        fclose(yyin);
        return 1;
    }
    fclose(yyin);

    ensure_dir(root);

    generate_pom(&g_config, root);
    generate_resources(&g_config, root);
    generate_core_app(&g_config, root);
    generate_security(&g_config, root);
    generate_user(&g_config, root);
    generate_auth(&g_config, root);
    generate_demo(&g_config, root);
    generate_init(&g_config, root);
    generate_crud(&g_config, root); /* New */

    generate_verification(&g_config, root); // <--- MAKE SURE THIS IS HERE!
    generate_mail(&g_config, root);         // <--- AND THIS

    // Optional: Free memory for tables/permissions here if desired

    generate_frontend_json(&g_config, root);

    printf("Project generated at: %s\n", root);
    // ...

    return 0;
}