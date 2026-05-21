FROM eclipse-temurin:21-jre

WORKDIR /app

ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.7.8/applicationinsights-agent-3.7.8.jar /app/applicationinsights-agent.jar

COPY target/azure-jvm-observatory-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:/app/applicationinsights-agent.jar", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]