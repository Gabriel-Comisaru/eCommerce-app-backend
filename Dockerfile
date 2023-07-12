FROM openjdk:17-oracle  AS BUILD_IMAGE
ENV APP_HOME=/root/dev/main-project-backend
RUN microdnf install findutils
RUN mkdir -p $APP_HOME/src/main/java
WORKDIR $APP_HOME
# Copy all the files
COPY ./build.gradle ./gradlew ./gradlew.bat $APP_HOME
COPY gradle $APP_HOME/gradle

RUN sed -i -e 's/\r$//' ./gradlew
RUN ./gradlew --version

COPY ./src $APP_HOME/src/
# Build desirable JAR
RUN ./gradlew clean build -x test

FROM openjdk:17-oracle 
WORKDIR /root/
COPY --from=BUILD_IMAGE '/root/dev/main-project-backend/build/libs/main-project-backend-0.0.1-SNAPSHOT.jar' '/app/store.jar'

EXPOSE 8080
CMD ["java","-jar","/app/store.jar"]