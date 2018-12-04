/*
 * Copyright 2018 Dematic, Corp.
 * Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
 */

package com.dematic.labs.docker_bases.dependencies_waiter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.dematic.labs.docker_bases.dependencies_waiter.Arguments.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArgumentsTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void noJdbcUrlShouldFail() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("There must be one or more service(s) to wait for");
        of();
    }

    @Test
    public void singleJdbcUrlShouldBeAccepted() {
        final Arguments arguments = of("jdbc:mysql://localhost/database");
        assertEquals(1, arguments.getDependencies().size());
        assertEquals("jdbc:mysql://localhost/database", arguments.getDependencies().get(0).getUrl());
        assertEquals(30, arguments.getInterval());
        assertEquals(3, arguments.getRetries());
        assertFalse(arguments.hasSecretPath());
        assertEquals(30, arguments.getTimeout());
    }

    @Test
    public void twoJdbcUrlShouldBeAccepted() {
        final Arguments arguments = of("jdbc:mysql://localhost/database", "jdbc:mysql://remotehost/database");
        assertEquals(2, arguments.getDependencies().size());
        assertEquals("jdbc:mysql://localhost/database", arguments.getDependencies().get(0).getUrl());
        assertEquals("jdbc:mysql://remotehost/database", arguments.getDependencies().get(1).getUrl());
    }

    @Test
    public void intervalOptionShouldBeAccepted() {
        final Arguments arguments = of("--interval", "10", "jdbc:mysql://localhost/database");
        assertEquals(1, arguments.getDependencies().size());
        assertEquals("jdbc:mysql://localhost/database", arguments.getDependencies().get(0).getUrl());
        assertEquals(10, arguments.getInterval());
        assertEquals(3, arguments.getRetries());
        assertFalse(arguments.hasSecretPath());
        assertEquals(30, arguments.getTimeout());
    }

    @Test
    public void retriesOptionShouldBeAccepted() {
        final Arguments arguments = of("--retries", "10", "jdbc:mysql://localhost/database");
        assertEquals(1, arguments.getDependencies().size());
        assertEquals("jdbc:mysql://localhost/database", arguments.getDependencies().get(0).getUrl());
        assertEquals(30, arguments.getInterval());
        assertEquals(10, arguments.getRetries());
        assertFalse(arguments.hasSecretPath());
        assertEquals(30, arguments.getTimeout());
    }

    @Test
    public void secretPathOptionShouldBeAccepted() {
        final Arguments arguments = of("--secretPath", "/opt/dlabs/secrets", "jdbc:mysql://localhost/database");
        assertEquals(1, arguments.getDependencies().size());
        assertEquals("jdbc:mysql://localhost/database", arguments.getDependencies().get(0).getUrl());
        assertEquals(30, arguments.getInterval());
        assertEquals(3, arguments.getRetries());
        assertTrue(arguments.hasSecretPath());
        assertEquals("/opt/dlabs/secrets", arguments.getSecretPath().getAbsolutePath());
        assertEquals(30, arguments.getTimeout());
    }

    @Test
    public void timeoutOptionShouldBeAccepted() {
        final Arguments arguments = of("--timeout", "10", "jdbc:mysql://localhost/database");
        assertEquals(1, arguments.getDependencies().size());
        assertEquals("jdbc:mysql://localhost/database", arguments.getDependencies().get(0).getUrl());
        assertEquals(30, arguments.getInterval());
        assertEquals(3, arguments.getRetries());
        assertFalse(arguments.hasSecretPath());
        assertEquals(10, arguments.getTimeout());
    }

}
