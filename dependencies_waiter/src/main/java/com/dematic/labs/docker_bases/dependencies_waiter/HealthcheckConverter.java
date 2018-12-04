/*
 * Copyright 2018 Dematic, Corp.
 * Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
 */

package com.dematic.labs.docker_bases.dependencies_waiter;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.BaseConverter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static com.dematic.labs.docker_bases.dependencies_waiter.HealthcheckConverter.Protocol.supported;
import static com.dematic.labs.docker_bases.dependencies_waiter.HealthcheckConverter.Protocol.valueOf;

public class HealthcheckConverter extends BaseConverter<Healthcheck> {
    HealthcheckConverter(final String optionName) {
        super(optionName);
    }

    @Override
    public Healthcheck convert(final String value) {
        try {
            final URI uri = new URI(value);
            return new LoggingDecorator(valueOf(uri.getScheme()).newHealthcheck(uri));
        } catch (IllegalArgumentException e) {
            throw new ParameterException(getErrorString(value, "a healtcheck. Supported protocol: " + supported()));
        } catch (URISyntaxException e) {
            throw new ParameterException(getErrorString(value, "a URI: " + e.getMessage()), e);
        }
    }

    enum Protocol {
        jdbc {
            @Override
            Healthcheck newHealthcheck(final URI uri) {
                return new JdbcHealthcheck(uri);
            }
        };

        abstract Healthcheck newHealthcheck(final URI uri);

        static String supported() {
            return Arrays.toString(values());
        }
    }
}
