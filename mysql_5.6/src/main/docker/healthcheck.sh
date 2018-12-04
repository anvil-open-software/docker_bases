#!/bin/sh
# Copyright 2018 Dematic, Corp.
# Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
/usr/bin/mysqladmin -u$MYSQL_USER -p$MYSQL_PASSWORD -h127.0.0.1 ping
