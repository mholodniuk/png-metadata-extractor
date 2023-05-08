FROM bellsoft/liberica-openjdk-alpine-musl:17

WORKDIR /app
COPY target/image-processing-api-0.0.1-SNAPSHOT.jar image-processing-api.jar
CMD ["java", "-jar", "image-processing-api.jar"]

# docker build --platform=linux/x86_64 -t spring-api .
# docker run -p 8080:8080 spring-api