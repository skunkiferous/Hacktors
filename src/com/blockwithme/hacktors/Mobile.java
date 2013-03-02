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

    /** Creates a specific mobile type. */
    public static Mobile create(final MobileType type) {
        final MobileController controller = MobileControllers
                .defaultControllerFor(type);
        return type.postInit(new Mobile(controller, type));
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
            if (e.getType().getCategory() == category) {
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
        controller.itemRemoved(result);
        return result;
    }

    /**
     * Returns some equipment to the mobile.
     */
    public Item getItem(final int index) {
        return equipment[index];
    }

    /** Informs the Mobile that it's position was updated. */
    public void updatedPosition(final boolean changedLevel) {
        controller.updatedPosition(changedLevel);
    }

    /** Returns the current mobile chunk, if connected to a world. */
    public Chunk getChunk() {
        final World world = position.getWorld();
        return (world == null) ? null : world.getOrCreateChunk(position);
    }

    /**
     * Inflict the amount of damage specified. Returns true if killed.
     */
    private boolean damageImpl(final int amount, final Object source) {
        int damage = amount;
        int i = 0;
        while (i < equipment.length) {
            final Item item = equipment[i];
            if (item.getType().armor()) {
                final int armorLife = item.getLife();
                if (armorLife > damage) {
                    item.setLife(armorLife - damage);
                    damage = 0;
                    i = equipment.length;
                } else {
                    item.setLife(0);
                    damage -= armorLife;
                    removeItem(i--);
                }
            }
            i++;
        }
        if (damage > 0) {
            life -= damage;
            controller.damaged(damage, source);
            if (isDead()) {
                controller.dead();
                getChunk().setMobile(position.getX(), position.getY(), null);
                return true;
            }
        }
        return false;
    }

    /** Inflict the amount of damage specified, cause by some mobile.
     * Returns true if killed.
     * @param item */
    public boolean damage(final int amount, final Mobile source, final Item item) {
        // TODO Currently ignoring item...
        return damageImpl(amount, source);
    }

    /** Inflict the amount of damage specified, cause by some block, normally a trap.
     * Returns true if killed. */
    public boolean damage(final int amount, final Block source) {
        return damageImpl(amount, source);
    }

    /** Inflict the amount of damage specified, cause by some missile.
     * Returns true if killed. */
    public boolean damage(final Item missile) {
        return damageImpl(missile.getType().getMobileDamage(), missile);
    }

    /** Returns true, if the mobile is now dead/destroyed. */
    public boolean isDead() {
        return life <= 0;
    }

    /** Returns true, if the mobile is now alive. */
    public boolean isAlive() {
        return life > 0;
    }

    /** Moves the Mobile one position in the current direction, if possible. */
    public boolean move() {
        final World world = position.getWorld();
        if (world == null) {
            // Detached
            return false;
        }
        final Position next = position.next();
        final Chunk chunk = world.getOrCreateChunk(next);
        if (chunk == null) {
            // At worlds edge
            return false;
        }
        final int x = next.getX();
        final int y = next.getY();
        if (chunk.occupied(x, y)) {
            // Next position is not empty.
            return false;
        }
        // OK
        position.setX(x);
        position.setY(y);
        controller.updatedPosition(false);
        final Block block = chunk.getBlock(x, y);
        if (block.getType() == BlockType.Trap) {
            damage(BlockType.TRAP_DAMAGE, block);
        }
        return true;
    }

    /** Changes the current direction. */
    public boolean turn(final Direction theDirection) {
        position.setDirection(theDirection);
        controller.updatedDirection();
        // Turn always succeeds
        return true;
    }

    /** Picks up stuff on the floor, until all is picked up, or the inventory is full. */
    public boolean pickup() {
        final World world = position.getWorld();
        if (world == null) {
            // Detached
            return false;
        }
        final Chunk chunk = world.getOrCreateChunk(position);
        if (chunk == null) {
            // At worlds edge
            return false;
        }
        final int x = position.getX();
        final int y = position.getY();
        Item[] items = chunk.getItems(x, y);
        boolean result = false;
        for (int i = 0; i < items.length; i++) {
            final Item item = items[i];
            if (addItem(item)) {
                items = (Item[]) ArrayUtils.remove(items, i--);
                chunk.removeItem(x, y, item);
                result = true;
            }
        }
        final Block block = chunk.getBlock(x, y);
        final BlockType blockType = block.getType();
        if (blockType == BlockType.OpenChest) {
            items = block.getContent();
            for (int i = 0; i < items.length; i++) {
                final Item item = items[i];
                if (addItem(item)) {
                    items = (Item[]) ArrayUtils.remove(items, i--);
                    result = true;
                }
            }
            block.setContent(items);
        }
        return result;
    }

    /** Attacks a Mobile. */
    private void attack(final Mobile other) {
        int best = -1;
        int damage = type.getDamage();
        for (int i = 0; i < equipment.length; i++) {
            final int dmg = equipment[i].getType().getMobileDamage();
            if ((best == -1) || (damage < dmg)) {
                best = i;
                damage = dmg;
            }
        }
        if (best == -1) {
            // Use bare hands/claws/...!
            final boolean killed = other.damage(damage, this, null);
            controller.attacked(other, null, killed);
        } else {
            // Attack with item 'best'
            final Item item = equipment[best];
            final boolean killed = other.damage(damage, this, item);
            controller.attacked(other, item, killed);
            if (item.use()) {
                removeItem(best);
            }
        }
    }

    /** Attacks a Block. */
    private void attack(final Block block, final Position next,
            final Chunk chunk) {
        int best = -1;
        int damage = type.getDamage();
        for (int i = 0; i < equipment.length; i++) {
            final int dmg = equipment[i].getType().getBlockDamage();
            if ((best == -1) || (damage < dmg)) {
                best = i;
                damage = dmg;
            }
        }
        final boolean destroyed = block.damage(damage);
        if (best == -1) {
            // Use bare hands/claws/...!
            controller.attacked(block, null, destroyed);
        } else {
            // Attack with item 'best'
            final Item item = equipment[best];
            controller.attacked(block, item, destroyed);
            if (item.use()) {
                removeItem(best);
            }
        }
        if (destroyed) {
            final int x = next.getX();
            final int y = next.getY();
            chunk.setBlock(x, y, Block.EMPTY);
            for (final Item item : block.destroyed()) {
                chunk.addItem(x, y, item);
            }
        }
    }

    /** Attacks one position ahead in the current direction, if possible. */
    public boolean attack() {
        final World world = position.getWorld();
        if (world == null) {
            // Detached
            return false;
        }
        final Position next = position.next();
        final Chunk chunk = world.getOrCreateChunk(next);
        if (chunk == null) {
            // At worlds edge
            return false;
        }
        final int x = next.getX();
        final int y = next.getY();
        final Mobile other = chunk.getMobile(x, y);
        if (other != null) {
            attack(other);
            return true;
        }
        final Block block = chunk.getBlock(x, y);
        if (block.getType().isDamageable()) {
            attack(block, next, chunk);
            return true;
        }
        return false;
    }

    /** Eats something, if possible. */
    public boolean eat() {
        for (int i = 0; i < equipment.length; i++) {
            final Item item = equipment[i];
            if (item.getType().food()) {
                removeItem(i);
                life += item.getLife();
                controller.ate(item);
                return true;
            }
        }
        return false;
    }

    /** Attack using a missile weapon, if possible. */
    public boolean fire() {
        final World world = position.getWorld();
        if (world != null) {
            for (int i = 0; i < equipment.length; i++) {
                final Item item = equipment[i];
                if (item.getType().missile()) {
                    removeItem(i);
                    world.getOrCreateLevel(position.getZ()).handleMissile(item,
                            position.getX(), position.getY(),
                            position.getDirection());
                    return true;
                }
            }
        }
        return false;
    }

    /** Drops some piece of equipment, if possible. */
    public boolean drop(final int index) {
        final Chunk chunk = getChunk();
        if ((chunk != null) && (index >= 0) && (index < equipment.length)) {
            final Item item = removeItem(index);
            chunk.addItem(position.getX(), position.getY(), item);
            return true;
        }
        return false;
    }

    /** Open the openable door/chest in front of the mobile, if possible. */
    public boolean open() {
        final World world = position.getWorld();
        if (world == null) {
            // Detached
            return false;
        }
        final Position next = position.next();
        final Chunk chunk = world.getOrCreateChunk(next);
        if (chunk == null) {
            // At worlds edge
            return false;
        }
        final int x = next.getX();
        final int y = next.getY();
        final Block block = chunk.getBlock(x, y);
        final BlockType blockType = block.getType();
        final boolean door = (blockType == BlockType.ClosedDoor);
        final boolean chest = (blockType == BlockType.ClosedChest);
        if (door || chest) {
            for (int i = 0; i < equipment.length; i++) {
                final Item item = equipment[i];
                if (item.getType() == ItemType.Key) {
                    if (item.use()) {
                        removeItem(i);
                    }
                    if (door) {
                        chunk.setBlock(x, y, new Block(BlockType.OpenDoor));
                    } else {
                        // chest ...
                        for (final Item loot : block.getContent()) {
                            chunk.addItem(x, y, loot);
                        }
                        final Block newChest = new Block(BlockType.OpenChest);
                        newChest.setContent(block.getContent());
                        chunk.setBlock(x, y, newChest);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /** Closes the closeable door/chest in front of the mobile, if possible. */
    public boolean close() {
        final World world = position.getWorld();
        if (world == null) {
            // Detached
            return false;
        }
        final Position next = position.next();
        final Chunk chunk = world.getOrCreateChunk(next);
        if (chunk == null) {
            // At worlds edge
            return false;
        }
        final int x = next.getX();
        final int y = next.getY();
        final Block block = chunk.getBlock(x, y);
        final BlockType blockType = block.getType();
        final boolean door = (blockType == BlockType.OpenDoor);
        final boolean chest = (blockType == BlockType.OpenChest);
        if (door || chest) {
            for (int i = 0; i < equipment.length; i++) {
                final Item item = equipment[i];
                if (item.getType() == ItemType.Key) {
                    if (item.use()) {
                        removeItem(i);
                    }
                    if (door) {
                        chunk.setBlock(x, y, new Block(BlockType.ClosedDoor));
                    } else {
                        // chest ...
                        final Block newChest = new Block(BlockType.ClosedChest);
                        newChest.setContent(block.getContent());
                        chunk.setBlock(x, y, newChest);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /** Tries to go up, if possible. */
    public boolean goUp() {
        final World world = position.getWorld();
        if (world == null) {
            // Detached
            return false;
        }
        Chunk chunk = getChunk();
        final int x = position.getX();
        final int y = position.getY();
        final Block block = chunk.getBlock(x, y);
        final BlockType blockType = block.getType();
        if (blockType == BlockType.StairsUp) {
            final int z = position.getZ();
            if (z > 0) {
                position.setZ(z - 1);
                chunk = getChunk();
                if (chunk.getBlock(x, y).getType() != BlockType.StairsDown) {
                    // Oops! Stairs don't match ... fix it now!
                    chunk.setBlock(x, y, new Block(BlockType.StairsDown));
                }
                updatedPosition(true);
                return true;
            }
        }
        return false;
    }

    /** Tries to go down, if possible. */
    public boolean goDown() {
        final World world = position.getWorld();
        if (world == null) {
            // Detached
            return false;
        }
        Chunk chunk = getChunk();
        final int x = position.getX();
        final int y = position.getY();
        final Block block = chunk.getBlock(x, y);
        final BlockType blockType = block.getType();
        if (blockType == BlockType.StairsDown) {
            final int z = position.getZ();
            if (z < World.Z - 1) {
                position.setZ(z + 1);
                chunk = getChunk();
                if (chunk.getBlock(x, y).getType() != BlockType.StairsUp) {
                    // Oops! Stairs don't match ... fix it now!
                    chunk.setBlock(x, y, new Block(BlockType.StairsUp));
                }
                updatedPosition(true);
                return true;
            }
        }
        return false;
    }

    /** Puts a block in front of the mobile, if possible. */
    public boolean layBlock() {
        final World world = position.getWorld();
        if (world == null) {
            // Detached
            return false;
        }
        final Position next = position.next();
        final Chunk chunk = world.getOrCreateChunk(next);
        if (chunk == null) {
            // At worlds edge
            return false;
        }
        final int x = next.getX();
        final int y = next.getY();
        if (chunk.getBlock(x, y).getType() == BlockType.Empty) {
            for (int i = 0; i < equipment.length; i++) {
                final Item item = equipment[i];
                if (item.getType() == ItemType.Block) {
                    chunk.setBlock(x, y, new Block(item.getBlockType()));
                    removeItem(i);
                    return true;
                }
            }
        }
        return false;
    }

    /** Crafts some items on an anvil in front of the mobile, if possible. */
    public boolean craftItems() {
        final World world = position.getWorld();
        if (world == null) {
            // Detached
            return false;
        }
        final Position next = position.next();
        final Chunk chunk = world.getOrCreateChunk(next);
        if (chunk == null) {
            // At worlds edge
            return false;
        }
        final int x = next.getX();
        final int y = next.getY();
        if (chunk.getBlock(x, y).getType() == BlockType.Anvil) {
            int iron = -1;
            int stick = -1;
            for (int i = 0; i < equipment.length; i++) {
                final Item item = equipment[i];
                if (item.getType() == ItemType.Iron) {
                    iron = i;
                } else if (item.getType() == ItemType.Stick) {
                    stick = i;
                }
            }
            if ((iron >= 0) && (stick >= 0)) {
                removeItem(iron);
                if (stick > iron) {
                    stick--;
                }
                removeItem(stick);
                addItem(new Item(ItemType.chooseCraftable(), null));
                return true;
            }
        }
        return false;
    }

    /**
     * The mobile was hit by some missile.
     * @param missile
     */
    public boolean hitBy(final Item missile) {
        final boolean result = damage(missile);
        if (!missile.use()) {
            final Chunk chunk = getChunk();
            if (chunk != null) {
                chunk.addItem(position.getX(), position.getY(), missile);
            }
        }
        return result;
    }
}
