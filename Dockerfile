FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY src ./src
COPY lib ./lib

RUN mkdir -p out
RUN javac -cp "lib/mysql-connector-j-26.7.0.jar" -d out src/*.java

CMD ["java", "-cp", "out:lib/mysql-connector-j-26.7.0.jar", "Main"]
