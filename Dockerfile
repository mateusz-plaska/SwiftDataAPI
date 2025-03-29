FROM docker.io/library/openjdk:21
WORKDIR /app
COPY target/swift-data.jar swift-data.jar
COPY target/classes/2025_SWIFT_CODES.csv /app/data/2025_SWIFT_CODES.csv
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "swift-data.jar"]