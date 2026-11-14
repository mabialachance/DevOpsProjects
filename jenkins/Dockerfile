FROM amazoncorretto:17-alpine-jdk25

EXPOSE 8080

COPY ./target/java-maven-app-*.jar /usr/app/
WORKDIR /usr/app

CMD java -jar java-maven-app-*.jar
