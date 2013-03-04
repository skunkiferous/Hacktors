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

/**
 * A Mobile controller tells the mobile what to do.
 *
 * It can be either some AI, or a player.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public interface MobileController {
    /** Avatar setter */
    void setMobile(final Mobile mobile);

    /** The controller can make the mobile do something. */
    void act();

    /** Informs the MobileController that it's position was updated. */
    void updatedPosition(final boolean changedLevel);

    /** Informs the MobileController that it's direction was updated. */
    void updatedDirection();

    /**
     * Informs the MobileController that new items have been picked-up.
     * @param array
     */
    void pickedUp(final Item[] array);

    /**
     * Informs the MobileController that damage was taken.
     * @param amount The amount of damage.
     * @param source The source of the damage (normally, a mobile)
     */
    void damaged(final int amount, final Object source);

    /**
     * Informs the MobileController that the mobile died.
     */
    void dead();

    /**
     * Informs the MobileController that the mobile attacked another mobile,
     * Optionally using some item as weapon.
     * @param other
     * @param item
     * @param killed
     */
    void attacked(final Mobile other, final Item item, final boolean killed);

    /**
     * Informs the MobileController that the mobile attacked a block,
     * Optionally using some item as weapon.
     * @param block
     * @param item
     * @param destroyed
     */
    void attacked(final Block block, final Item item, final boolean destroyed);

    /**
     * Informs the MobileController that this item has been added to the equipment.
     * @param theItem
     */
    void itemAdded(final Item theItem);

    /**
     * Informs the MobileController that this item has been removed from the equipment.
     * @param result
     */
    void itemRemoved(final Item theItem);

    /**
     * Informs the MobileController that this item has been eaten.
     * @param item
     */
    void ate(final Item item);

    /**
     * Informs the MobileController that the game is stopping.
     */
    void stop();
}
