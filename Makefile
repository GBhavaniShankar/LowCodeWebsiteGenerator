# ==========================================
# MASTER BUILD SYSTEM
# ==========================================

# --- CONFIGURATION: PATHS ---
# Where your C code lives
BACKEND_SRC_DIR = backend-generator
# Where your Node scripts & blueprints live
FRONTEND_SRC_DIR = frontend-generator

# Where we generate the final apps
OUTPUT_DIR = output
FRONTEND_OUT = $(OUTPUT_DIR)/frontend
BACKEND_OUT = $(OUTPUT_DIR)/backend

# --- TARGETS ---

all: clean build-generator generate-all install-frontend

# 1. Compile the C Generator
# We go into 'backend-generator' folder and run its internal makefile
build-generator:
	@echo "🔨 Building Backend Generator..."
	cd $(BACKEND_SRC_DIR) && make all
	# Move the binary to root so we can run it easily
	cp $(BACKEND_SRC_DIR)/bin/backendgen .

# 2. Run C Generator (Generates Java Backend + app-spec.json)
generate-all:
	@echo "🚀 Generating Backend & Frontend Spec..."
	mkdir -p $(OUTPUT_DIR)
	
	# Usage: ./backendgen <config_file> <output_dir>
	# Ensure you have 'test2.cfg' in your root, or update this path
	./backendgen test2.cfg $(OUTPUT_DIR)
	
	# Organize Output: Move Java code to specific backend folder
	mkdir -p $(BACKEND_OUT)
	mv $(OUTPUT_DIR)/src $(BACKEND_OUT)/src
	mv $(OUTPUT_DIR)/pom.xml $(BACKEND_OUT)/
	
	# Important: Move the generated JSON spec to root for the frontend script
	mv $(OUTPUT_DIR)/app-spec.json . 2>/dev/null || true

# 3. Setup Angular & Run Frontend Generator
install-frontend:
	@echo "🎨 Setting up Angular Frontend..."
	
	# A. Create Angular App (if not exists)
	if [ ! -d "$(FRONTEND_OUT)" ]; then \
		echo "Creating new Angular app..."; \
		npx -p @angular/cli ng new frontend --directory $(FRONTEND_OUT) --standalone --routing --style=css --skip-install; \
	fi

	# B. Inject Static Shell (from frontend-generator/blueprints/shell)
	@echo "📦 Injecting Shell Blueprints..."
	cp -r $(FRONTEND_SRC_DIR)/blueprints/shell/* $(FRONTEND_OUT)/src/app/

	# C. Install Dependencies
	cd $(FRONTEND_OUT) && npm install

	# D. Run Dynamic Generator (from frontend-generator/scripts)
	@echo "⚡ Generating Dynamic Pages..."
	node $(FRONTEND_SRC_DIR)/scripts/generate.js app-spec.json

	@echo "✅ DONE! Frontend is in: $(FRONTEND_OUT)"

# 4. Run Tests (Optional)
TEST_DIR = tests
test:
	@echo "🧪 Running Integration Tests..."
	pip install -r $(TEST_DIR)/requirements.txt || true
	python $(TEST_DIR)/complex_api_tester.py

clean:
	@echo "🧹 Cleaning up..."
	rm -rf $(OUTPUT_DIR) backendgen app-spec.json
	# Clean the C build artifacts too
	cd $(BACKEND_SRC_DIR) && make clean
