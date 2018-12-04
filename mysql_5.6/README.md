Base image for MySQL
=
This image is a specialization of the base mysql:5.6.40 image for running MySQL.
This image launches collects the necessary secrets and launch MySql.
This is implemented by the entry point _docker-entrypoint.sh_.

The entry point expects four secrets to configure MySQL:
* *DB_USER*, name of the superuser for the named database
* *DB_PASSWORD*, password of the superuser for the named database
* *DB_NAME*, name of a database to be created on image startup if not present
* *MYSQL_ROOT_PASSWORD*, database root password

Other aspects of MySQL can be configured as described on the [docker store](https://hub.docker.com/_/mysql/)
and [MySql](https://dev.mysql.com/doc/refman/5.6/en/linux-installation-docker.html).
