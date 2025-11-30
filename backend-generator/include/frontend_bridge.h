#ifndef FRONTEND_BRIDGE_H
#define FRONTEND_BRIDGE_H

#include "config.h"

// Generates the app-spec.json file needed by the Frontend Generator
void generate_frontend_json(const AppConfig *cfg, const char *root);

#endif