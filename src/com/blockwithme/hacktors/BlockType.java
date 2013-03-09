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

import com.blockwithme.base40.Enum40;
import com.google.common.base.Preconditions;

/**
 * The data associated with each BlockType.
 *
 * @author monster
 */
public class BlockType extends Enum40<BlockType> implements Displayable {
    /** serialVersionUID */
    private static final long serialVersionUID = 6131338755498127582L;

    /** AN empty array of block types. */
    public static final BlockType[] EMPTY = new BlockType[0];

    // CHECKSTYLE.OFF: ConstantName
    /** The Bedrock item type. */
    public static final BlockType Bedrock = new BlockType(0, true, '!',
            Color.RED);

    /** The Empty item type. */
    public static final BlockType Empty = new BlockType(0, false, ' ',
            Color.WHITE);

    /** The Stone item type. */
    public static final BlockType Stone = new BlockType(100, true, '$',
            Color.YELLOW);

    /** The Earth item type. */
    public static final BlockType Earth = new BlockType(30, true, '&',
            Color.MAGENTA);

    /** The Tree item type. */
    public static final BlockType Tree = new BlockType(30, true, '#',
            Color.GREEN);

    /** The ClosedDoor item type. */
    public static final BlockType ClosedDoor = new BlockType(20, true, '+',
            Color.BLUE);

    /** The OpenDoor item type. */
    public static final BlockType OpenDoor = new BlockType(10, false, '-',
            Color.YELLOW);

    /** The ClosedChest item type. */
    public static final BlockType ClosedChest = new BlockType(20, false, '[',
            Color.CYAN);

    /** The OpenChest item type. */
    public static final BlockType OpenChest = new BlockType(10, false, ']',
            Color.MAGENTA);

    /** The StairsUp item type. */
    public static final BlockType StairsUp = new BlockType(200, false, '<',
            Color.RED);

    /** The StairsDown item type. */
    public static final BlockType StairsDown = new BlockType(200, false, '>',
            Color.RED);

    /** The Anvil item type. */
    public static final BlockType Anvil = new BlockType(100, false, '=',
            Color.BLUE);

    /** The Trap item type. */
    public static final BlockType Trap = new BlockType(20, false, '^',
            Color.YELLOW);

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

    /** The display character color. */
    private final transient Color color;

    /**
     * Chooses one block type at random.
     * Note that Bedrock is excluded from the selection.
     */
    public static BlockType choose() {
        // We never generate bedrock!
        return BlockType.VALUES[Util.nextInt(BlockType.VALUES.length - 1) + 1];
    }

    /** Crates a Block type. */
    BlockType(final int theLife, final boolean theSolid, final char theDisplay,
            final Color theColor) {
        super(BlockType.class);
        Preconditions.checkArgument(theLife >= 0, "Life must be >= 0");
        life = theLife;
        solid = theSolid;
        display = theDisplay;
        color = Preconditions.checkNotNull(theColor);
    }

    /** Returns the display character color. */
    @Override
    public Color getColor() {
        return color;
    }

    /** Graphic representation of this block. */
    @Override
    public char getDisplay() {
        return display;
    }

    /**
     * Are blocks of this type damageable?
     */
    public boolean isDamageable() {
        return life != 0;
    }

    /**
     * Are blocks of this type solid?
     * A mobile cannot be located where a solid block is.
     */
    public boolean isSolid() {
        return solid;
    }

    /** Finalizes the initialization of an block of this type. */
    public Block postInit(final Block block) {
        Preconditions.checkNotNull(block);
        Preconditions.checkArgument(block.getType() == this);
        block.setLife((life == -1) ? -1 : life);
        if ((this == BlockType.ClosedChest) || (this == BlockType.OpenChest)) {
            final int count = Util.nextInt(2) + 1;
            Item[] content = block.getContent();
            for (int i = 0; i < count; i++) {
                content = (Item[]) ArrayUtils.add(content, Item.create());
            }
            block.setContent(content);
        }
        return block;
    }
}
