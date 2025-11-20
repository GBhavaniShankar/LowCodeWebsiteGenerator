#include "file_util.h"

#include <stdio.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>

void ensure_dir(const char *path)
{
#ifdef _WIN32
    _mkdir(path);
#else
    mkdir(path, 0777);
#endif
}

void join_path(char *buf, size_t bufsize, const char *root, const char *sub)
{
    snprintf(buf, bufsize, "%s/%s", root, sub);
}
