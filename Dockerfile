# Stage 1: Build the Spring Boot app
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the app
FROM openjdk:17-jdk-slim
WORKDIR /app

# ✅ Correct reference to the named build stage
COPY --from=builder /build/target/*.jar app.jar

# ✅ Set email credentials (not secure for public repos)
ENV SPRING_MAIL_USERNAME=raybruno679@gmail.com
ENV SPRING_MAIL_PASSWORD=ycxbwicudmsagblw

# Optional: Add other mail properties
ENV SPRING_MAIL_HOST=smtp.gmail.com
ENV SPRING_MAIL_PORT=587
ENV SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
ENV SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
ENV SPRING_MAIL_DEFAULT_ENCODING=UTF-8
ENV SPRING_MAIL_PROPERTIES_MAIL_SMTP_CONNECTIONTIMEOUT=5000
ENV SPRING_MAIL_PROPERTIES_MAIL_SMTP_TIMEOUT=5000
ENV SPRING_MAIL_PROPERTIES_MAIL_SMTP_WRITETIMEOUT=5000

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
