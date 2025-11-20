#ifndef CONFIG_H
#define CONFIG_H

typedef enum {
    AUTH_JWT,
    AUTH_BASIC
} AuthType;

typedef struct {
    char *project_name;
    AuthType auth_type;
    int mail_verification;   /* 1 = yes, 0 = no */

    char *admin_email;       /* new */
    char *admin_password;    /* new */
} AppConfig;

extern AppConfig g_config;

#endif /* CONFIG_H */
