#!/bin/bash

# ==============================================================================
# SURVEILLANCE BOQ ENGINE - VPS DEPLOYMENT & ECOSYSTEM MANAGER
# ==============================================================================
# This script automates the installation, configuration, and execution of the
# Surveillance BOQ Engine on a Linux VPS. It supports both Docker-based and PM2-based
# deployment systems.
# ==============================================================================

# Colors for terminal output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Helper functions
log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Print Banner
echo -e "${CYAN}"
echo "======================================================================"
echo "    __  ___  ____    _    _   _   ____   _____  ____  _   _  _____    "
echo "   /  |/  / / __ \  | |  / / | | |  _ \ / ___/ / ___/| | | ||_   _|   "
echo "  / /|_/ / / / / /  | | / /  | | | |_) )\___ \ \___ \| | | |  | |     "
echo " / /  / / / /_/ /   | |/ /   | | |  __/ ____) )____) ) |_| |  | |     "
echo "/_/  /_/  \____/    |___/    |_| |_|   /____/ /____/  \___/   |_|     "
echo "======================================================================"
echo "             VPS Setup, Config & Deployment Ecosystem Manager          "
echo "======================================================================"
echo -e "${NC}"

# Check OS (Must be run on Linux VPS)
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    log_warn "This script is designed for a Linux VPS but running on Windows environment."
fi

# Configuration File Path
ENV_FILE=".env"
DOCKER_COMPOSE_FILE="docker-compose.yml"
PM2_CONFIG_FILE="ecosystem.config.js"

# Create default .env file if it doesn't exist
init_env() {
    if [ ! -f "$ENV_FILE" ]; then
        log_info "Creating default environment configuration file (.env)..."
        cat <<EOF > "$ENV_FILE"
# --- Database Configuration ---
DB_HOST=192.168.100.200
DB_PORT=5433
POSTGRES_DB=elv
POSTGRES_USER=elvuser
POSTGRES_PASSWORD=123456789123

# --- Application Configuration ---
APP_PORT=8080
GEMINI_API_KEY=your_gemini_api_key_here
APP_URL=https://boq.thiensu.com.vn

# --- VPS Domain & SSL Configuration (For PM2/Nginx/Certbot) ---
DOMAIN_NAME=boq.thiensu.com.vn
SSL_EMAIL=admin@thiensu.com.vn
EOF
        log_success "Created .env with default values."
        log_warn "Please open '.env' and set your real 'GEMINI_API_KEY' and configuration variables."
    else
        log_info "Environment configuration file (.env) already exists."
    fi
}

# Install VPS System Dependencies (Ubuntu/Debian)
install_dependencies() {
    log_info "Detecting system package manager and updating packages..."
    if [ -f /etc/debian_version ]; then
        sudo apt-get update -y
        
        # Install general packages
        log_info "Installing basic utility packages..."
        sudo apt-get install -y curl git wget build-essential gnupg lsb-release ca-certificates
        
        # Install Node.js (v20)
        if ! command -v node &> /dev/null; then
            log_info "Installing Node.js v20..."
            curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
            sudo apt-get install -y nodejs
            log_success "Node.js $(node -v) installed."
        else
            log_info "Node.js already installed: $(node -v)"
        fi

        # Install PM2
        if ! command -v pm2 &> /dev/null; then
            log_info "Installing PM2 globally..."
            sudo npm install -g pm2
            log_success "PM2 installed successfully."
        else
            log_info "PM2 already installed: $(pm2 -v)"
        fi

        # Install Docker
        if ! command -v docker &> /dev/null; then
            log_info "Installing Docker..."
            sudo mkdir -p /etc/apt/keyrings
            curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
            echo \
              "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
              $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
            sudo apt-get update -y
            sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
            log_success "Docker installed successfully."
        else
            log_info "Docker already installed: $(docker --version)"
        fi

        # Install Java 17 and Maven (For PM2 deployment mode)
        if ! command -v java &> /dev/null; then
            log_info "Installing OpenJDK 17..."
            sudo apt-get install -y openjdk-17-jdk openjdk-17-jre
            log_success "Java installed: $(java -version 2>&1 | head -n 1)"
        else
            log_info "Java already installed: $(java -version 2>&1 | head -n 1)"
        fi

        if ! command -v mvn &> /dev/null; then
            log_info "Installing Maven..."
            sudo apt-get install -y maven
            log_success "Maven installed: $(mvn -version | head -n 1)"
        else
            log_info "Maven already installed."
        fi

        # Install PostgreSQL locally (Only needed for PM2 mode, Docker Compose uses Docker container)
        if ! command -v psql &> /dev/null; then
            log_info "Installing PostgreSQL Client..."
            sudo apt-get install -y postgresql-client
        fi

        # Install Nginx
        if ! command -v nginx &> /dev/null; then
            log_info "Installing Nginx..."
            sudo apt-get install -y nginx
            sudo systemctl enable nginx
            sudo systemctl start nginx
            log_success "Nginx installed and started."
        else
            log_info "Nginx already installed."
        fi
    else
        log_error "Unsupported OS. This install script only supports Debian/Ubuntu based systems."
        exit 1
    fi
}

# Sync configurations from .env to active deployment files
sync_configs() {
    if [ ! -f "$ENV_FILE" ]; then
        init_env
    fi

    # Load environment variables
    export $(grep -v '^#' "$ENV_FILE" | xargs)

    log_info "Syncing configurations to docker-compose.yml..."
    
    # Update docker-compose.yml values from .env
    if [ -f "$DOCKER_COMPOSE_FILE" ]; then
        sed -i.bak -E "s/POSTGRES_DB: .*/POSTGRES_DB: ${POSTGRES_DB}/" "$DOCKER_COMPOSE_FILE"
        sed -i.bak -E "s/POSTGRES_USER: .*/POSTGRES_USER: ${POSTGRES_USER}/" "$DOCKER_COMPOSE_FILE"
        sed -i.bak -E "s/POSTGRES_PASSWORD: .*/POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}/" "$DOCKER_COMPOSE_FILE"
        sed -i.bak -E "s/SPRING_DATASOURCE_USERNAME=.*/SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}/" "$DOCKER_COMPOSE_FILE"
        sed -i.bak -E "s/SPRING_DATASOURCE_PASSWORD=.*/SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}/" "$DOCKER_COMPOSE_FILE"
        sed -i.bak -E "s/\"5432:5432\"/\"${DB_PORT}:5432\"/" "$DOCKER_COMPOSE_FILE"
        sed -i.bak -E "s/\"8080:8080\"/\"${APP_PORT}:8080\"/" "$DOCKER_COMPOSE_FILE"
        rm -f "${DOCKER_COMPOSE_FILE}.bak"
        log_success "Synced config variables to $DOCKER_COMPOSE_FILE"
    else
        log_error "docker-compose.yml not found in current directory."
    fi

    # Sync to frontend environment files
    log_info "Configuring frontend environmental variables..."
    echo "VITE_API_URL=${APP_URL}" > frontend/.env.production
    echo "GEMINI_API_KEY=${GEMINI_API_KEY}" >> frontend/.env.production
    echo "APP_URL=${APP_URL}" >> frontend/.env.production
    log_success "Synced config variables to frontend/.env.production"
}

# --- Docker Deployment Path ---
deploy_docker() {
    log_info "Starting Docker-based deployment ecosystem..."
    sync_configs
    
    log_info "Building and running containers using Docker Compose..."
    sudo docker compose up -d --build
    
    log_success "Docker ecosystem deployed successfully!"
    sudo docker compose ps
    log_info "You can view logs via: docker compose logs -f"
}

# --- PM2 Local Deployment Path ---
deploy_pm2() {
    log_info "Starting PM2-based deployment ecosystem..."
    sync_configs

    # 1. Build Frontend
    log_info "Building frontend..."
    cd frontend || exit 1
    npm install
    npm run build
    cd ..

    # 2. Build Backend
    log_info "Building backend..."
    cd backend || exit 1
    # Create directory and copy frontend static assets
    mkdir -p src/main/resources/static
    cp -r ../frontend/dist/* src/main/resources/static/
    mvn clean package -DskipTests
    cd ..

    # 3. Check for ecosystem file
    if [ ! -f "$PM2_CONFIG_FILE" ]; then
        log_info "Creating PM2 ecosystem configuration..."
        cat <<EOF > "$PM2_CONFIG_FILE"
module.exports = {
  apps: [
    {
      name: "boq-backend",
      script: "java",
      args: "-jar backend/target/elv-0.0.1-SNAPSHOT.jar",
      cwd: "./",
      instances: 1,
      autorestart: true,
      watch: false,
      max_memory_restart: "1G",
      env: {
        PORT: "${APP_PORT}",
        SPRING_DATASOURCE_URL: "jdbc:postgresql://${DB_HOST}:${DB_PORT}/${POSTGRES_DB}?sslmode=disable",
        SPRING_DATASOURCE_USERNAME: "${POSTGRES_USER}",
        SPRING_DATASOURCE_PASSWORD: "${POSTGRES_PASSWORD}",
        GEMINI_API_KEY: "${GEMINI_API_KEY}"
      }
    }
  ]
};
EOF
    fi

    log_info "Starting PM2 processes..."
    pm2 start "$PM2_CONFIG_FILE"
    pm2 save
    
    log_success "PM2 ecosystem started successfully!"
    pm2 list
}

# --- Systemd Service Deployment Path (Production Best Practice) ---
deploy_systemd() {
    log_info "Starting Systemd-based deployment ecosystem..."
    sync_configs

    # 1. Build Frontend
    log_info "Building frontend..."
    cd frontend || exit 1
    npm install
    npm run build
    cd ..

    # 2. Build Backend
    log_info "Building backend..."
    cd backend || exit 1
    mkdir -p src/main/resources/static
    cp -r ../frontend/dist/* src/main/resources/static/
    mvn clean package -DskipTests
    cd ..

    # 3. Create Deploy Directory and Copy Jar
    DEPLOY_DIR="/var/www/boq-backend"
    log_info "Setting up deploy directory at $DEPLOY_DIR..."
    sudo mkdir -p "$DEPLOY_DIR"
    sudo cp backend/target/elv-0.0.1-SNAPSHOT.jar "$DEPLOY_DIR/app.jar"
    sudo chown -R $USER:$USER "$DEPLOY_DIR"

    # 4. Create Systemd Service File
    SERVICE_FILE="/etc/systemd/system/boq.service"
    log_info "Generating systemd service file at $SERVICE_FILE..."
    
    sudo bash -c "cat <<EOF > $SERVICE_FILE
[Unit]
Description=Surveillance BOQ Engine Service
After=syslog.target network.target

[Service]
User=$USER
WorkingDirectory=$DEPLOY_DIR
ExecStart=/usr/bin/java -jar app.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

Environment=PORT=${APP_PORT}
Environment=SPRING_DATASOURCE_URL=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${POSTGRES_DB}?sslmode=disable
Environment=SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
Environment=SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
Environment=GEMINI_API_KEY=${GEMINI_API_KEY}

[Install]
WantedBy=multi-user.target
EOF"

    log_info "Reloading systemd daemon..."
    sudo systemctl daemon-reload
    log_info "Enabling boq service..."
    sudo systemctl enable boq.service
    log_info "Starting boq service..."
    sudo systemctl restart boq.service

    log_success "Systemd ecosystem deployed successfully!"
    sudo systemctl status boq.service --no-pager | head -n 15
}

# --- Nginx Setup for Reverse Proxy & Domain ---
setup_nginx() {
    if [ ! -f "$ENV_FILE" ]; then
        init_env
    fi
    export $(grep -v '^#' "$ENV_FILE" | xargs)

    log_info "Setting up Nginx reverse proxy configuration for domain ${DOMAIN_NAME}..."
    
    NGINX_CONF="/etc/nginx/sites-available/boq"
    
    sudo bash -c "cat <<EOF > $NGINX_CONF
server {
    listen 80;
    server_name ${DOMAIN_NAME} www.${DOMAIN_NAME};

    location / {
        proxy_pass http://127.0.0.1:${APP_PORT};
        proxy_http_version 1.1;
        proxy_set_header Upgrade \\\$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \\\$host;
        proxy_cache_bypass \\\$http_upgrade;
        proxy_set_header X-Real-IP \\\$remote_addr;
        proxy_set_header X-Forwarded-For \\\$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \\\$scheme;
    }
}
EOF"

    sudo ln -sf "$NGINX_CONF" /etc/nginx/sites-enabled/
    sudo rm -f /etc/nginx/sites-enabled/default
    
    log_info "Testing Nginx configuration..."
    if sudo nginx -t; then
        sudo systemctl restart nginx
        log_success "Nginx reverse proxy is active."
        
        # Optional: Ask to setup Certbot SSL
        echo -e "${YELLOW}Do you want to configure Let's Encrypt SSL (HTTPS) now? (y/n)${NC}"
        read -r ssl_choice
        if [[ "$ssl_choice" =~ ^[Yy]$ ]]; then
            log_info "Installing certbot..."
            sudo apt-get install -y certbot python3-certbot-nginx
            sudo certbot --nginx -d "${DOMAIN_NAME}" -d "www.${DOMAIN_NAME}" --non-interactive --agree-tos -m "${SSL_EMAIL}" --redirect
            log_success "SSL certificates generated and activated automatically!"
        fi
    else
        log_error "Nginx configuration verification failed. Reverting changes."
    fi
}

# Print Command Options
usage() {
    echo -e "Usage: $0 [command]"
    echo -e "Commands:"
    echo -e "  ${GREEN}init${NC}          Initialize local config file (.env)"
    echo -e "  ${GREEN}install${NC}       Install system dependencies on VPS (Ubuntu/Debian)"
    echo -e "  ${GREEN}docker${NC}        Deploy ecosystem using Docker Compose (Recommended)"
    echo -e "  ${GREEN}systemd${NC}       Deploy ecosystem using Systemd Service (Production Recommended)"
    echo -e "  ${GREEN}pm2${NC}           Deploy ecosystem locally using PM2 (Alternative Process Manager)"
    echo -e "  ${GREEN}nginx${NC}         Configure Nginx Reverse Proxy with HTTPS SSL"
    echo -e "  ${GREEN}status${NC}        Check status of running applications (Docker, PM2 & Systemd)"
    echo -e "  ${GREEN}stop${NC}          Stop all running systems (Docker, PM2 & Systemd)"
    echo -e ""
    exit 1
}

# Main Execution Routing
case "$1" in
    init)
        init_env
        ;;
    install)
        install_dependencies
        ;;
    docker)
        deploy_docker
        ;;
    systemd)
        deploy_systemd
        ;;
    pm2)
        deploy_pm2
        ;;
    nginx)
        setup_nginx
        ;;
    status)
        echo "=== Docker Containers ==="
        sudo docker compose ps 2>/dev/null || echo "Docker compose is not running here."
        echo "=== PM2 Applications ==="
        pm2 list 2>/dev/null || echo "PM2 is not running."
        echo "=== Systemd Services ==="
        sudo systemctl status boq.service 2>/dev/null --no-pager | head -n 15 || echo "Systemd service 'boq' not active or not installed."
        ;;
    stop)
        log_info "Stopping Docker containers..."
        sudo docker compose down 2>/dev/null || true
        log_info "Stopping PM2 apps..."
        pm2 stop all 2>/dev/null || true
        log_info "Stopping Systemd service..."
        sudo systemctl stop boq.service 2>/dev/null || true
        log_success "All systems stopped."
        ;;
    *)
        usage
        ;;
esac
