#ifndef FILE_UTIL_H
#define FILE_UTIL_H

#include <stddef.h>

void ensure_dir(const char *path);
void join_path(char *buf, size_t bufsize, const char *root, const char *sub);

#endif
