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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.blockwithme.util.Enum40;
import com.google.common.base.Preconditions;

/**
 * The possible item types.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class ItemType extends Enum40<ItemType> {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 72657509122225609L;

    // CHECKSTYLE.OFF: ConstantName
    /** Apples! */
    public static final ItemType Apple = new ItemType(1, 5, 1, 1,
            ItemCategory.Food);
    /** Meat (most likely, pig meat) */
    public static final ItemType Meat = new ItemType(1, 20, 1, 1,
            ItemCategory.Food);

    /** Egg of a pig */
    public static final ItemType PigEgg = new ItemType(1, 5, 1, 1,
            ItemCategory.Egg);
    /** Egg of a human */
    public static final ItemType HumanEgg = new ItemType(1, 5, 1, 1,
            ItemCategory.Egg);
    /** Egg of a zombie */
    public static final ItemType ZombieEgg = new ItemType(1, 5, 1, 1,
            ItemCategory.Egg);
    /** Egg of a dog */
    public static final ItemType DogEgg = new ItemType(1, 5, 1, 1,
            ItemCategory.Egg);

    /** Sticks */
    public static final ItemType Stick = new ItemType(20, 0, 5, 5,
            ItemCategory.Material);
    /** Bones */
    public static final ItemType Bone = new ItemType(30, 0, 7, 7,
            ItemCategory.Material);
    /** Some kind of block */
    public static final ItemType Block = new ItemType(1, 0, 5, 5,
            ItemCategory.Material);
    /** Iron (used to make weapons, ...) */
    public static final ItemType Iron = new ItemType(100, 0, 10, 10,
            ItemCategory.Material);

    /** A pick-axe, to break stone. */
    public static final ItemType PickAxe = new ItemType(100, 0, 15, 30,
            ItemCategory.Tool);
    /** A key, to open doors and chests. */
    public static final ItemType Key = new ItemType(10, 0, 1, 1,
            ItemCategory.Tool);

    /** A sword against zombies. */
    public static final ItemType Sword = new ItemType(100, 0, 30, 15,
            ItemCategory.Weapon);
    /** A throwing dagger, also against zombies. */
    public static final ItemType ThrowingDagger = new ItemType(30, 0, 15, 10,
            ItemCategory.Weapon, 5);

    /** An armored helm. */
    public static final ItemType Helm = new ItemType(100, 0, 5, 5,
            ItemCategory.Armor);
    /** An armored pair of gloves. */
    public static final ItemType Gloves = new ItemType(100, 0, 7, 7,
            ItemCategory.Armor);
    /** An armored pair of boots. */
    public static final ItemType Boots = new ItemType(100, 0, 7, 7,
            ItemCategory.Armor);
    /** An armor chest plate */
    public static final ItemType Chestplate = new ItemType(100, 0, 5, 5,
            ItemCategory.Armor);
    // CHECKSTYLE.ON: ConstantName

    /** All Item types. */
    public static final ItemType[] ALL_SET = Enum40.values(ItemType.class);

    /** All craftable Item types. */
    public static final ItemType[] CRAFTABLE = findCraftable();

    /** An empty array of Item types. */
    public static final ItemType[] EMPTY = new ItemType[0];

    /** The "life" / hit points of an item of this type. */
    private final int life;

    /** The nutritional value of this item. */
    private final int food;

    /** The missile range of this item, if any. */
    private final int range;

    /** The amount of damage that a hit from this item would do to mobiles. */
    private final int mobileDamage;

    /** The amount of damage that a hit from this item would do to blocks. */
    private final int blockDamage;

    /** The general category of this item. */
    private final ItemCategory category;

    /** The nutritional value of this item. */
    public int getFood() {
        return food;
    }

    /** The missile range of this item, if any. */
    public int getRange() {
        return range;
    }

    /** The amount of damage that a hit from this item would do to mobiles. */
    public int getMobileDamage() {
        return mobileDamage;
    }

    /** The amount of damage that a hit from this item would do to blocks. */
    public int getBlockDamage() {
        return blockDamage;
    }

    /** The general category of this item. */
    public ItemCategory getCategory() {
        return category;
    }

    /** Chooses one item type at random. */
    public static ItemType choose() {
        return ALL_SET[Util.RND.nextInt(ALL_SET.length)];
    }

    /** Chooses one craftable item type at random. */
    public static ItemType chooseCraftable() {
        return CRAFTABLE[Util.RND.nextInt(CRAFTABLE.length)];
    }

    /** Creates an item type. */
    protected ItemType(final int theLife, final int theFood,
            final int theMobileDamage, final int theBlockDamage,
            final ItemCategory theCategory) {
        this(ItemType.class, theLife, theFood, theMobileDamage, theBlockDamage,
                theCategory, 0);
    }

    /** Creates an item type. */
    protected ItemType(final int theLife, final int theFood,
            final int theMobileDamage, final int theBlockDamage,
            final ItemCategory theCategory, final int theRange) {
        this(ItemType.class, theLife, theFood, theMobileDamage, theBlockDamage,
                theCategory, theRange);
    }

    /** Creates an item type. */
    protected ItemType(final Class<? extends ItemType> type, final int theLife,
            final int theFood, final int theMobileDamage,
            final int theBlockDamage, final ItemCategory theCategory,
            final int theRange) {
        super(type);
        Preconditions.checkArgument(theLife >= 0, "Life must be >= 0");
        Preconditions.checkArgument(theFood >= 0, "Food must be >= 0");
        Preconditions.checkArgument(theMobileDamage >= 0,
                "MobileDamage must be >= 0");
        Preconditions.checkArgument(theBlockDamage >= 0,
                "BlockDamage must be >= 0");
        life = theLife;
        food = theFood;
        range = theRange;
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

    /** Is this some kind of missile weapon (or an egg)? */
    public boolean missile() {
        return (this == ThrowingDagger) || (category == ItemCategory.Egg);
    }

    /** Is this some kind of craftable item? */
    public boolean craftable() {
        return armor() || weapon() || tool();
    }

    /**
     * Finds an returns all craftable item types.
     * @return
     */
    private static ItemType[] findCraftable() {
        final List<ItemType> list = new ArrayList<>();
        for (final ItemType it : ALL_SET) {
            if (it.craftable()) {
                list.add(it);
            }
        }
        return list.toArray(new ItemType[list.size()]);
    }

    /** Spawns an appropriate mobile, if possible. */
    public Mobile spawn() {
        if (this == PigEgg) {
            return Mobile.create(MobileType.Pig);
        }
        if (this == HumanEgg) {
            return Mobile.create(MobileType.Human);
        }
        if (this == ZombieEgg) {
            return Mobile.create(MobileType.Zombie);
        }
        if (this == DogEgg) {
            return Mobile.create(MobileType.Dog);
        }
        return null;
    }
}
