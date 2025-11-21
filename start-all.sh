#!/bin/bash

echo "🚀 Starting Social Media Platform..."

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# Build all services
echo "📦 Building services..."
docker-compose build --parallel

# Start infrastructure first
echo "🏗️ Starting infrastructure services..."
docker-compose up -d mongodb redis zookeeper kafka

# Wait for infrastructure
echo "⏳ Waiting for infrastructure to be ready..."
sleep 20

# Start Eureka
echo "🔍 Starting Eureka Server..."
docker-compose up -d eureka-server
sleep 30

# Start all other services
echo "🚀 Starting all microservices..."
docker-compose up -d

echo "✅ All services started!"
echo "📊 Eureka Dashboard: http://localhost:8761"
echo "🌐 API Gateway: http://localhost:8080"
echo "📈 Grafana: http://localhost:3001 (admin/admin123)"