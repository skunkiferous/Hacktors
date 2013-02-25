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

import lombok.Data;

/**
 * Represents a game level/floor.
 *
 * Almost all the game state is stored in levels.
 * To limit memory and CPU usage, levels are broken in chunks,
 * which are lazily loaded, and generated at runtime on demand.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
@Data
public class Level {
    /** Size in X axis. */
    public static final int X = 16;

    /** Size in Y axis. */
    public static final int Y = 16;

    /** Size in total. */
    public static final int SIZE = X * Y;

    /** The level position */
    private final Position position = new Position();

    /** The chunk Generator. */
    private final Generator generator;

    /** All the chunks. */
    private final Chunk[] chunks = new Chunk[SIZE];

    /** Checks that the index are valid. */
    private void check(final int x, final int y) {
        if ((x < 0) || (x >= X)) {
            throw new IllegalArgumentException("x must be withing [0," + X
                    + "]");
        }
        if ((y < 0) || (y >= Y)) {
            throw new IllegalArgumentException("y must be withing [0," + Y
                    + "]");
        }
    }

    /** computes the linear array index in blocks, from the coordinates. */
    private int index(final int x, final int y) {
        check(x, y);
        return x + X * y;
    }

    /** Returns a Chunk. */
    public Chunk getChunk(final int x, final int y) {
        return chunks[index(x, y)];
    }

    /** Returns a Chunk. Creates it if needed. */
    public Chunk getOrCreateChunk(final int x, final int y) {
        final int index = index(x, y);
        Chunk result = chunks[index];
        if (result == null) {
            result = new Chunk();
            setChunk(x, y, result);
            generator.fill(result);
        }
        return result;
    }

    /** Updates the Chunk position! */
    private void updateChunkPosition(final int x, final int y, final Chunk chunk) {
        final Position pos = chunk.getPosition();
        final World oldWorld = pos.getWorld();
        if (oldWorld != null) {
            final Level oldLevel = oldWorld.getLevel(pos.getZ());
            if (oldLevel != null) {
                final Chunk other = oldLevel.getChunk(pos.getX(), pos.getY());
                if (other == chunk) {
                    oldLevel.setChunk(pos.getX(), pos.getY(), null);
                }
            }
        }
        pos.setX(x * X);
        pos.setY(y * Y);
        pos.setZ(position.getZ());
        pos.setWorld(position.getWorld());
        chunk.updatedPosition();
    }

    /** Sets a block. null is mapped to Empty. */
    public void setChunk(final int x, final int y, final Chunk chunk) {
        final int index = index(x, y);
        final Chunk before = chunks[index];
        if (before != chunk) {
            chunks[index] = chunk;
            if (before != null) {
                // Detach old chunk
                before.getPosition().setWorld(null);
                before.updatedPosition();
            }
            if (chunk != null) {
                updateChunkPosition(x, y, chunk);
            }
        }
    }

    /** Informs the Mobile that it's position was updated. */
    public void updatedPosition() {
        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                final Chunk chunk = getChunk(x, y);
                if (chunk != null) {
                    updateChunkPosition(x, y, chunk);
                }
            }
        }
    }
}
