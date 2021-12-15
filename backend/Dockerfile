FROM openjdk:17-jdk as build
WORKDIR /app
COPY gradlew ./
COPY settings.gradle.kts ./
COPY build.gradle.kts ./
COPY gradle/wrapper/gradle-wrapper.properties ./gradle/wrapper/
COPY gradle/wrapper/gradle-wrapper.jar ./gradle/wrapper/
RUN ./gradlew wrapper
COPY src ./src
RUN ./gradlew installDist

FROM openjdk:17-jdk
EXPOSE 8080
WORKDIR /app
RUN adduser -s /bin/bash user \
 && chown -R user:user /app
USER user
COPY --from=build /app/build/install /app
CMD ["com.beanpedia.beanpedia/bin/com.beanpedia.beanpedia"]
