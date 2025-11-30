#include "file_util.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>

/* --- Existing Utils --- */
void ensure_dir(const char *path) {
#ifdef _WIN32
    _mkdir(path);
#else
    mkdir(path, 0777);
#endif
}

void join_path(char *buf, size_t bufsize, const char *root, const char *sub) {
    snprintf(buf, bufsize, "%s/%s", root, sub);
}

/* --- NEW: Template Engine Implementation --- */

void init_template_data(TemplateData *data) {
    data->replacements = NULL;
    data->count = 0;
}

void add_replacement(TemplateData *data, const char *key, const char *value) {
    data->count++;
    data->replacements = realloc(data->replacements, data->count * sizeof(Replacement));
    
    // Store copies of the strings to be safe
    data->replacements[data->count - 1].key = strdup(key);
    data->replacements[data->count - 1].value = value ? strdup(value) : strdup(""); 
}

void free_template_data(TemplateData *data) {
    for (int i = 0; i < data->count; i++) {
        free(data->replacements[i].key);
        free(data->replacements[i].value);
    }
    free(data->replacements);
    data->count = 0;
}

/* Internal helper: Read entire file into a generic buffer */
static char* read_file_into_string(const char *path) {
    FILE *f = fopen(path, "rb");
    if (!f) return NULL;

    fseek(f, 0, SEEK_END);
    long length = ftell(f);
    fseek(f, 0, SEEK_SET);

    char *buffer = malloc(length + 1);
    if (buffer) {
        fread(buffer, 1, length, f);
        buffer[length] = '\0';
    }
    fclose(f);
    return buffer;
}

/* Internal helper: Replace all occurrences of 'target' with 'replacement' in 'orig' */
/* Returns a NEW allocated string that you must free. */
static char* str_replace(const char *orig, const char *target, const char *replacement) {
    char *result;
    int i, cnt = 0;
    int target_len = strlen(target);
    int replace_len = strlen(replacement);
    
    // Counting the number of times target occurs in the string
    for (i = 0; orig[i]; i++) {
        if (strstr(&orig[i], target) == &orig[i]) {
            cnt++;
            i += target_len - 1;
        }
    }
  
    // Allocating result string
    result = malloc(i + cnt * (replace_len - target_len) + 1);
    if (!result) return NULL;
  
    char *p = result;
    while (*orig) {
        if (strstr(orig, target) == orig) {
            strcpy(p, replacement);
            p += replace_len;
            orig += target_len;
        } else {
            *p++ = *orig++;
        }
    }
    *p = '\0';
    return result;
}

void write_template_to_file(const char *template_path, const char *output_path, TemplateData *data) {
    // 1. Read Template
    char *content = read_file_into_string(template_path);
    if (!content) {
        fprintf(stderr, "ERROR: Could not read template: %s\n", template_path);
        return;
    }

    // 2. Perform Replacements
    // We process the string repeatedly. This isn't the most efficient for huge files,
    // but for code generation, it's perfectly fine and simple.
    for (int i = 0; i < data->count; i++) {
        char *new_content = str_replace(content, data->replacements[i].key, data->replacements[i].value);
        free(content); // Free the old version
        content = new_content; // Move to the new version
    }

    // 3. Write Output
    FILE *f = fopen(output_path, "w");
    if (f) {
        fprintf(f, "%s", content);
        fclose(f);
    } else {
        perror("Failed to write output file");
    }

    free(content);
}