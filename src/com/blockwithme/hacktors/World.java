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

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents one game world; it is the core, upon which everything else hangs.
 *
 * It is composed of multiple levels, each on top/under of the previous one.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class World {

    /** Size in Z axis, which is the number of levels. */
    public static final int Z = 256;

    /** All the levels. */
    private final Level[] levels = new Level[Z];

    /** Returns the Level. */
    public Level getLevel(final int z) {
        return levels[z];
    }

    /** Returns the Level. Creates it if needed. */
    public Level getOrCreateLevel(final int z) {
        Level result = levels[z];
        if (result == null) {
            // TODO : we should have some kind of game world profiles, that dictates those probabilities.
            if (Util.RND.nextBoolean()) {
                result = new Level(Generators.RANDOM);
            } else {
                final int gen = Util.RND.nextInt(Generators.ALL_TERRAIN.length);
                result = new Level(Generators.ALL_TERRAIN[gen]);
            }
            setLevel(z, result);
        }
        return result;
    }

    /** Sets the position in the Level. */
    private void updateLevelPosition(final int z, final Level level) {
        final Position pos = level.getPosition();
        // x and y are meaningless for levels
        pos.setZ(z);
        pos.setWorld(this);
        level.updatedPosition();
    }

    /** Sets the Level. */
    public void setLevel(final int z, final Level level) {
        final Level before = levels[z];
        if (before != level) {
            levels[z] = level;
            if (before != null) {
                before.getPosition().setWorld(null);
                level.updatedPosition();
            }
            if (level != null) {
                updateLevelPosition(z, level);
            }
        }
    }

    /** Returns all the levels. */
    public Level[] getLevels() {
        return levels.clone();
    }

    /** Returns the current time. Eventually would come from some distributed API. */
    // TODO We should have a distinct clock entity
    public long getTime() {
        return System.currentTimeMillis();
    }
}
