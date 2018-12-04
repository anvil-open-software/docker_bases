# Copyright 2018 Dematic, Corp.
# Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
FROM us.gcr.io/dlabs-dev-primary/jdk_8:${project.version}

# 8080: wildfly
# 9990: wildfly admin
# 6300: jacoco
# 55159: jpda
EXPOSE 8080 9990 6300 55159

ENV JBOSS_HOME /opt/jboss/wildfly/
ENV LIQUIBASE_HOME /opt/jboss/liquibase/

COPY maven/wildfly-8.2.0.Final $JBOSS_HOME
COPY maven/liquibase.jar $LIQUIBASE_HOME

COPY maven/dependencies_waiter.jar $LIQUIBASE_HOME
COPY docker-entrypoint.sh /opt/dlabs/bin/

ENTRYPOINT ["/opt/dlabs/bin/docker-entrypoint.sh"]
