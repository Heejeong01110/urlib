FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=prod", "-Dfile.encoding=UTF-8", "-Duser.language=ko", "-Duser.country=KR", "-jar", "/app.jar"]
