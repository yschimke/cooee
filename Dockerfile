FROM gcr.io/distroless/java17:nonroot
WORKDIR /app
#COPY . .
#RUN ./gradlew --no-daemon build
COPY build/libs/cooee-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
