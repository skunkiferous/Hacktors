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
 * Represents a block.
 *
 * Blocks can be solid, or they can let mobiles pass through.
 * Some blocks have a functionality, like doors, and stairs, and chests.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
@Data
public class Block {
    /** The immutable empty block. */
    public static final Block EMPTY = BlockType.Empty.postInit(new Block(
            BlockType.Empty));

    /** The immutable bedrock block. */
    public static final Block BEDROCK = BlockType.Bedrock.postInit(new Block(
            BlockType.Bedrock));

    /** The block type. */
    private final BlockType type;

    /** The life energy/hit point of this block. */
    private int life;

    /** The optional content of this block, if it is a container, like a chest. */
    private Item[] content = Item.EMPTY;

    /**
     * Optionally create a new block instance. If the type is null, it is
     * chosen at random.
     */
    public static Block create(final BlockType type, final float probability) {
        if (Util.nextFloat() < probability) {
            return null;
        }
        return create(type);
    }

    /**
     * Creates a new block instance. If the type is null, it is chosen at random.
     */
    public static Block create(final BlockType type) {
        final BlockType bt = (type == null) ? BlockType.choose() : type;
        if (bt == BlockType.Empty) {
            return EMPTY;
        }
        if (bt == BlockType.Bedrock) {
            return BEDROCK;
        }
        return bt.postInit(new Block(bt));
    }

    /** Creates a block at random. */
    public static Block create() {
        return create(null);
    }

    /**
     * This block has been damaged by the give amount.
     * Returns true if destroyed.
     * @param damage
     */
    public boolean damage(final int amount) {
        life -= amount;
        return (life <= 0);
    }

    /**
     * This block was destroyed. Returns the items that should be put in it's
     * place, if any.
     */
    public Item[] destroyed() {
        BlockType itemType = type;
        if (type == BlockType.OpenDoor) {
            itemType = BlockType.ClosedDoor;
        } else if (type == BlockType.Tree) {
            return new Item[] { Item.create(ItemType.Stick),
                    Item.create(ItemType.Stick), Item.create(ItemType.Apple),
                    Item.create(ItemType.Apple) };
        } else if ((type == BlockType.ClosedChest)
                || (type == BlockType.OpenChest)) {
            itemType = BlockType.OpenChest;
            if (content.length > 0) {
                final Item[] result = new Item[content.length + 1];
                System.arraycopy(content, 0, result, 0, content.length);
                result[content.length] = Item.create(ItemType.Block, itemType);
                return result;
            }
        }
        return new Item[] { Item.create(ItemType.Block, itemType) };
    }
}
