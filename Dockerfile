# Stage 1: Build React Frontend
FROM node:20-alpine AS frontend-builder
WORKDIR /frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Stage 2: Build Spring Boot Backend with built Frontend
FROM maven:3.9.6-eclipse-temurin-17 AS backend-builder
WORKDIR /backend
COPY backend/pom.xml ./
RUN mvn dependency:go-offline
COPY backend/src ./src
# Create static resources directory and copy frontend build output
RUN mkdir -p src/main/resources/static
COPY --from=frontend-builder /frontend/dist ./src/main/resources/static/
RUN mvn clean package -DskipTests

# Stage 3: Package final JRE image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=backend-builder /backend/target/elv-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
