FROM amazoncorretto:17-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", \
  "-Xms256m", \
  "-Xmx384m", \
  "-XX:+HeapDumpOnOutOfMemoryError", \
  "-XX:HeapDumpPath=/tmp/heapdump.hprof", \
  "-Dspring.profiles.active=prod", \
  "-jar", \
  "/app.jar"]
