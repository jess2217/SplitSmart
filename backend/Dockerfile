FROM maven:3.9-eclipse-temurin-25

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

EXPOSE 8081

CMD ["java", "-jar", "target/split-smart-1.0-SNAPSHOT.jar"]