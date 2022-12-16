# syntax=docker/dockerfile:1.3

## Builder
FROM amazoncorretto:17-alpine3.15-jdk as builder

####################
## Label Manifest ##
####################
LABEL maintainer="Marthinus_Kruger"

#######################
## Project Resources ##
#######################
WORKDIR /app
COPY pom.xml .
COPY src src

##################
## Installation ##
##################
RUN set -x \
    && apk add --no-cache maven=3.8.3-r0

# hadolint ignore=DL3003,SC2164
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B \
    && mvn package \
    && mkdir -p target/dependency \
    && (cd target/dependency; jar -xf ../*.jar)

## Runtime
FROM amazoncorretto:17-alpine3.15

#######################
## Project Resources ##
#######################
USER root
WORKDIR /app
COPY --from=builder /app/target/TwitterFeedAssignment.jar /app

###################
## Configuration ##
###################
USER nobody
ENTRYPOINT ["java", "-jar", "TwitterFeedAssignment.jar"]