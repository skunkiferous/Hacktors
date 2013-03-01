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

import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Defines multiple possible terrain generators, including totally random.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class Generators {
    /** The random number generator. */
    private static final Random RND = Util.RND;

    /** The village houses coordinates. */
    private static final int[] HOUSES = new int[] {1, 1, 1, 9, 9, 1, 9, 9};

    /** The houses corners coordinates, relative to first house block. */
    private static final int[] CORNERS = new int[] {0, 0, 0, 5, 5, 0, 5, 5};

    /** The houses doors coordinates, relative to first house block. */
    private static final int[] DOORS = new int[] {3, 0, 0, 3, 5, 2, 2, 5};

    /** Fills in N block at random with specific type. */
    private static void fillN(final Chunk chunk, final int n,
            final BlockType type) {
        for (int i = 0; i < n; i++) {
            final int x = RND.nextInt(Chunk.X);
            final int y = RND.nextInt(Chunk.Y);
            final Block block = Block.create(type);
            chunk.setBlockLocal(x, y, block);
        }
    }

    /** Leaves the Chunk empty. */
    public static final Generator EMPTY = new Generator() {
        @Override
        public void fill(final Chunk chunk) {
            // NOP
        }
    };

    /** Generates mostly empty "plain" land. */
    public static final Generator PLAIN = new Generator() {
        @Override
        public void fill(final Chunk chunk) {
            fillN(chunk, RND.nextInt(Chunk.SIZE / 10 + 5), null);
        }
    };

    /** Generates mostly forest land. */
    public static final Generator FOREST = new Generator() {
        @Override
        public void fill(final Chunk chunk) {
            fillN(chunk, Chunk.SIZE / 8, BlockType.Tree);
            fillN(chunk, RND.nextInt(10), null);
        }
    };

    /** Generates mostly mountain land. */
    public static final Generator MOUNTAIN = new Generator() {
        @Override
        public void fill(final Chunk chunk) {
            fillN(chunk, Chunk.SIZE / 2, BlockType.Stone);
            fillN(chunk, RND.nextInt(10), null);
        }
    };

    /** Generates mostly village land. */
    public static final Generator VILLAGE = new Generator() {
        @Override
        public void fill(final Chunk chunk) {
            final BlockType blockType = RND.nextBoolean() ? BlockType.Stone
                    : BlockType.Earth;
            for (int i = 0; i < HOUSES.length / 2; i++) {
                if (RND.nextFloat() <= 0.2f) {
                    // The house land if 8*8 in size, so with one free block
                    // around it we get 6*6 in size.
                    final int hx = HOUSES[i * 2];
                    final int hy = HOUSES[1 + i * 2];
                    // First the walls
                    for (int j = 1; j < 6; j++) {
                        chunk.setBlockLocal(hx + j, hy,
                        		Block.create(blockType));
                        chunk.setBlockLocal(hx + j, hy + 5,
                                Block.create(blockType));
                        chunk.setBlockLocal(hx, hy + j,
                        		Block.create(blockType));
                        chunk.setBlockLocal(hx + 5, hy + j,
                                Block.create(blockType));
                    }
                    // Do we fill the corners too?
                    if (RND.nextBoolean()) {
                        for (int j = 0; j < CORNERS.length / 2; j++) {
                            final int cornerX = CORNERS[j * 2];
                            final int cornerY = CORNERS[j * 2 + 1];
                            chunk.setBlockLocal(cornerX, cornerY,
                                    Block.create(blockType));
                        }
                    }
                    // Now the door ...
                    final int door = RND.nextInt(4);
                    final int doorX = DOORS[door * 2];
                    final int doorY = DOORS[door * 2 + 1];
                    final BlockType doorType = RND.nextBoolean()
                    		? BlockType.OpenDoor
                            : BlockType.ClosedDoor;
                    chunk.setBlockLocal(doorX, doorY, Block.create(doorType));
                    // Now the chest
                    final BlockType chestType = RND.nextBoolean()
                    		? BlockType.ClosedChest
                            : BlockType.EmptyChest;
                    chunk.setBlockLocal(3, 3, Block.create(chestType));
                }
            }
        }
    };

    /** All normal terrain generators. */
    public static final Generator[] ALL_TERRAIN = new Generator[] {EMPTY,
            PLAIN, FOREST, MOUNTAIN, VILLAGE};

    /** Chooses one of the other generators at random, and calls it. */
    public static final Generator RANDOM = new RandomGenerator(ALL_TERRAIN);
}
