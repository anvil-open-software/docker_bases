/*
 * Copyright 2018 Dematic, Corp.
 * Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
 */

package com.dematic.labs.docker_bases.dependencies_waiter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import net.jodah.failsafe.RetryPolicy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

class Arguments {
    @Parameter(description = "URL for service(s) to wait for", converter = HealthcheckConverter.class)
    private List<Healthcheck> dependencies = new ArrayList<>();
    @Parameter(names = "--interval", description = "The health call will first run interval seconds after the container is started, and then again interval seconds after each previous call completes.")
    private int interval = 30;
    @Parameter(names = "--retries", description = "It takes retries consecutive failures of the health call for the container to be considered unhealthy.")
    private int retries = 3;
    @Parameter(names = "--secretPath", description = "Path to secrets. Expects each file to be the name of the secret.", converter = FileConverter.class)
    private File secretPath;
    @Parameter(names = "--timeout", description = "If a single run of the call takes longer than timeout seconds then the call is considered to have failed.")
    private int timeout = 30;

    static Arguments of(final String... argv) {
        final Arguments arguments = new Arguments();
        JCommander.newBuilder()
            .addObject(arguments)
            .build()
            .parse(argv);
        if (arguments.getDependencies().isEmpty()) {
            throw new IllegalArgumentException("There must be one or more service(s) to wait for");
        }
        return arguments;
    }

    @Override
    public String toString() {
        return timingInformation() + " " + dependencies.stream().map(Object::toString).collect(joining(" "));
    }

    public String timingInformation() {
        return
            "--interval " + interval +
                " --retries " + retries +
                " --timeout " + timeout;
    }

    public List<Healthcheck> getDependencies() {
        return unmodifiableList(dependencies);
    }

    public int getInterval() {
        return interval;
    }

    public int getRetries() {
        return retries;
    }

    public File getSecretPath() {
        return secretPath;
    }

    public boolean hasSecretPath() {
        return secretPath != null;
    }

    public int getTimeout() {
        return timeout;
    }

    public RetryPolicy toRetryPolicy() {
        return new RetryPolicy()
            .retryWhen(Boolean.FALSE)
            .withDelay(interval, TimeUnit.SECONDS)
            .withMaxRetries(retries)
            ;
    }
}
