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

import com.google.common.base.Preconditions;

/**
 * The possible item types.
 *
 * TODO We need bow and arrow, or at least some projectile.
 * TODO Monster eggs
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public enum ItemType {
    Apple(1, 5, 1, 1, ItemCategory.Food), Meat(1, 20, 1, 1, ItemCategory.Food), Stick(
            20, 0, 5, 5, ItemCategory.Material), Bone(30, 0, 7, 7,
            ItemCategory.Material), PickAxe(100, 0, 15, 30, ItemCategory.Tool), Key(
            10, 0, 1, 1, ItemCategory.Tool), Sword(100, 0, 30, 15,
            ItemCategory.Weapon), Block(1, 0, 5, 5, ItemCategory.Material), Iron(
            100, 0, 10, 10, ItemCategory.Material), Helm(100, 0, 5, 5,
            ItemCategory.Armor), Gloves(100, 0, 7, 7, ItemCategory.Armor), Boots(
            100, 0, 7, 7, ItemCategory.Armor), Chestplate(100, 0, 5, 5,
            ItemCategory.Armor);

    /** An empty array of Item types. */
    public static final ItemType[] EMPTY = new ItemType[0];

    /** The "life" / hit points of an item of this type. */
    private final int life;

    /** The nutritional value of this item. */
    public final int food;

    /** The amount of damage that a hit from this item would do to mobiles. */
    public final int mobileDamage;

    /** The amount of damage that a hit from this item would do to blocks. */
    public final int blockDamage;

    /** The general category of this item. */
    public final ItemCategory category;

    /** Chooses one item type at random. */
    public static ItemType choose() {
        return values()[Util.RND.nextInt(values().length)];
    }

    /** Creates an item type. */
    private ItemType(final int theLife, final int theFood,
            final int theMobileDamage, final int theBlockDamage,
            final ItemCategory theCategory) {
        Preconditions.checkArgument(theLife >= 0, "Life must be >= 0");
        Preconditions.checkArgument(theFood >= 0, "Food must be >= 0");
        Preconditions.checkArgument(theMobileDamage >= 0,
                "MobileDamage must be >= 0");
        Preconditions.checkArgument(theBlockDamage >= 0,
                "BlockDamage must be >= 0");
        life = theLife;
        food = theFood;
        mobileDamage = theMobileDamage;
        blockDamage = theBlockDamage;
        category = Preconditions.checkNotNull(theCategory);
    }

    /** Finalizes the initialization of an item of this type. */
    public Item postInit(final Item item) {
        Preconditions.checkNotNull(item);
        Preconditions.checkArgument(item.getType() == this);
        item.setLife((life == 1) ? 1 : Util.RND.nextInt(life) + 1);
        return item;
    }

    /** Is this some kind of food? */
    public boolean food() {
        return (category == ItemCategory.Food);
    }

    /** Is this some kind of armor? */
    public boolean armor() {
        return (category == ItemCategory.Armor);
    }

    /** Is this some kind of tool? */
    public boolean tool() {
        return (category == ItemCategory.Tool);
    }

    /** Is this some kind of weapon? */
    public boolean weapon() {
        return (category == ItemCategory.Weapon);
    }
}
