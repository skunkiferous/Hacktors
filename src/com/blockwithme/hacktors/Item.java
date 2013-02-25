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
 * An item, that can be found and used in the game.
 *
 * Most things of another type, like blocks,
 * can have an "item form".
 *
 * TODO Expend to support mobile type, in addition to block type.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
@Data
public class Item {
    /** EMpty array of items. */
    public static final Item[] EMPTY = new Item[0];

    /** The type of the item. */
    private final ItemType type;

    /** The block type of the item, when it is a block item. */
    private final BlockType blockType;

    /** The life energy/hit point of this item. */
    private int life;

    /**
     * Creates a new item, with the given type and block type.
     * If type is null, then it will be chosen at random. In that case, if
     * it happens to be a block item, then the block type will also be
     * chosen at random. Otherwise, if the type is given, then the
     * value of the block type must be appropriate to the item type.
     */
    public static Item create(final ItemType type, final BlockType blockType) {
        final ItemType it;
        final BlockType bt;
        if (type == null) {
            it = ItemType.choose();
            bt = (it == ItemType.Block) ? BlockType.choose() : null;
        } else if (type == ItemType.Block) {
            if (blockType == null) {
                throw new IllegalArgumentException(
                        "Block Item requires BlockType");
            }
            it = type;
            bt = blockType;
        } else {
            if (blockType != null) {
                throw new IllegalArgumentException(
                        "Non-Block Item cannot have BlockType");
            }
            it = type;
            bt = blockType;
        }
        return type.postInit(new Item(it, bt));
    }

    /** Creates a new item, with the given type and no block type. */
    public static Item create(final ItemType type) {
        return create(type, null);
    }

    /** Optionally creates a new item, with the given type and block type. */
    public static Item create(final ItemType type, final BlockType blockType,
            final float probability) {
        if (Util.RND.nextFloat() < probability) {
            return null;
        }
        return create(type, blockType);
    }

    /** Optionally creates a new item, with the given type and no block type. */
    public static Item create(final ItemType type, final float probability) {
        return create(type, null, probability);
    }

    /** Creates a new item randomly. */
    public static Item create() {
        return create(null);
    }

    /** Optionally creates a new item randomly. */
    public static Item create(final float probability) {
        if (Util.RND.nextFloat() < probability) {
            return null;
        }
        return create();
    }

    /**
     * Uses an item once. This consumes one life point. As a result, the item
     * might get destroyed.
     */
    public void use() {
        if (destroyed()) {
            throw new IllegalStateException("Item already destroyed");
        }
        life--;
    }

    /** Returns true if this item has been destroyed. */
    public boolean destroyed() {
        return life == 0;
    }
}
