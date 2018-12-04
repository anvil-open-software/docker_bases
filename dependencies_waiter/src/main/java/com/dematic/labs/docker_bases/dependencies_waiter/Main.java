/*
 * Copyright 2018 Dematic, Corp.
 * Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
 */

package com.dematic.labs.docker_bases.dependencies_waiter;

import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

import static com.dematic.labs.docker_bases.dependencies_waiter.Arguments.of;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.jodah.failsafe.Failsafe.with;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Checks the health of all the services declared as URI on the command line.
 * Returns a non-zero value if any service fails to become healthy in the allowed time.
 * <p>
 * Note that the services are checked serially. Assuming that successful healthchecks are quick,
 * converting this to parallel execution is not worth the cost.
 * In the worse case, waiting for one failing service is the dominant time.
 * In the good case, executing a (reasonable) number of quick healthchecks will not be much slower, if not faster than,
 * executing them all in parallel.
 */
public class Main {
    private static final Logger LOGGER = getLogger(Main.class);
    private final Arguments arguments;

    public static void main(String... argv) {
        try {
            System.exit(new Main(of(argv)).run());
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    Main(final Arguments arguments) {
        this.arguments = arguments;
    }

    int run() {
        final long timeMillis = currentTimeMillis();
        LOGGER.info("Running with " + arguments);

        try {
            TimeUnit.SECONDS.sleep(arguments.getInterval());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Interrupted during interval sleep: " + e.getMessage(), e);
            return 1;
        }

        for (final Healthcheck healthcheck : arguments.getDependencies()) {
            healthcheck.setArguments(arguments);
            if (!with(arguments.toRetryPolicy()).get(healthcheck)) {
                LOGGER.info(healthcheck + " failed with " + arguments.timingInformation());
                return 2;
            }
        }

        LOGGER.info("All dependencies up; waited " + MILLISECONDS.toSeconds(currentTimeMillis() - timeMillis) + " s");
        return 0;
    }
}
