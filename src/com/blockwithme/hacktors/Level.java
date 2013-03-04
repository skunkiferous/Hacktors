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

    /** Number of mobiles contained. */
    private int mobileCount;

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

    /** Returns the number of mobiles contained. */
    public int getMobileCount() {
        return mobileCount;
    }

    /** Updates the mobile count. */
    public void updateMobileCount(final int change) {
        if (change != 0) {
            mobileCount += change;
            final World world = position.getWorld();
            if (world != null) {
                world.updateMobileCount(change);
            }
        }
    }

    /** Returns a Chunk, using chunk position. */
    public Chunk getChunk(final int x, final int y) {
        return chunks[index(x, y)];
    }

    /** Returns a Chunk, using global position. */
    public Chunk getChunkOf(final int x, final int y) {
        return getChunk(x / Chunk.X, y / Chunk.Y);
    }

    /** Returns a Chunk, using chunk position. Creates it if needed. */
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

    /** Returns a Chunk, using global position. Creates it if needed. */
    public Chunk getOrCreateChunkOf(final int x, final int y) {
        return getOrCreateChunk(x / Chunk.X, y / Chunk.Y);
    }

    /** Updates the Chunk position! */
    private void updateChunkPosition(final int x, final int y, final Chunk chunk) {
        final Position pos = chunk.getPosition();
        final World oldWorld = pos.getWorld();
        if (oldWorld != null) {
            final Level oldLevel = oldWorld.getLevel(pos.getZ());
            if (oldLevel != null) {
                final Chunk other = oldLevel.getChunkOf(pos.getX(), pos.getY());
                if (other == chunk) {
                    oldLevel.setChunkOf(pos.getX(), pos.getY(), null);
                }
            }
        }
        pos.setX(x * X);
        pos.setY(y * Y);
        pos.setZ(position.getZ());
        pos.setWorld(position.getWorld());
        chunk.updatedPosition();
    }

    /** Sets a chunk, using chunk position. */
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
            if (before != null) {
                updateMobileCount(-before.getMobileCount());
            }
            if (chunk != null) {
                updateMobileCount(chunk.getMobileCount());
            }
        }
    }

    /** Sets a chunk, using global position. */
    public void setChunkOf(final int x, final int y, final Chunk chunk) {
        setChunk(x / Chunk.X, y / Chunk.Y, chunk);
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

    /** Handles missile firing. */
    public void handleMissile(final Item missile, final int startX,
            final int startY, final Direction direction) {
        int range = missile.getType().getRange();
        Position pos = position.clone();
        pos.setX(startX);
        pos.setY(startY);
        pos.setDirection(direction);
        Position next = pos.next();
        final World world = pos.getWorld();
        final boolean egg = (missile.getType().getCategory() == ItemCategory.Egg);
        while (world.isValid(next) && (range > 0)) {
            final int x = next.getX();
            final int y = next.getY();
            final Chunk chunk = getOrCreateChunkOf(x, y);
            if (chunk.occupied(x, y)) {
                final Mobile mobile = chunk.getMobile(x, y);
                if ((mobile != null) && !egg) {
                    mobile.hitBy(missile);
                    return;
                } else {
                    // Solid block or egg ...
                    break;
                }
            } else {
                range--;
                pos = next;
                next = pos.next();
            }
        }
        // We should not be on a solid block.
        final Mobile mobile = missile.getType().spawn();
        final int x = pos.getX();
        final int y = pos.getY();
        if (mobile == null) {
            if (!missile.use()) {
                final Chunk chunk = getOrCreateChunkOf(x, y);
                chunk.addItem(x, y, missile);
            }
        } else {
            final Chunk chunk = getOrCreateChunkOf(x, y);
            chunk.setMobile(x, y, mobile);
        }
    }

    /** Runs an update cycle. */
    public void update() {
        if (mobileCount > 0) {
            for (final Chunk chunk : chunks) {
                if (chunk != null) {
                    chunk.update();
                }
            }
        }
    }

    /** Passes all mobiles to the visitor. */
    public void visitMobiles(final MobileVisitor visitor) {
        if (mobileCount > 0) {
            for (final Chunk chunk : chunks) {
                if (chunk != null) {
                    chunk.visitMobiles(visitor);
                }
            }
        }
    }
}
