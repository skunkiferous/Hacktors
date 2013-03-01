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

import com.blockwithme.util.Enum40;
import com.google.common.base.Preconditions;

/**
 * The possible item types.
 *
 * TODO We need bow and arrow, or at least some projectile. TODO Monster eggs
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class ItemType extends Enum40<ItemType> {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 72657509122225609L;

	public static final ItemType Apple = new ItemType(1, 5, 1, 1,
			ItemCategory.Food);
	public static final ItemType Meat = new ItemType(1, 20, 1, 1,
			ItemCategory.Food);
	public static final ItemType Stick = new ItemType(20, 0, 5, 5,
			ItemCategory.Material);
	public static final ItemType Bone = new ItemType(30, 0, 7, 7,
			ItemCategory.Material);
	public static final ItemType PickAxe = new ItemType(100, 0, 15, 30,
			ItemCategory.Tool);
	public static final ItemType Key = new ItemType(10, 0, 1, 1,
			ItemCategory.Tool);
	public static final ItemType Sword = new ItemType(100, 0, 30, 15,
			ItemCategory.Weapon);
	public static final ItemType Block = new ItemType(1, 0, 5, 5,
			ItemCategory.Material);
	public static final ItemType Iron = new ItemType(100, 0, 10, 10,
			ItemCategory.Material);
	public static final ItemType Helm = new ItemType(100, 0, 5, 5,
			ItemCategory.Armor);
	public static final ItemType Gloves = new ItemType(100, 0, 7, 7,
			ItemCategory.Armor);
	public static final ItemType Boots = new ItemType(100, 0, 7, 7,
			ItemCategory.Armor);
	public static final ItemType Chestplate = new ItemType(100, 0, 5, 5,
			ItemCategory.Armor);

	/** All Item types. */
	public static final ItemType[] ALL_SET = Enum40.values(ItemType.class);

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
		return ALL_SET[Util.RND.nextInt(ALL_SET.length)];
	}

	/** Creates an item type. */
	protected ItemType(final int theLife, final int theFood,
			final int theMobileDamage, final int theBlockDamage,
			final ItemCategory theCategory) {
		this(ItemType.class, theLife, theFood, theMobileDamage, theBlockDamage,
				theCategory);
	}

	/** Creates an item type. */
	protected ItemType(final Class<? extends ItemType> type, final int theLife,
			final int theFood, final int theMobileDamage,
			final int theBlockDamage, final ItemCategory theCategory) {
		super(type);
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
