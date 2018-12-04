Base image for java 8
=
This image is a specialization of the base openjdk:8-jre-alpine image for running java application.
It includes _/usr/bin/curl_. 

The entrypoint_functions/jdk_8.sh initializes _JAVA_OPTS_ to recommended  values to run java in a container.
It also provides variables with useful values:
+ jvm_jacoco_config
+ jvm_jpda_config
+ secrets_path
+ configuration_path
