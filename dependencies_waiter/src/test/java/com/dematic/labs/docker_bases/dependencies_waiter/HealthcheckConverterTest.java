/*
 * Copyright 2018 Dematic, Corp.
 * Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
 */

package com.dematic.labs.docker_bases.dependencies_waiter;

import com.beust.jcommander.ParameterException;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class HealthcheckConverterTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void jdbcSchemeIsRecognized() {
        final Healthcheck healthcheck = new HealthcheckConverter("").convert("jdbc:mysql://localhost/");
        assertThat(healthcheck, instanceOf(LoggingDecorator.class));
        assertThat(((LoggingDecorator) healthcheck).getHealthcheck(), instanceOf(JdbcHealthcheck.class));
    }

    @Test
    public void unsupportedSchemeFails() {
        expectedException.expect(ParameterException.class);
        expectedException.expectMessage("couldn't convert \"x-dna://whatever\" to a healtcheck.");
        expectedException.expectMessage(Matchers.endsWith(" Supported protocol: [jdbc]"));
        new HealthcheckConverter("").convert("x-dna://whatever");
    }

    @Test
    public void unparsableUriFails() {
        expectedException.expect(ParameterException.class);
        expectedException.expectMessage("couldn't convert \"this is not a uri\" to a URI:");
        new HealthcheckConverter("").convert("this is not a uri");
    }
}
