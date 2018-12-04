Base image for Wildfly
=
This image is a specialization of the base us.gcr.io/dlabs-dev-primary/jdk_8 image for running wildfly.
This image waits for the db to become available, runs liquibase, configures an admin user if name and password are defined, and launches wildfly.
This image is database agnostic.
This is implemented by the entry point _docker-entrypoint.sh_.

This image provides two environment variables:
* *JBOSS_HOME*, the installation location for wildfly
* *LIQUIBASE_HOME*, the installation location for liquibase

The entry point expects one argument
* *liquibase-skip*, to bypass liquibase and start wildfly.
* *liquibase-update*, to apply liquibase changes and start wildfly.
* *liquibase-update-only*, to apply liquibase changes and exit.

The entry points supports optional arguments
* *jacoco*, to enable the jacoco agent.
* *jpda*, to enable a remote debugging to attach.

The entry point accepts three environment variables:
* *JDBC_DRIVER*, optional, database driver class name
* *JDBC_URL*, optional, database connection url
* *WAITER_OPTS*, optional, options for the dependencies_waiter

If *JDBC_URL* is set, the entry point expects two secrets:
* *DB_USER*, database username
* *DB_PASSWORD*, database password

The entry point accepts two optional secrets:
* *WILDFLY_ADMIN_USER*, name for the admin user
* *WILDFLY_ADMIN_PASSWORD*, password for the admin user
