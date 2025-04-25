FROM eclipse-temurin:21-alpine

EXPOSE 8081

ADD build/libs/mc-auth.jar mc-auth.jar

ENTRYPOINT ["java", "-jar", "mc-auth.jar"]