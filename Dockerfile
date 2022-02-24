FROM openjdk:8-jdk-alpine
RUN mkdir -p /data /app
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/app.jar
WORKDIR /app
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar ${0} ${@}"]