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

import java.util.Arrays;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.ArrayUtils;

/**
 * A chunk is fixed-size a part of a game level.
 *
 * It is used to split up the game levels so that they can be distributed more
 * easily, and so that only the active parts of the world need to be in memory
 * at any time.
 *
 * It contains directly most of the game state.
 *
 * Since it represents parts of a level, data can be accessed both with
 * absolute coordinates, and with relative coordinates.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class Chunk {
    /** Size in X axis. */
    public static final int X = 16;

    /** Size in Y axis. */
    public static final int Y = 16;

    /** Size in total. */
    public static final int SIZE = X * Y;

    /** The Chunk position */
    private final Position position = new Position();

    /** All the chunk blocks. */
    private final Block[] blocks = new Block[SIZE];

    /** All the chunk Mobiles. */
    private final Mobile[] mobiles = new Mobile[SIZE];

    /** All the chunk Items. */
    private final Item[][] items = new Item[SIZE][];

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

    /** Computes the linear array index, from the coordinates. */
    private int index(final int x, final int y) {
        check(x, y);
        return x + X * y;
    }

    /** Creates an empty Chunk. */
    public Chunk() {
        Arrays.fill(blocks, Block.EMPTY);
        Arrays.fill(items, Item.EMPTY);
    }

    /**
     * Returns the position
     */
    public Position getPosition() {
        return position;
    }

    /** Returns the number of mobiles contained. */
    public int getMobileCount() {
        return mobileCount;
    }

    /** Returns a Block, using local coordinates. It an never be null. */
    public Block getBlockLocal(final int x, final int y) {
        return blocks[index(x, y)];
    }

    /** Returns a Block. It an never be null. */
    public Block getBlock(final int x, final int y) {
        return getBlockLocal(x - position.getX(), y - position.getY());
    }

    /** Sets a block, using local coordinates. null is mapped to Empty. */
    public void setBlockLocal(final int x, final int y, final Block block) {
        final int index = index(x, y);
        if (block == null) {
            blocks[index] = Block.EMPTY;
        } else {
            if (block.getType().isSolid() && (mobiles[index] != null)) {
                throw new IllegalArgumentException("Coordinate (" + x + "," + y
                        + ") contains a mobile!");
            }
            blocks[index] = block;
        }
    }

    /** Sets a block. null is mapped to Empty. */
    public void setBlock(final int x, final int y, final Block block) {
        setBlockLocal(x - position.getX(), y - position.getY(), block);
    }

    /** Returns true, if the block at the given coordinate is solid, using local coordinates. */
    public boolean solidLocal(final int x, final int y) {
        return blocks[index(x, y)].getType().isSolid();
    }

    /** Returns true, if the block at the given coordinate is solid. */
    public boolean solid(final int x, final int y) {
        return solidLocal(x - position.getX(), y - position.getY());
    }

    /** Returns a Mobile, using local coordinates. Can be null. */
    public Mobile getMobileLocal(final int x, final int y) {
        return mobiles[index(x, y)];
    }

    /** Returns a Mobile. Can be null. */
    public Mobile getMobile(final int x, final int y) {
        return getMobileLocal(x - position.getX(), y - position.getY());
    }

    /** Returns true, if the block at the given coordinate is either solid, or occupied by a mobile, using local coordinates. */
    public boolean occupiedLocal(final int x, final int y) {
        final int index = index(x, y);
        return blocks[index].getType().isSolid() || (mobiles[index] != null);
    }

    /** Returns true, if the block at the given coordinate is either solid, or occupied by a mobile. */
    public boolean occupied(final int x, final int y) {
        return occupiedLocal(x - position.getX(), y - position.getY());
    }

    /** Updates the Mobile position, using local coordinates! */
    private void updateMobilePosition(final int x, final int y,
            final Mobile mobile) {
        final Position pos = mobile.getPosition();
        final World world = position.getWorld();
        if (world != null) {
            final Level level = world.getLevel(pos.getZ());
            if (level != null) {
                final Chunk chunk = level.getChunk(pos.getX(), pos.getY());
                if (chunk != null) {
                    final Mobile other = chunk
                            .getMobile(pos.getX(), pos.getY());
                    if (other == mobile) {
                        chunk.setMobile(pos.getX(), pos.getY(), null);
                    }
                }
            }
        }
        pos.setX(position.getX() + x);
        pos.setY(position.getY() + y);
        final boolean changedLevel = (position.getZ() != pos.getZ());
        pos.setZ(position.getZ());
        pos.setWorld(world);
        mobile.updatedPosition(changedLevel);
    }

    /** Sets a Mobile, using local coordinates. */
    public void setMobileLocal(final int x, final int y, final Mobile mobile) {
        final int index = index(x, y);
        final Mobile before = mobiles[index];
        if (before != mobile) {
            if (mobile != null) {
                if (solidLocal(x, y)) {
                    throw new IllegalArgumentException("Coordinate (" + x + ","
                            + y + ") is solid!");
                }
                mobiles[index] = mobile;
                if (before != null) {
                    before.getPosition().setWorld(null);
                    before.updatedPosition(false);
                }
                updateMobilePosition(x, y, mobile);
            } else {
                mobiles[index] = null;
            }
        }
        final int countBefore = mobileCount;
        if (before != null) {
            mobileCount--;
        }
        if (mobile != null) {
            mobileCount++;
        }
        final int countChange = mobileCount - countBefore;
        if (countChange != 0) {
            final World world = position.getWorld();
            if (world != null) {
                final Level level = world.getLevel(position.getZ());
                level.updateMobileCount(countChange);
            }
        }
    }

    /** Sets a Mobile. */
    public void setMobile(final int x, final int y, final Mobile mobile) {
        setMobileLocal(x - position.getX(), y - position.getY(), mobile);
    }

    /** Returns the items, using local coordinates. */
    public Item[] getItemsLocal(final int x, final int y) {
        final Item[] array = items[index(x, y)];
        return (array.length == 0) ? array : array.clone();
    }

    /** Returns the items. */
    public Item[] getItems(final int x, final int y) {
        return getItemsLocal(x - position.getX(), y - position.getY());
    }

    /** Adds an item, using local coordinates. */
    public void addItemLocal(final int x, final int y, final Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item is null");
        }
        final int index = index(x, y);
        if (!ArrayUtils.contains(items[index], item)) {
            items[index] = (Item[]) ArrayUtils.add(items[index], item);
        }
    }

    /** Adds an item. */
    public void addItem(final int x, final int y, final Item item) {
        addItemLocal(x - position.getX(), y - position.getY(), item);
    }

    /** Removes an item, using local coordinates. */
    public void removeItemLocal(final int x, final int y, final Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item is null");
        }
        final int index = index(x, y);
        final int where = ArrayUtils.indexOf(items[index], item);
        if (where < 0) {
            throw new IllegalArgumentException("item not found");
        }
        items[index] = (Item[]) ArrayUtils.remove(items[index], where);
    }

    /** Removes an item. */
    public void removeItem(final int x, final int y, final Item item) {
        removeItemLocal(x - position.getX(), y - position.getY(), item);
    }

    /** Informs the Chunk that it's position was updated. */
    public void updatedPosition() {
        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                final Mobile mobile = getMobileLocal(x, y);
                if (mobile != null) {
                    updateMobilePosition(x, y, mobile);
                }
            }
        }
    }

    /** Runs an update cycle. */
    public void update() {
        if (mobileCount > 0) {
            for (final Mobile mobile : mobiles) {
                if (mobile != null) {
                    mobile.getController().act(mobile);
                }
            }
        }
    }
}
