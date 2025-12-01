# ==========================================
# MASTER BUILD SYSTEM
# ==========================================

# --- CONFIGURATION: PATHS ---
BACKEND_SRC_DIR = backend-generator
FRONTEND_SRC_DIR = frontend-generator
OUTPUT_DIR = output

# --- CONFIGURATION: INPUTS ---
# Point to your new config folder here
CONFIG_FILE = configs/test2.cfg

# --- OUTPUT PATHS ---
FRONTEND_OUT = $(OUTPUT_DIR)/frontend
BACKEND_OUT = $(OUTPUT_DIR)/backend

# --- TARGETS ---

all: clean build-generator generate-all install-frontend

# 1. Compile the C Generator
build-generator:
	@echo "Building Backend Generator..."
	cd $(BACKEND_SRC_DIR) && make all
	cp $(BACKEND_SRC_DIR)/bin/backendgen .

# 2. Run C Generator
generate-all: build-generator
	@echo "Generating Backend & Frontend Spec..."
	mkdir -p $(OUTPUT_DIR)
	
	# Pass the config file variable to the generator
	backend-generator/bin/backendgen $(CONFIG_FILE) $(OUTPUT_DIR)
	
	# Organize Output
	mkdir -p $(BACKEND_OUT)
	mv $(OUTPUT_DIR)/src $(BACKEND_OUT)/src
	mv $(OUTPUT_DIR)/pom.xml $(BACKEND_OUT)/
	
	# Move the generated JSON spec to root for the frontend script
	mv $(OUTPUT_DIR)/app-spec.json . 2>/dev/null || true

generate-backend:
	@echo "Generating Backend..."
	mkdir -p $(OUTPUT_DIR)
	
	# Pass the config file variable to the generator
	# ./backendgen $(CONFIG_FILE) $(OUTPUT_DIR)

	backend-generator/bin/backendgen $(CONFIG_FILE) $(OUTPUT_DIR)

	# Organize Output
	mkdir -p $(BACKEND_OUT)
	mv $(OUTPUT_DIR)/src $(BACKEND_OUT)/src
	mv $(OUTPUT_DIR)/pom.xml $(BACKEND_OUT)/
	
	# Move the generated JSON spec to root for the frontend script
	mv $(OUTPUT_DIR)/app-spec.json . 2>/dev/null || true

install-frontend:
	@echo "Setting up Angular Frontend..."
	
	if [ ! -d "$(FRONTEND_OUT)" ]; then \
		echo "Creating new Angular app..."; \
		npx -p @angular/cli@17 ng new frontend --directory $(FRONTEND_OUT) --standalone --routing --style=css --skip-install; \
	fi

	@echo "Injecting Shell Blueprints..."
	cp -r $(FRONTEND_SRC_DIR)/blueprints/shell/* $(FRONTEND_OUT)/src/app/

	mv $(FRONTEND_OUT)/src/app/styles.css $(FRONTEND_OUT)/src/styles.css

	# --- FIX: Inject Environment Variables ---
	@echo "Injecting Environment..."
	mkdir -p $(FRONTEND_OUT)/src/environments
	cp -r $(FRONTEND_SRC_DIR)/blueprints/environments/* $(FRONTEND_OUT)/src/environments/
	# -----------------------------------------

	cd $(FRONTEND_OUT) && npm install

	@echo "Generating Dynamic Pages..."
	# Pass the output path explicitly
	node $(FRONTEND_SRC_DIR)/scripts/generate.js app-spec.json $(FRONTEND_OUT)/src/app/features/generated

	@echo "DONE! Frontend is in: $(FRONTEND_OUT)"


# 4. Run Tests
TEST_DIR = backend-generator/tests
test:
	@echo "Running Integration Tests..."
	pip install -r $(TEST_DIR)/requirements.txt || true
	# We assume the config used (test2.cfg) matches this test script
	python3 $(TEST_DIR)/complex_api_tester.py

clean:
	@echo "Cleaning up..."
	rm -rf $(OUTPUT_DIR) backendgen app-spec.json
	cd $(BACKEND_SRC_DIR) && make clean
	rm -rf $(FRONTEND_OUT)/src/app/features/generated/*

app: build-generator generate-backend install-frontend

rebuild: clean app
	@echo "Rebuild complete."

run-frontend:
	@echo "Running Frontend..."
	cd $(FRONTEND_OUT) && ng serve

run-backend:
	@echo "Running Backend..."
	cd $(BACKEND_OUT) && mvn spring-boot:run