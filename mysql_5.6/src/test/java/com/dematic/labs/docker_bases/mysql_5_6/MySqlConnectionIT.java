/*
 * Copyright 2018 Dematic, Corp.
 * Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
 */

package com.dematic.labs.docker_bases.mysql_5_6;

import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static com.dematic.labs.toolkit.helpers.test_util.DockerHelper.getPort;
import static com.dematic.labs.toolkit.helpers.test_util.SecretHelper.getSecret;
import static java.sql.DriverManager.getConnection;
import static org.junit.Assert.assertTrue;

public class MySqlConnectionIT {

    @Test
    public void databaseShouldBeUp() throws SQLException {
        try (Connection connect = getConnection(
            "jdbc:mysql://localhost:" + getPort("mysql.port") + "/" + getSecret("DB_NAME"),
            getSecret("DB_USER"),
            getSecret("DB_PASSWORD"))) {
            assertTrue(connect.isValid(10));
        }
    }
}
