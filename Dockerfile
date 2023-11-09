FROM adoptopenjdk:11-jre-hotspot

WORKDIR /usr/src/app

COPY target/truck-company-0.0.1-SNAPSHOT.jar /usr/src/app/

CMD ["java", "-jar", "truck-company-0.0.1-SNAPSHOT.jar"]