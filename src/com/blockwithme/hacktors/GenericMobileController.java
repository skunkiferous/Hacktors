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

import lombok.Data;

/**
 * Generic MobileController for monsters.
 *
 * It makes all monsters behave the same except dependent on the food
 * preferences, health, ...
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class GenericMobileController extends AbstractMobileController {

    @Data
    private class ItemPos {
        public final Item item;
        public final int x;
        public final int y;
    }

    /** Mobiles are scared if they have less then that much life. */
    private static final float MIN_LIFE = 0.333f;

    /** Mobiles are scared if they have less then that much life, and are armed. */
    private static final float MIN_LIFE_ARMED = 0.20f;

    /** Mobiles are hungry if they have less then that much life. */
    private static final float HUNGRY = 0.75f;

    /** Returns ture, if stuff is a suitable food. */
    private static boolean food(final ItemType[] foodTypes, final Item stuff) {
        final ItemType type = stuff.getType();
        for (final ItemType food : foodTypes) {
            if (type == food) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#act(com.blockwithme.hacktors.Mobile)
     */
    @Override
    public void act(final Mobile mobile) {
        final World world = mobile.getPosition().getWorld();
        if (world == null) {
            return;
        }
        final MobileType type = mobile.getType();
        final Mobile attacker = mobile.getLastAttacker();
        final int life = mobile.getLife();
        final int maxLife = type.getLife();
        final boolean mindless = mobile.isMindless();
        final boolean hungry = (life < (int) (maxLife * HUNGRY));
        boolean hasFood = false;
        final ItemType[] foodTypes = mobile.getType().getFood();
        for (final ItemType food : foodTypes) {
            if (mobile.findItemByType(food) >= 0) {
                hasFood = true;
            }
        }
        boolean hasWeapon = false;
        final Item[] stuff = mobile.getEquipment();
        if (type.isToolUser()) {
            for (final Item thing : stuff) {
                if (thing.getType().weapon()) {
                    hasWeapon = true;
                }
            }
        }
        final float scaredLimit = hasWeapon ? MIN_LIFE_ARMED : MIN_LIFE;
        final boolean scared = !mindless && (attacker != null)
                && (life <= (int) (maxLife * scaredLimit));
        final Position pos = mobile.getPosition();
        final int perception = type.getPerception();
        final int x = pos.getX();
        final int y = pos.getY();
        final int xMin = x - perception;
        final int xMax = x + perception;
        final int yMin = y - perception;
        final int yMax = y + perception;
        final Level level = world.getOrCreateLevel(pos.getZ());
        final List<Mobile> ennemies = new ArrayList<>();
        final List<ItemPos> foods = new ArrayList<>();
        final List<ItemPos> wants = new ArrayList<>();
        for (int px = xMin; px <= xMax; px++) {
            pos.setX(px);
            for (int py = yMin; py <= yMax; py++) {
                pos.setY(py);
                if (world.isValid(pos)) {
                    final boolean isMe = (px == x) && (py == y);
                    final Chunk chunk = level.getChunkOf(px, py);
                    final Mobile mob = isMe ? null : chunk.getMobile(px, py);
                    if (mob != null) {
                        if (mob == attacker) {
                            ennemies.add(mob);
                        } else {
                            for (final MobileType scary : type.fears()) {
                                if (mob.getType() == scary) {
                                    ennemies.add(mob);
                                }
                            }
                        }
                    }
                    for (final Item item : chunk.getItems(px, py)) {
                        if (food(foodTypes, item)) {
                            foods.add(new ItemPos(item, px, py));
                        } else if (type.isToolUser()) {
                            // Tool user wants everything!
                            wants.add(new ItemPos(item, px, py));
                        }
                    }
                    //final Block block = chunk.getBlock(px, py);
                }
            }
        }
        act(mobile, hungry, hasFood, hasWeapon, scared, ennemies, foods, wants);
    }

    private static boolean free(final World world, final Position pos) {
        final Level level = world.getOrCreateLevel(pos.getZ());
        boolean ok = false;
        if (world.isValid(pos)) {
            final int x = pos.getX();
            final int y = pos.getX();
            final Chunk chunk = level.getChunkOf(x, y);
            ok = !chunk.occupied(x, y);
        }
        return ok;
    }

    protected boolean tryMove(final World world, final Mobile mobile,
            Direction direction) {
        final Position pos = mobile.getPosition();
        pos.setDirection(direction);
        Position next = pos.next();
        boolean move = true;
        if (!free(world, next)) {
            final Direction[] alternatives = direction.normal();
            direction = alternatives[0];
            pos.setDirection(direction);
            next = pos.next();
            if (!free(world, next)) {
                direction = alternatives[1];
                pos.setDirection(direction);
                next = pos.next();
                if (!free(world, next)) {
                    // Stuck on all sides!
                    move = false;
                }
            }
        }
        if (move) {
            if (mobile.move()) {
                return true;
            }
        }
        return false;
    }

    protected void act(final Mobile mobile, final boolean hungry,
            final boolean hasFood, final boolean hasWeapon,
            final boolean scared, final List<Mobile> ennemies,
            final List<ItemPos> foods, final List<ItemPos> wants) {
        final World world = mobile.getPosition().getWorld();
        final Position pos = mobile.getPosition();
        Direction direction = pos.getDirection();
        if (scared && !ennemies.isEmpty()) {
            Mobile nearest = null;
            float distance = -1.0f;

            for (final Mobile ennemy : ennemies) {
                final float dst = pos.distance(ennemy.getPosition());
                if (dst > distance) {
                    distance = dst;
                    nearest = ennemy;
                }
            }

            direction = pos.awayFrom(nearest.getPosition());
            if (tryMove(world, mobile, direction)) {
                return;
            }
            // Can't run! Maybe backtrack?
            direction = direction.opposite();
            if (tryMove(world, mobile, direction)) {
                return;
            }
            // OK. We're in trouble!
            if (hasFood) {
                // Life a bit longer .. get lucky on next cycle?
                while (mobile.eat()) {
                    // NOP
                }
                return;
            }
        }
        if (!ennemies.isEmpty()) {
            for (final Mobile ennemy : ennemies) {
                final Position epos = ennemy.getPosition();
                if (pos.distance(epos) < 1.05f) {
                    direction = pos.towards(epos);
                    pos.setDirection(direction);
                    if (mobile.attack()) {
                        return;
                    }
                    // We really should not get here ...
                }
            }
        }
        if (hungry && hasFood) {
            while (mobile.eat()) {
                // NOP
            }
            return;
        }
        // No high priority goals; is there anything we want that is near?
        ItemPos nearest = null;
        float distance = -1.0f;
        for (final ItemPos ip : foods) {
            final float dst = pos.distance(ip.x, ip.y);
            if (dst > distance) {
                distance = dst;
                nearest = ip;
            }
        }
        for (final ItemPos ip : wants) {
            final float dst = pos.distance(ip.x, ip.y);
            if (dst > distance) {
                distance = dst;
                nearest = ip;
            }
        }
        if (distance < 0.05f) {
            mobile.pickup();
        } else if (nearest != null) {
            direction = pos.towards(nearest.x, nearest.y);
        }
        tryMove(world, mobile, direction);
    }
}
