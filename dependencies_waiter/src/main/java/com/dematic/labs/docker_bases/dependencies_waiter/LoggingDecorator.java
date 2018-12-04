/*
 * Copyright 2018 Dematic, Corp.
 * Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
 */

package com.dematic.labs.docker_bases.dependencies_waiter;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

class LoggingDecorator implements Healthcheck {
    private static final Logger LOGGER = getLogger(LoggingDecorator.class);
    private final Healthcheck healthcheck;

    LoggingDecorator(final Healthcheck healthcheck) {
        this.healthcheck = healthcheck;
    }

    Healthcheck getHealthcheck() {
        return healthcheck;
    }

    @Override
    public void setArguments(Arguments arguments) {
        healthcheck.setArguments(arguments);
    }

    @Override
    public String getUrl() {
        return healthcheck.getUrl();
    }

    @Override
    public Boolean call() throws Exception {
        LOGGER.info("Checking " + getUrl());
        return healthcheck.call();
    }
}
