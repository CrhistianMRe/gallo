FROM eclipse-temurin:17-jre
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring
WORKDIR /app
ARG JAR_FILE
COPY target/${JAR_FILE} /app/
ENTRYPOINT ["sh", "-c", "java -jar /app/*.jar"]
