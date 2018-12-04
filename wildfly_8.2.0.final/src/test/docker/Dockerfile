# Copyright 2018 Dematic, Corp.
# Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
FROM us.gcr.io/dlabs-dev-primary/wildfly_8.2.0.final:${project.version}

COPY maven/mysql-connector-java.jar $LIQUIBASE_HOME/lib/

ENTRYPOINT ["/opt/dlabs/bin/docker-entrypoint.sh"]
