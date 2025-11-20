#ifndef CONFIG_H
#define CONFIG_H

typedef enum
{
    AUTH_JWT,
    AUTH_BASIC
} AuthType;

typedef struct
{
    char *project_name;
    AuthType auth_type;
    int mail_verification; /* 1 = email verification enabled, 0 = disabled */
} AppConfig;

extern AppConfig g_config;

#endif
