/*
 * Copyright 2018 Dematic, Corp.
 * Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
 */

package com.dematic.labs.docker_bases.dependencies_waiter;

import com.dematic.labs.toolkit.helpers.test_util.SecretHelper;
import org.junit.Test;

import static com.dematic.labs.toolkit.helpers.test_util.DockerHelper.getPort;
import static com.dematic.labs.toolkit.helpers.test_util.SecretHelper.getSecret;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JdbcHealthcheckIT {
    @Test
    public void authenticationInSecretShouldConnect() throws Exception {
        assertTrue(createJdbcHealthcheck().call());
    }

    @Test
    public void authenticationInUrlShouldConnect() throws Exception {
        assertTrue(createJdbcHealthcheck(getPort("mysql.port"), getSecret("DB_NAME"), getSecret("DB_USER")).call());
    }

    @Test
    public void noConnectionIsNotHealthy() throws Exception {
        assertFalse(createJdbcHealthcheck(getPort("mysql.port") + "1", getSecret("DB_NAME"), getSecret("DB_USER")).call());
    }

    @Test
    public void wrongDatabaseIsNotHealthy() throws Exception {
        assertFalse(createJdbcHealthcheck(getPort("mysql.port"), getSecret("DB_NAME") + "_NOT", getSecret("DB_USER")).call());
    }

    @Test
    public void wrongUserIsNotHealthy() throws Exception {
        assertFalse(createJdbcHealthcheck(getPort("mysql.port"), getSecret("DB_NAME"), getSecret("DB_USER") + "_NOT").call());
    }

    private static Healthcheck createJdbcHealthcheck(final String port, final String db_name, final String db_user) {
        final Arguments arguments = Arguments.of(
            "--timeout", "2",
            "jdbc:mysql://localhost:" + port + "/" + db_name + "?user=" + db_user + "&password=" + getSecret("DB_PASSWORD")
        );
        final Healthcheck healthcheck = arguments.getDependencies().get(0);
        healthcheck.setArguments(arguments);
        return healthcheck;
    }

    private static Healthcheck createJdbcHealthcheck() {
        final Arguments arguments = Arguments.of(
            "--timeout", "2",
            "--secretPath", SecretHelper.SECRETS_PATH,
            "jdbc:mysql://localhost:" + getPort("mysql.port") + "/" + getSecret("DB_NAME")
        );
        final Healthcheck healthcheck = arguments.getDependencies().get(0);
        healthcheck.setArguments(arguments);
        return healthcheck;
    }

}
