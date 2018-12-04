/*
 * Copyright 2018 Dematic, Corp.
 * Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
 */

package com.dematic.labs.docker_bases.dependencies_waiter;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.lang.Boolean.FALSE;
import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;
import static java.sql.DriverManager.setLoginTimeout;
import static org.slf4j.LoggerFactory.getLogger;

public class JdbcHealthcheck implements Healthcheck {
    private static final Logger LOGGER = getLogger(JdbcHealthcheck.class);
    private final String jdbcUrl;
    private Arguments arguments;

    JdbcHealthcheck(final URI uri) {
        this.jdbcUrl = uri.toString();
    }

    @Override
    public void setArguments(final Arguments arguments) {
        this.arguments = arguments;
    }

    @Override
    public String getUrl() {
        return jdbcUrl;
    }

    @Override
    public String toString() {
        return jdbcUrl;
    }

    @Override
    public Boolean call() {
        setLoginTimeout(arguments.getTimeout());
        try (Connection connect = getConnection()) {
            return connect.isValid(arguments.getTimeout());
        } catch (SQLException e) {
            if (e.getMessage().startsWith("No suitable driver found for ")) {
                throw new IllegalStateException(e);
            }
            LOGGER.debug(e.toString(), e);
        }
        return FALSE;
    }

    private Connection getConnection() throws SQLException {
        if (arguments.hasSecretPath()) {
            final String db_user = getSecret("DB_USER");
            final String db_password = getSecret("DB_PASSWORD");
            if (!(db_user == null || db_password == null)) {
                return DriverManager.getConnection(jdbcUrl, db_user, db_password);
            }
        }
        return DriverManager.getConnection(jdbcUrl);
    }

    private String getSecret(String name) {
        Path path = get(arguments.getSecretPath().getAbsolutePath(), name);
        if (path.toFile().isFile()) {
            try {
                return lines(path).findFirst().orElseThrow(() -> new IllegalArgumentException("Undefined secret " + path));
            } catch (IOException e) {
                throw new IllegalArgumentException("Undefined secret " + path, e);
            }
        }
        return null;
    }

}
