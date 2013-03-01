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
 * The possible mobile (players, monsters) types.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class MobileType extends Enum40<MobileType> {
    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3941589849195137035L;

	public static final MobileType Pig
		= new MobileType(20, MobileType.EMPTY, new ItemType[] { ItemType.Apple }, ItemType.Meat, ItemType.Meat, ItemType.Meat);
    public static final MobileType Human
    	= new MobileType(100, new MobileType[] { Pig }, new ItemType[] { ItemType.Apple, ItemType.Meat }, null, null, null);
    public static final MobileType Zombie
    	= new MobileType(100, new MobileType[] { Human }, ItemType.EMPTY, null, ItemType.Bone, ItemType.Bone);
    public static final MobileType Dog
		= new MobileType(50, new MobileType[] { Zombie }, new ItemType[] { ItemType.Meat }, ItemType.Bone);

    /** All MobileTypes. */
    public static final MobileType[] ALL_SET = Enum40.values(MobileType.class);

    /** Reusable empty array of MobileType. */
    public static final MobileType[] EMPTY = new MobileType[0];

    /** Probability of items being dropped, per item type. */
    private static final float PROBABILITY = 0.2f;

    /** Maximum life a mobile can have, when created. */
    private final int life;

    /** Types of possible dropping for a mobile to this type. */
    private final ItemType[] droppings;

    /** Types of food that this mobile eats. */
    public final ItemType[] food;

    /** The other mobiles type that it hunts/attacks on sight. */
    public final MobileType[] hunts;

    /** The other mobiles type that it runs away from. */
    private volatile MobileType[] fears = EMPTY;

    /** The other mobiles type that it runs away from. */
    public MobileType[] fears() {
    	return fears;
    }

    /** Chooses one mobile type at random. */
    public static MobileType choose() {
        return ALL_SET[Util.RND.nextInt(ALL_SET.length)];
    }

    /** Constructor */
    protected MobileType(final int theLife, final MobileType[] theHunts,
            final ItemType[] theFood, final ItemType... theDroppings) {
        this(MobileType.class, theLife, theHunts, theFood, theDroppings);
    }

    /** Constructor */
    protected MobileType(final Class<? extends MobileType> type, final int theLife, final MobileType[] theHunts,
            final ItemType[] theFood, final ItemType... theDroppings) {
    	super(type);
        Preconditions.checkArgument(theLife > 0, "Life must be > 0");
        life = theLife;
        hunts = Util.checkNotNull(theHunts);
        food = Util.checkNotNull(theFood);
        // theDroppings can contain null
        droppings = Preconditions.checkNotNull(theDroppings);
        MobileType[] _fears = new MobileType[0];
//        if (this == Human) {
//        	_fears = new MobileType[] { Zombie };
//        }
        fears = _fears;
    }

    /**
     * Finalize the initialization of a Mobile, based on it's type.
     * In particular, it's life points and droppings are computed.
     */
    public Mobile postInit(final Mobile mobile) {
        Preconditions.checkNotNull(mobile);
        Preconditions.checkArgument(mobile.getType() == this);
        mobile.setLife(1 + (int) (Util.RND.nextGaussian() * life));
        for (int i = 0; i < droppings.length; i++) {
            final ItemType itemType = droppings[i];
            final Item dropping;
            if (itemType == null) {
                dropping = Item.create(PROBABILITY);
            } else {
                dropping = Item.create(itemType, PROBABILITY);
            }
            if (dropping != null) {
                mobile.addItem(dropping);
            }
        }
        return mobile;
    }

	/**
	 * If implemented by the "additional data", the postInit() method will be
	 * called after the Enum40 values were fully initialized.
	 */
    @Override
	protected void postInit(final MobileType[] allSet) {
		if (this == Pig) {
			fears = new MobileType[] { Dog };
		} else if (this == Human) {
			fears = new MobileType[] { Zombie };
		}
	}
}
