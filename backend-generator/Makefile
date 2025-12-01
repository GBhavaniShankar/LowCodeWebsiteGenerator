# backend-generator/Makefile

# Compiler Settings
CC = gcc
CFLAGS = -Wall -Wextra -O2 -I./include -I./build

# Directories
SRC_DIR = src
INC_DIR = include
GRAMMAR_DIR = grammar
BUILD_DIR = build
BIN_DIR = bin

# Toolchain
LEX = flex
YACC = bison

# Output Binary Name
TARGET = $(BIN_DIR)/backendgen

# Source Files
SRCS = $(wildcard $(SRC_DIR)/*.c)
# Object Files
OBJS = $(patsubst $(SRC_DIR)/%.c, $(BUILD_DIR)/%.o, $(SRCS)) \
       $(BUILD_DIR)/config.tab.o \
       $(BUILD_DIR)/lex.yy.o

# --- RULES ---

all: directories $(TARGET)

# Link everything
$(TARGET): $(OBJS)
	@echo "[Backend] Linking..."
	$(CC) $(CFLAGS) -o $@ $^ -lfl

# Compile C files
$(BUILD_DIR)/%.o: $(SRC_DIR)/%.c
	$(CC) $(CFLAGS) -c $< -o $@

# Compile Bison (Parser)
$(BUILD_DIR)/config.tab.o: $(BUILD_DIR)/config.tab.c
	$(CC) $(CFLAGS) -c $< -o $@

# Compile Flex (Lexer)
$(BUILD_DIR)/lex.yy.o: $(BUILD_DIR)/lex.yy.c
	$(CC) $(CFLAGS) -c $< -o $@

# Generate C from Bison
$(BUILD_DIR)/config.tab.c $(BUILD_DIR)/config.tab.h: $(GRAMMAR_DIR)/config.y
	@echo "[Backend] Running Bison..."
	$(YACC) -d -o $(BUILD_DIR)/config.tab.c $<

# Generate C from Flex
$(BUILD_DIR)/lex.yy.c: $(GRAMMAR_DIR)/config.l $(BUILD_DIR)/config.tab.h
	@echo "[Backend] Running Flex..."
	$(LEX) -o $@ $<

directories:
	@mkdir -p $(BUILD_DIR) $(BIN_DIR)

clean:
	rm -rf $(BUILD_DIR) $(BIN_DIR)

.PHONY: all clean directories