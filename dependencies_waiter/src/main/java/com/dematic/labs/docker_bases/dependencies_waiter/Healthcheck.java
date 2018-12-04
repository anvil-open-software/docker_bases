/*
 * Copyright 2018 Dematic, Corp.
 * Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
 */

package com.dematic.labs.docker_bases.dependencies_waiter;

import java.util.concurrent.Callable;

/**
 * A single call to probe the health of a service described by its URL.
 * The probe will be called again if the call returns false.
 * The application will fail and exit if the call throws anything.
 */
interface Healthcheck extends Callable<Boolean> {
    void setArguments(final Arguments arguments);

    String getUrl();
}
