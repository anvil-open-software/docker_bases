# Copyright 2018 Dematic, Corp.
# Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
FROM us.gcr.io/dlabs-dev-primary/jdk_8:${project.version}

ENV LIQUIBASE_HOME /opt/jboss/liquibase/

COPY maven/mysql-connector-java.jar $LIQUIBASE_HOME/lib/
COPY maven/dependencies_waiter.jar $LIQUIBASE_HOME
COPY docker-entrypoint.sh /opt/dlabs/bin/

ENTRYPOINT ["/opt/dlabs/bin/docker-entrypoint.sh"]
