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
        PORT: "8080",
        SPRING_DATASOURCE_URL: "jdbc:postgresql://192.168.100.200:5433/elv?sslmode=disable",
        SPRING_DATASOURCE_USERNAME: "elvuser",
        SPRING_DATASOURCE_PASSWORD: "123456789123",
        GEMINI_API_KEY: "your_gemini_api_key_here"
      }
    }
  ]
};
