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

import org.apache.commons.lang.ArrayUtils;

import com.google.common.base.Preconditions;

/**
 * Represents some creature (including players) in the game.
 *
 * It only defines the physical manifestation, without defining the "mind" of
 * the creature, which must be implemented as a MobileController.
 *
 * TODO: Add "speed"
 * TODO: Add "fears"
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
@Data
public class Mobile {
    /** Empty array of mobiles. */
    public static final Mobile[] EMPTY = new Mobile[0];

    /** The maximum number of pieces of equipment. */
    public static final int MAX_ITEMS = 10;

    /** The mobile controller; the "brains" of a mobile. */
    private final MobileController controller;

    /** The type of mobile. */
    private final MobileType type;

    /**
     * The life energy / hit points, of a mobile.
     * Reaching zero means death/destruction.
     */
    private int life;

    /**
     * The current position of the mobile in the world. If the world field of
     * the position is null, it then means that the mobile is currently
     * "detached".
     */
    private final Position position = new Position();

    /** The equipment (and "droppings") currently carried by the mobile. */
    private Item[] equipment = Item.EMPTY;

    /** Optionally creates a specific mobile, depending on chance. */
    public static Mobile create(final MobileController theController,
            final MobileType type, final float probability) {
        if (Util.RND.nextFloat() < probability) {
            return null;
        }
        return type.postInit(new Mobile(theController, type));
    }

    /** Creates a specific mobile. */
    public static Mobile create(final MobileController theController,
            final MobileType type) {
        return type.postInit(new Mobile(theController, type));
    }

    /** Creates a random mobile. */
    public static Mobile create() {
        final MobileType mobileType = MobileType.choose();
        final MobileController controller = MobileControllers
                .defaultControllerFor(mobileType);
        return create(controller, mobileType);
    }

    /** Returns the mobiles carried items; it's equipment. */
    public int getItems() {
        return equipment.length;
    }

    /** Tries to find some equipment of the mobile. */
    public int findItem(final Item theItem) {
        return ArrayUtils.indexOf(equipment, theItem);
    }

    /** Tries to find some equipment of the mobile, by type. */
    public int findItemByType(final ItemType itemType) {
        for (int i = 0; i < equipment.length; i++) {
            final Item e = equipment[i];
            if (e.getType() == itemType) {
                return i;
            }
        }
        return -1;
    }

    /** Tries to find some equipment of the mobile, by category. */
    public int findItemByCategory(final ItemCategory category) {
        for (int i = 0; i < equipment.length; i++) {
            final Item e = equipment[i];
            if (e.getType().category == category) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Tries to add some equipment to the mobile.
     * Returns false on failure.
     */
    public boolean addItem(final Item theItem) {
        Preconditions.checkNotNull(theItem);
        if (getItems() == MAX_ITEMS) {
            // 10 is maximum items
            return false;
        }
        if (ArrayUtils.contains(equipment, theItem)) {
            // Already in equipment ...
            return false;
        }
        equipment = (Item[]) ArrayUtils.add(equipment, theItem);
        return true;
    }

    /**
     * Removes some equipment to the mobile.
     */
    public Item removeItem(final int index) {
        final Item result = equipment[index];
        equipment = (Item[]) ArrayUtils.remove(equipment, index);
        return result;
    }

    /**
     * Returns some equipment to the mobile.
     */
    public Item getItem(final int index) {
        return equipment[index];
    }

    /** Informs the Mobile that it's position was updated. */
    public void updatedPosition() {
        // NOP
    }
}
