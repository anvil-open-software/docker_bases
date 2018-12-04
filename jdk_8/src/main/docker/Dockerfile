# Copyright 2018 Dematic, Corp.
# Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
FROM openjdk:8-jdk-alpine

RUN apk add --no-cache curl

COPY maven/jacocoagent.jar /opt/dlabs/
COPY entrypoint_functions /opt/dlabs/entrypoint_functions/
