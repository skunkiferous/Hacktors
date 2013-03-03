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

import org.apache.commons.lang.ArrayUtils;

import com.blockwithme.util.Enum40;
import com.google.common.base.Preconditions;

/**
 * The data associated with each BlockType.
 *
 * @author monster
 */
public class BlockType extends Enum40<BlockType> {
    /** serialVersionUID */
    private static final long serialVersionUID = 6131338755498127582L;

    /** AN empty array of block types. */
    public static final BlockType[] EMPTY = new BlockType[0];

    // CHECKSTYLE.OFF: ConstantName
    /** The Bedrock item type. */
    public static final BlockType Bedrock = new BlockType(0, true, '!');
    /** The Empty item type. */
    public static final BlockType Empty = new BlockType(0, false, ' ');
    /** The Stone item type. */
    public static final BlockType Stone = new BlockType(100, true, '$');
    /** The Earth item type. */
    public static final BlockType Earth = new BlockType(30, true, '§');
    /** The Tree item type. */
    public static final BlockType Tree = new BlockType(30, true, '#');
    /** The ClosedDoor item type. */
    public static final BlockType ClosedDoor = new BlockType(20, true, '+');
    /** The OpenDoor item type. */
    public static final BlockType OpenDoor = new BlockType(10, false, '-');
    /** The ClosedChest item type. */
    public static final BlockType ClosedChest = new BlockType(20, false, '[');
    /** The OpenChest item type. */
    public static final BlockType OpenChest = new BlockType(10, false, ']');
    /** The StairsUp item type. */
    public static final BlockType StairsUp = new BlockType(200, false, '<');
    /** The StairsDown item type. */
    public static final BlockType StairsDown = new BlockType(200, false, '>');
    /** The Anvil item type. */
    public static final BlockType Anvil = new BlockType(100, false, '=');
    /** The Trap item type. */
    public static final BlockType Trap = new BlockType(20, false, '^');
    // CHECKSTYLE.ON: ConstantName

    /** All the values */
    public static final BlockType[] VALUES = Enum40.values(BlockType.class);

    /** Trap damage */
    public static final int TRAP_DAMAGE = 10;

    /** The life of a block of this type. 0 means indestructible. */
    private final transient int life;

    /**
     * Are blocks of this type solid?
     * A mobile cannot be located where a solid block is.
     */
    private final transient boolean solid;

    /** Graphic representation of this block. */
    private final transient char display;

    /**
     * Are blocks of this type solid?
     * A mobile cannot be located where a solid block is.
     */
    public boolean isSolid() {
        return solid;
    }

    /**
     * Are blocks of this type damageable?
     */
    public boolean isDamageable() {
        return life != 0;
    }

    /**
     * Chooses one block type at random.
     * Note that Bedrock is excluded from the selection.
     */
    public static BlockType choose() {
        // We never generate bedrock!
        return BlockType.VALUES[Util.RND.nextInt(BlockType.VALUES.length - 1) + 1];
    }

    /** Crates a Block type. */
    BlockType(final int theLife, final boolean theSolid, final char theDisplay) {
        super(BlockType.class);
        Preconditions.checkArgument(theLife >= 0, "Life must be >= 0");
        life = theLife;
        solid = theSolid;
        display = theDisplay;
    }

    /** Finalizes the initialization of an block of this type. */
    public Block postInit(final Block block) {
        Preconditions.checkNotNull(block);
        Preconditions.checkArgument(block.getType() == this);
        block.setLife((life == -1) ? -1 : life);
        if (this == BlockType.ClosedChest) {
            final int count = Util.RND.nextInt(2) + 1;
            Item[] content = block.getContent();
            for (int i = 0; i < count; i++) {
                content = (Item[]) ArrayUtils.add(content, Item.create());
            }
            block.setContent(content);
        }
        return block;
    }

    /** Graphic representation of this block. */
    public char getDisplay() {
        return display;
    }
}