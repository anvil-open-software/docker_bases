#!/bin/sh
# Copyright 2018 Dematic, Corp.
# Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
#
# generic helpers functions for use by any images
# Most of these helpers are work-around for applications that are not cloud-compliant in the 12 factors application (12FA) sense.

# Recommended JAVA_OPTS, sets only if not already defined
if [ -z "${JAVA_OPTS+x}" ]; then
    export JAVA_OPTS="-showversion -XshowSettings:vm -XX:NativeMemoryTracking=summary"
    JAVA_OPTS="$JAVA_OPTS -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1"
fi

# Use these jvm option to hook up to jacoco
readonly jvm_jacoco_config="-javaagent:/opt/dlabs/jacocoagent.jar=output=tcpserver,address=*"
# Use these jvm option to hook up to jpda
readonly jvm_jpda_config="-agentlib:jdwp=transport=dt_socket,address=55159,suspend=n,server=y"
# Mount-points for secrets.
readonly secrets_path="/opt/dlabs/secrets/"
# Mount-points for configuration.
readonly configuration_path="/opt/dlabs/conf/"

# reads one or more secrets from a file mount.
# Use of that method is a work-around for a deficient application.
# a 12FA application should access the secret file directly.
file_env() {
    for var in "$@"; do
        [[ -r "${secrets_path}${var}" ]] || continue
        export ${var}=`cat "${secrets_path}${var}"`
	done
}

# reads one or more secrets from a file mount and fails if the secret is not defined.
# Use of that method is a work-around for a deficient application.
# a 12FA application should access the secret file directly.
required_file_env() {
	for var in "$@"; do
        [[ -r "${secrets_path}${var}" ]] || { echo >&2 "error: secret file not readable for $var"; exit 1; }
        export ${var}=`cat "${secrets_path}${var}"`
	done
}

# waits for one or more services to become healthy
# Use of that method is a work-around for a deficient application.
# a 12FA application should handle services becoming unavailable (#CircuitBreaker)
dependencies_waiter() {
    [[ "${WAITER_OPTS#*--interval}" != "$WAITER_OPTS" ]]   || WAITER_OPTS="$WAITER_OPTS --interval 5"
    [[ "${WAITER_OPTS#*--retries}" != "$WAITER_OPTS" ]]    || WAITER_OPTS="$WAITER_OPTS --retries 20"
    [[ "${WAITER_OPTS#*--secretPath}" != "$WAITER_OPTS" ]] || WAITER_OPTS="$WAITER_OPTS --secretPath ${secrets_path}"
    [[ "${WAITER_OPTS#*--timeout}" != "$WAITER_OPTS" ]]    || WAITER_OPTS="$WAITER_OPTS --timeout 3"
    $JAVA_HOME/bin/java -cp $LIQUIBASE_HOME/dependencies_waiter.jar:$LIQUIBASE_HOME/lib/* com.dematic.labs.docker_bases.dependencies_waiter.Main ${WAITER_OPTS:-} "$@"
}
