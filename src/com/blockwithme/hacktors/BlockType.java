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

import org.apache.commons.lang.ArrayUtils;

import com.google.common.base.Preconditions;

/**
 * The possible block types.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public enum BlockType {
    Bedrock(0, true), Empty(0, false), Stone(100, true), Earth(30, true), Tree(
            30, true), ClosedDoor(20, true), OpenDoor(10, false), ClosedChest(
            20, false), EmptyChest(10, false), StairsUp(200, false), StairsDown(
            200, false), Anvil(100, false), Trap(20, false);

    /** AN empty array of block types. */
    public static final BlockType[] EMPTY = new BlockType[0];

    /** The life of a block of this type. */
    private final int life;

    /**
     * Are blocks of this type solid?
     * A mobile cannot be located where a solid block is.
     */
    public final boolean solid;

    /**
     * Chooses one block type at random.
     * Note that Bedrock is excluded from the selection.
     */
    public static BlockType choose() {
        // We never generate bedrock!
        return values()[Util.RND.nextInt(values().length - 1) + 1];
    }

    /** Crates a Block type. */
    private BlockType(final int theLife, final boolean theSolid) {
        Preconditions.checkArgument(theLife >= 0, "Life must be >= 0");
        life = theLife;
        solid = theSolid;
    }

    /** Finalizes the initialization of an block of this type. */
    public Block postInit(final Block block) {
        Preconditions.checkNotNull(block);
        Preconditions.checkArgument(block.getType() == this);
        block.setLife((life == -1) ? -1 : life);
        if (this == ClosedChest) {
            final int count = Util.RND.nextInt(2) + 1;
            Item[] content = block.getContent();
            for (int i = 0; i < count; i++) {
                content = (Item[]) ArrayUtils.add(content, Item.create());
            }
            block.setContent(content);
        }
        return block;
    }
}
