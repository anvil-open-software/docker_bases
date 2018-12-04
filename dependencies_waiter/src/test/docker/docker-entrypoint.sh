#!/bin/sh
# Copyright 2018 Dematic, Corp.
# Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
set -e

exec $JAVA_HOME/bin/java -cp $LIQUIBASE_HOME/dependencies_waiter.jar:$LIQUIBASE_HOME/lib/* com.dematic.labs.docker_bases.dependencies_waiter.Main "$@"
