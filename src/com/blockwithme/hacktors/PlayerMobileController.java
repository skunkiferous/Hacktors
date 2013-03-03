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

import java.util.Arrays;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

/**
 * MobileController for players.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class PlayerMobileController implements MobileController {

    /** Reference to the PlayerConsole :*/
    private final PlayerConsole console;

    /** The buffered input. */
    private final StringBuilder input = new StringBuilder();

    /** The buffered output. */
    private final StringBuilder output = new StringBuilder();

    /** Constructor */
    public PlayerMobileController(final PlayerConsole theConsole) {
        console = Preconditions.checkNotNull(theConsole);
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#updatedPosition(boolean)
     */
    @Override
    public void updatedPosition(final boolean changedLevel) {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#updatedDirection()
     */
    @Override
    public void updatedDirection() {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#pickedUp(com.blockwithme.hacktors.Item[])
     */
    @Override
    public void pickedUp(final Item[] array) {
        if ((array != null) && (array.length > 0)) {
            output.append("You picked up:\n");
            for (final Item item : array) {
                output.append(" a ").append(item).append('\n');
            }
        }
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#damaged(int, java.lang.Object)
     */
    @Override
    public void damaged(final int amount, final Object source) {
        if (source == null) {
            output.append("You took ").append(amount)
                    .append(" damage from ?\n");
        } else {
            output.append("You took ").append(amount).append(" damage from ")
                    .append(source).append('\n');
        }
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#dead()
     */
    @Override
    public void dead() {
        output.append("YOU DIE! GAME OVER!\n");
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#attacked(com.blockwithme.hacktors.Mobile, com.blockwithme.hacktors.Item, boolean)
     */
    @Override
    public void attacked(final Mobile other, final Item item,
            final boolean killed) {
        output.append("You attacked the ").append(other);
        if (item != null) {
            output.append(" with a ").append(item);
        }
        if (killed) {
            output.append(" and killed it!");
        }
        output.append('\n');
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#attacked(com.blockwithme.hacktors.Block, com.blockwithme.hacktors.Item, boolean)
     */
    @Override
    public void attacked(final Block block, final Item item,
            final boolean destroyed) {
        output.append("You attacked the ").append(block);
        if (item != null) {
            output.append(" with a ").append(item);
        }
        if (destroyed) {
            output.append(" and destroyed it!");
        }
        output.append('\n');
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#itemAdded(com.blockwithme.hacktors.Item)
     */
    @Override
    public void itemAdded(final Item theItem) {
        output.append("Added to your inventory: ").append(theItem).append('\n');
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#itemRemoved(com.blockwithme.hacktors.Item)
     */
    @Override
    public void itemRemoved(final Item theItem) {
        output.append("Removed from your inventory: ").append(theItem)
                .append('\n');
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#ate(com.blockwithme.hacktors.Item)
     */
    @Override
    public void ate(final Item theItem) {
        output.append("You ate a ").append(theItem).append('\n');
    }

    /** Display game area around player. */
    private void displayArea(final Mobile mobile, final World world) {
        final Position pos = mobile.getPosition();
        final MobileType type = mobile.getType();
        final int perception = type.getPerception();
        final int x = pos.getX();
        final int y = pos.getY();
        final int xMin = x - perception;
        final int xMax = x + perception;
        final int yMin = y - perception;
        final int yMax = y + perception;
        final char[][] area = new char[perception][];
        for (int i = 0; i < area.length; i++) {
            area[i] = new char[perception];
            Arrays.fill(area[i], ' ');
        }
        final Level level = world.getOrCreateLevel(pos.getZ());
        for (int px = xMin; px <= xMax; px++) {
            pos.setX(px);
            final int m = px - xMin;
            for (int py = yMin; py <= yMax; py++) {
                pos.setY(py);
                final int n = py - yMin;
                if (world.isValid(pos)) {
                    if ((px == x) && (py == y)) {
                        area[m][n] = '@';
                    } else {
                        final Chunk chunk = level.getOrCreateChunkOf(px, py);
                        final Mobile mob = chunk.getMobile(px, py);
                        if (mob != null) {
                            area[m][n] = mob.getType().getDisplay();
                        } else {
                            final Item[] items = chunk.getItems(px, py);
                            if (items.length > 0) {
                                if (items.length == 1) {
                                    area[m][n] = items[0].getType()
                                            .getDisplay();
                                } else {
                                    area[m][n] = '*';
                                }
                            } else {
                                final Block block = chunk.getBlock(px, py);
                                area[m][n] = block.getType().getDisplay();
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < area.length; i++) {
            output.append(area[i]).append('\n');
        }
    }

    /** Display player stats. */
    private void displayStats(final Mobile mobile, final World world) {
        final Position pos = mobile.getPosition();
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        final int cycle = world.getClock().getCycle();
        final int life = mobile.getLife();
        final MobileType type = mobile.getType();
        output.append("(").append(x).append(",").append(y).append(",")
                .append(z).append(",").append(cycle).append(") HP: ")
                .append(life).append(" SPC: ").append(type).append('\n');
        final Item[] items = mobile.getEquipment();
        boolean newline = false;
        for (int i = 0; i < items.length; i++) {
            final Item item = items[i];
            final ItemType it = item.getType();
            output.append(i).append(": ").append(it).append(' ');
            newline = true;
            if (it == ItemType.Block) {
                output.append(" (").append(item.getBlockType()).append(") ");
            }
            if ((i == 3) || (i == 6)) {
                output.append('\n');
                newline = false;
            }
        }
        if (newline) {
            output.append('\n');
        }
    }

    /** Gives out the help. */
    private void help() {
        // TODO
    }

    /** Quits the game. */
    private void quit(final World world) {
        output.append("QUITTING!\n");
        flushOutput();
        world.getClock().stop();
    }

    /** Flushes the output */
    private void flushOutput() {
        if (output.length() > 0) {
            console.output(output.toString());
            output.setLength(0);
        }
    }

    /** Try to go, or attack, in the specified direction. */
    private void go(final Mobile mobile, final Direction direction) {
        mobile.getPosition().setDirection(direction);
        if (!mobile.move()) {
            mobile.attack();
        }
    }

    /** Drops an item. */
    private void drop(final Mobile mobile, final char item) {
        final int index = item - '0';
        if (!mobile.drop(index)) {
            output.append("FAILED TO DROP ITEM ").append(index).append("!\n");
        }
    }

    /** Process the player input. */
    private void processInput(final Mobile mobile, final World world) {
        input.append(console.input());
        if (input.length() > 0) {
            final char cmd = input.charAt(0);
            input.deleteCharAt(0);
            switch (cmd) {
            case 'h':
                help();
                break;

            case 'q':
                quit(world);
                break;

            case 'w':
                go(mobile, Direction.YUp);
                break;

            case 's':
                go(mobile, Direction.YDown);
                break;

            case 'a':
                go(mobile, Direction.YDown);
                break;

            case 'd':
                go(mobile, Direction.YUp);
                break;

            case 'o':
                if (!mobile.open()) {
                    output.append("FAILED TO OPEN!\n");
                }
                break;

            case 'c':
                if (!mobile.close()) {
                    output.append("FAILED TO CLOSE!\n");
                }
                break;

            case '<':
                if (!mobile.goUp()) {
                    output.append("FAILED TO GO UP!\n");
                }
                break;

            case '>':
                if (!mobile.goDown()) {
                    output.append("FAILED TO GO DOWN!\n");
                }
                break;

            case 'f':
                if (!mobile.fire()) {
                    output.append("FAILED TO FIRE!\n");
                }
                break;

            case 'e':
                if (!mobile.eat()) {
                    output.append("FAILED TO EAT!\n");
                }
                break;

            case 'b':
                if (!mobile.layBlock()) {
                    output.append("FAILED TO LAY BLOCK!\n");
                }
                break;

            case 'p':
                if (!mobile.pickup()) {
                    output.append("FAILED TO PICKUP!\n");
                }
                break;

            case 'i':
                if (!mobile.craftItems()) {
                    output.append("FAILED TO CRAFT ITEM!\n");
                }
                break;

            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                drop(mobile, cmd);
                break;

            default:
                output.append("UNKNOWN COMMAND: ").append(cmd).append('\n');
                help();
                break;
            }
        }
        flushOutput();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#act(com.blockwithme.hacktors.Mobile)
     */
    @Override
    public void act(final Mobile mobile) {
        final World world = mobile.getPosition().getWorld();
        if (world == null) {
            System.out.println("I'm sorry Dave, I can't let you do that.");
        } else {
            displayArea(mobile, world);
            displayStats(mobile, world);
            processInput(mobile, world);
        }
    }
}
