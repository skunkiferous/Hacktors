/*
 * Copyright (C) 2013 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blockwithme.hacktors;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

/**
 * The game clock.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class Clock implements Runnable {
    /** The start time. */
    private final long startrTime = System.currentTimeMillis();

    /** The duration of a cycle. */
    public static final long CYCLE = 1000L;

    /** The number of nanoseconds in a millisecond. */
    private static final long NANOS_IN_MILLIS = 1000000L;

    /** The maximum number of actions a Mobile can do in one cycle. */
    public static final int MAX_SPEED = MobileType.MAX_SPEED;

    /** The game World */
    private final World world;

    /** Should we stop? */
    private volatile boolean stop;

    /** The current cycle. */
    private final AtomicInteger cycle = new AtomicInteger();

    /** Constructor */
    public Clock(final World theWorld) {
        world = Preconditions.checkNotNull(theWorld);
    }

    /** Stop the run. */
    public void stop() {
        stop = true;
    }

    /** Start the run. */
    public void start() {
        stop = false;
        run();
    }

    /** The elapsed time. */
    public long getTime() {
        return System.currentTimeMillis() - startrTime;
    }

    /** The clock cycle. */
    public int getCycle() {
        return cycle.get();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (!stop) {
            final long before = System.nanoTime();
            world.update();
            final long after = System.nanoTime();
            final long duration = (after - before) / NANOS_IN_MILLIS;
            if (duration < CYCLE) {
                final long sleep = CYCLE - duration;
                try {
                    Thread.sleep(sleep);
                } catch (final InterruptedException e) {
                    stop = true;
                }
            }
            cycle.incrementAndGet();
        }
    }
}
