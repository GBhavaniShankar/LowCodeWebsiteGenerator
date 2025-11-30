# ==========================================
# C BACKEND GENERATOR MAKEFILE
# ==========================================

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
# 1. Standard C sources in /src
SRCS = $(wildcard $(SRC_DIR)/*.c)
# 2. Generated C sources (from Flex/Bison)
GEN_SRCS = $(BUILD_DIR)/config.tab.c $(BUILD_DIR)/lex.yy.c

# Object Files
OBJS = $(patsubst $(SRC_DIR)/%.c, $(BUILD_DIR)/%.o, $(SRCS)) \
       $(BUILD_DIR)/config.tab.o \
       $(BUILD_DIR)/lex.yy.o

# ==========================================
# RULES
# ==========================================

all: directories $(TARGET)

# 1. Link everything into the final executable
$(TARGET): $(OBJS)
	@echo "🔨 Linking..."
	$(CC) $(CFLAGS) -o $@ $^ -lfl
	@echo "✅ Build Complete: $(TARGET)"

# 2. Compile standard C files
$(BUILD_DIR)/%.o: $(SRC_DIR)/%.c
	@echo "CC $<"
	$(CC) $(CFLAGS) -c $< -o $@

# 3. Compile the Parser (Bison output)
$(BUILD_DIR)/config.tab.o: $(BUILD_DIR)/config.tab.c
	$(CC) $(CFLAGS) -c $< -o $@

# 4. Compile the Lexer (Flex output)
$(BUILD_DIR)/lex.yy.o: $(BUILD_DIR)/lex.yy.c
	$(CC) $(CFLAGS) -c $< -o $@

# 5. Generate C code from Bison (.y)
$(BUILD_DIR)/config.tab.c $(BUILD_DIR)/config.tab.h: $(GRAMMAR_DIR)/config.y
	@echo "YACC $<"
	$(YACC) -d -o $(BUILD_DIR)/config.tab.c $<

# 6. Generate C code from Flex (.l)
$(BUILD_DIR)/lex.yy.c: $(GRAMMAR_DIR)/config.l $(BUILD_DIR)/config.tab.h
	@echo "LEX $<"
	$(LEX) -o $@ $<


# ... (Previous variables) ...
TEST_DIR = tests
PYTHON_REQ = $(TEST_DIR)/requirements.txt

# ... (Previous targets: all, build-generator, etc.) ...

# 4. Run Automated Tests
test:
	@echo "🧪 Preparing Test Environment..."
	pip install -r $(PYTHON_REQ)
	
	@echo "🔍 Running AgileTracker Integration Tests..."
	# We use 'complex_api_tester.py' because it matches the Team/Sprint/Ticket schema
	python $(TEST_DIR)/complex_api_tester.py
	
	@echo "🛡️ Running Security & Isolation Tests..."
	python $(TEST_DIR)/complex_test2.py

# Optional: A target to test SMTP specifically if needed
test-mail:
	python $(TEST_DIR)/test.py

# 7. Create needed directories (build/ and bin/)
directories:
	@mkdir -p $(BUILD_DIR) $(BIN_DIR)

clean:
	@echo "🧹 Cleaning..."
	rm -rf $(BUILD_DIR) $(BIN_DIR)

.PHONY: all clean directories