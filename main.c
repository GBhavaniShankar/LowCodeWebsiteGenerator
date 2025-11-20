#include <stdio.h>
#include <stdlib.h>
#include "config.h"

/* parser stuff */
int yyparse(void);
extern FILE *yyin;
AppConfig g_config = {NULL, AUTH_JWT, 0, NULL, NULL};

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

    /* defaults */
    g_config.project_name = NULL;
    g_config.auth_type = AUTH_JWT;
    g_config.mail_verification = 0;
    g_config.admin_email = NULL;
    g_config.admin_password = NULL;

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

    printf("Project generated at: %s\n", root);
    return 0;
}
