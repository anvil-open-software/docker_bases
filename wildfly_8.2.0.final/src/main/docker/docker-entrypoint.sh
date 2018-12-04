#!/bin/sh
# Copyright 2018 Dematic, Corp.
# Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
set -e
. /opt/dlabs/entrypoint_functions/*

function liquibase() {
  if [ -z "$JDBC_URL" ]; then
		echo >&2 "error: JDBC_URL not defined, can't call liquibase"
		exit 1
  fi

  local command="$1"

  local classpath=""
  local path_separator=""
  for i in "$LIQUIBASE_HOME"/lib/*.jar; do
    classpath="$classpath$path_separator$i"
    path_separator=":"
  done

  local liquibase_command="$JAVA_HOME/bin/java $JAVA_OPTS -jar $LIQUIBASE_HOME/liquibase.jar"
  liquibase_command="$liquibase_command --classpath=$classpath"
  liquibase_command="$liquibase_command --username=$DB_USER --password=$DB_PASSWORD"
  liquibase_command="$liquibase_command --changeLogFile=liquibase/changelog.xml"
  liquibase_command="$liquibase_command --url=$JDBC_URL"
  liquibase_command="$liquibase_command --driver=$JDBC_DRIVER"

  $liquibase_command $command

}

if [ -n "$JDBC_URL" ]; then
    dependencies_waiter $JDBC_URL

    required_file_env "DB_PASSWORD" "DB_USER"
fi

while [ $# -gt 0 ]; do
    case $1 in
      "")
        # maven can set empty parameters...
        ;;
      liquibase-skip)
        ;;
      liquibase-update)
        liquibase update
        ;;
      liquibase-update-only)
        liquibase update
        exit 0
        ;;
      jpda)
        JAVA_OPTS="$JAVA_OPTS $jvm_jpda_config"
        ;;
      jacoco)
        JAVA_OPTS="$JAVA_OPTS $jvm_jacoco_config"
        ;;
      --)
        shift
        break
        ;;
      *)
        break
    esac
    shift
done

arguments="-b 0.0.0.0"

file_env "WILDFLY_ADMIN_USER" "WILDFLY_ADMIN_PASSWORD"

if [ "${WILDFLY_ADMIN_USER:+x}${WILDFLY_ADMIN_PASSWORD:+x}" = "xx" ]; then
    $JBOSS_HOME/bin/add-user.sh $WILDFLY_ADMIN_USER $WILDFLY_ADMIN_PASSWORD --silent
    arguments="$arguments -bmanagement 0.0.0.0"
fi

exec $JBOSS_HOME/bin/standalone.sh $arguments
