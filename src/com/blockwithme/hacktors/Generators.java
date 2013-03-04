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
 * Defines multiple possible terrain generators, including totally random.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public final class Generators {
    /** The village houses coordinates. */
    private static final int[] HOUSES = new int[] { 1, 1, 1, 9, 9, 1, 9, 9 };

    /** The houses corners coordinates, relative to first house block. */
    private static final int[] CORNERS = new int[] { 0, 0, 0, 5, 5, 0, 5, 5 };

    /** The houses doors coordinates, relative to first house block. */
    private static final int[] DOORS = new int[] { 3, 0, 0, 3, 5, 2, 2, 5 };

    /** Cannot be instantiated. */
    private Generators() {
        // NOP
    }

    /** Fills in N block at random with specific type. */
    private static void fillNBlocks(final Chunk chunk, final int n,
            final BlockType type) {
        for (int i = 0; i < n; i++) {
            final int x = Util.nextInt(Chunk.X);
            final int y = Util.nextInt(Chunk.Y);
            final Block block = Block.create(type);
            chunk.setBlockLocal(x, y, block);
        }
    }

    /** Fills in (up to) N mobile at random with specific type. */
    private static void fillNMobiles(final Chunk chunk, final int n,
            final MobileType type) {
        for (int i = 0; i < n; i++) {
            int x = Util.nextInt(Chunk.X);
            int y = Util.nextInt(Chunk.Y);
            final Mobile mobile = (type == null) ? Mobile.create() : Mobile
                    .create(type);
            if (!chunk.occupiedLocal(x, y)) {
                chunk.setMobileLocal(x, y, mobile);
            } else {
                // try once more ...
                x = Util.nextInt(Chunk.X);
                y = Util.nextInt(Chunk.Y);
                if (!chunk.occupiedLocal(x, y)) {
                    chunk.setMobileLocal(x, y, mobile);
                }
            }
        }
    }

    /** Leaves the Chunk empty. */
    public static final Generator EMPTY = new Generator() {
        @Override
        public void fill(final Chunk chunk) {
            // NOP
        }
    };

    /** 10!. */
    private static final int TEN = 10;

    /** House Wall Length. */
    private static final int HOUSE_WALL_LEN = 6;

    /** Generates mostly empty "plain" land. */
    public static final Generator PLAIN = new Generator() {
        @Override
        public void fill(final Chunk chunk) {
            fillNBlocks(chunk, Util.nextInt(Chunk.SIZE / TEN + TEN / 2), null);
            fillNMobiles(chunk, TEN / 5, null);
        }
    };

    /** Generates mostly forest land. */
    public static final Generator FOREST = new Generator() {
        @Override
        public void fill(final Chunk chunk) {
            fillNBlocks(chunk, Chunk.SIZE / TEN, BlockType.Tree);
            fillNBlocks(chunk, Util.nextInt(TEN), null);
            fillNMobiles(chunk, TEN / 5, null);
        }
    };

    /** Generates mostly mountain land. */
    public static final Generator MOUNTAIN = new Generator() {
        @Override
        public void fill(final Chunk chunk) {
            fillNBlocks(chunk, Chunk.SIZE / 2, BlockType.Stone);
            fillNBlocks(chunk, Util.nextInt(TEN), null);
            fillNMobiles(chunk, TEN / 5, null);
        }
    };

    /** Generates mostly village land. */
    public static final Generator VILLAGE = new Generator() {
        @Override
        public void fill(final Chunk chunk) {
            final BlockType blockType = Util.nextBoolean() ? BlockType.Stone
                    : BlockType.Earth;
            for (int i = 0; i < HOUSES.length / 2; i++) {
                if (Util.nextFloat() <= 1.0f / 2) {
                    // The house land if 8*8 in size, so with one free block
                    // around it we get 6*6 in size.
                    final int hx = HOUSES[i * 2];
                    final int hy = HOUSES[1 + i * 2];
                    fillInHouse(chunk, blockType, hx, hy);
                }
            }
            fillNMobiles(chunk, TEN / 5, MobileType.Human);
            fillNMobiles(chunk, TEN / 5, MobileType.Pig);
            fillNMobiles(chunk, TEN / 5, MobileType.Dog);
        }

        /** Fills-in a house */
        private void fillInHouse(final Chunk chunk, final BlockType blockType,
                final int hx, final int hy) {
            // First the walls
            for (int j = 1; j < HOUSE_WALL_LEN - 1; j++) {
                chunk.setBlockLocal(hx + j, hy, Block.create(blockType));
                chunk.setBlockLocal(hx + j, hy + TEN / 2,
                        Block.create(blockType));
                chunk.setBlockLocal(hx, hy + j, Block.create(blockType));
                chunk.setBlockLocal(hx + TEN / 2, hy + j,
                        Block.create(blockType));
            }
            // Do we fill the corners too?
            if (Util.nextBoolean()) {
                for (int j = 0; j < CORNERS.length / 2; j++) {
                    final int cornerX = hx + CORNERS[j * 2];
                    final int cornerY = hy + CORNERS[j * 2 + 1];
                    chunk.setBlockLocal(cornerX, cornerY,
                            Block.create(blockType));
                }
            }
            // Now the door ...
            final int door = Util.nextInt(4);
            final int doorX = hx + DOORS[door * 2];
            final int doorY = hy + DOORS[door * 2 + 1];
            final BlockType doorType = Util.nextBoolean() ? BlockType.OpenDoor
                    : BlockType.ClosedDoor;
            chunk.setBlockLocal(doorX, doorY, Block.create(doorType));
            // Now the chest
            final BlockType chestType = Util.nextBoolean() ? BlockType.ClosedChest
                    : BlockType.OpenChest;
            chunk.setBlockLocal(hx + HOUSE_WALL_LEN / 2, hy + HOUSE_WALL_LEN
                    / 2, Block.create(chestType));
        }
    };

    /** Generates and "arena" area. */
    public static final Generator ARENA = new Generator() {
        @Override
        public void fill(final Chunk chunk) {
            fillNBlocks(chunk, Util.nextInt(Chunk.SIZE / TEN + TEN / 2), null);
            final BlockType blockType = /*Util.nextBoolean() ? BlockType.Stone
                                        : */BlockType.Earth;
            for (int x = 0; x < Chunk.X; x++) {
                chunk.setBlockLocal(x, 0, Block.create(blockType));
                chunk.setBlockLocal(x, Chunk.Y - 1, Block.create(blockType));
            }
            for (int y = 0; y < Chunk.Y; y++) {
                chunk.setBlockLocal(0, y, Block.create(blockType));
                chunk.setBlockLocal(Chunk.X - 1, y, Block.create(blockType));
            }
            fillNMobiles(chunk, TEN * 2, null);
        }
    };

    /** All normal terrain generators. */
    public static final Generator[] ALL_TERRAIN = new Generator[] { EMPTY,
            PLAIN, FOREST, MOUNTAIN, VILLAGE, ARENA };

    /** All non-empty normal terrain generators. */
    public static final Generator[] NON_EMPTY_TERRAIN = new Generator[] {
            PLAIN, FOREST, MOUNTAIN, VILLAGE, ARENA };

    /** Chooses one of the other generators at random, and calls it. */
    public static final Generator RANDOM = new RandomGenerator(ALL_TERRAIN);
}
