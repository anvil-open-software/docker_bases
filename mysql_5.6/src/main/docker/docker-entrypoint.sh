#!/bin/sh
# Copyright 2018 Dematic, Corp.
# Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
set -e

export MYSQL_ROOT_PASSWORD_FILE=/opt/dlabs/secrets/MYSQL_ROOT_PASSWORD
export MYSQL_USER_FILE=/opt/dlabs/secrets/DB_USER
export MYSQL_PASSWORD_FILE=/opt/dlabs/secrets/DB_PASSWORD
export MYSQL_DATABASE_FILE=/opt/dlabs/secrets/DB_NAME
exec /usr/local/bin/docker-entrypoint.sh mysqld
