#ifndef FILE_UTIL_H
#define FILE_UTIL_H

#include <stddef.h>

void ensure_dir(const char *path);
void join_path(char *buf, size_t bufsize, const char *root, const char *sub);


typedef struct {
    char *key;      /* e.g. "{{ClassName}}" */
    char *value;    /* e.g. "Team" */
} Replacement;

typedef struct {
    Replacement *replacements;
    int count;
} TemplateData;

/* --- NEW: Template Engine Functions --- */

/* Initialize a new data structure */
void init_template_data(TemplateData *data);

/* Add a key-value pair to replace */
void add_replacement(TemplateData *data, const char *key, const char *value);

/* Clean up memory */
void free_template_data(TemplateData *data);

/* The Magic Function: Reads template_path, replaces vars, writes to output_path */
void write_template_to_file(const char *template_path, const char *output_path, TemplateData *data);

#endif
