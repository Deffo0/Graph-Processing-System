FROM amazoncorretto:8-alpine3.19-jre

WORKDIR /app
COPY ./target/Server.jar /app.jar

RUN   mkdir -p src/main/resources

COPY ./target/classes/system.properties /app/src/main/resources/system.properties
COPY ./target/classes/graph.txt /app/src/main/resources/graph.txt
# COPY entrypoint.sh /app/entrypoint.sh

# RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["java", "-cp", "/app.jar", "org.example.server.GSPServer"]

