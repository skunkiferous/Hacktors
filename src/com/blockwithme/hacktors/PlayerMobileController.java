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
import java.util.Arrays;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Data;

import com.google.common.base.Preconditions;

/**
 * MobileController for players.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class PlayerMobileController implements MobileController {

    /** A text message, with a duration. */
    @Data
    private static class Message {
        private static final long TIMEOUT = 5000L;

        public final StringBuilder output = new StringBuilder();
        private final long created = System.currentTimeMillis();
        private String text;

        /** Returns true when the message timed-out, and should be deleted. */
        public boolean delete() {
            return System.currentTimeMillis() - created >= TIMEOUT;
        }

        @Override
        public String toString() {
            if (text == null) {
                text = output.toString();
            }
            return text;
        }
    }

    /** The player avatar. */
    private Mobile mobile;

    /** Reference to the PlayerConsole :*/
    private final PlayerConsole console;

    /** The buffered input. */
    private final StringBuilder input = new StringBuilder();

    /** The buffered output. */
    private final StringBuilder output = new StringBuilder();

    /** Currently visible messages. */
    private final List<Message> messages = new ArrayList<>();

    /** Creates a new message. */
    private StringBuilder msg() {
        final Message msg = new Message();
        if (!messages.isEmpty()) {
            System.out.println(messages.get(messages.size() - 1));
        }
        messages.add(msg);
        return msg.output;
    }

    /** Converts an Item to String. */
    private String str(final Item item) {
        return item.getType().toString();
    }

    /** Converts an Block to String. */
    private String str(final Block block) {
        return block.getType().toString();
    }

    /** Converts an Mobile to String. */
    private String str(final Mobile mobile) {
        return mobile.getType().toString();
    }

    /** Converts an object to String. */
    private String str(final Object obj) {
        if (obj instanceof Item) {
            return str((Item) obj);
        }
        if (obj instanceof Block) {
            return str((Block) obj);
        }
        if (obj instanceof Mobile) {
            return str((Mobile) obj);
        }
        return obj.toString();
    }

    /** Constructor */
    public PlayerMobileController(final PlayerConsole theConsole) {
        console = Preconditions.checkNotNull(theConsole);
    }

    /** Avatar setter */
    @Override
    public void setMobile(final Mobile theMobile) {
        mobile = Preconditions.checkNotNull(theMobile);
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
            final StringBuilder msg = msg();
            msg.append("You picked up:\n");
            for (final Item item : array) {
                msg.append(" a ").append(str(item)).append('\n');
            }
        }
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#damaged(int, java.lang.Object)
     */
    @Override
    public void damaged(final int amount, final Object source) {
        if (source == null) {
            msg().append("You took ").append(amount).append(" damage from ?\n");
        } else {
            msg().append("You took ").append(amount).append(" damage from ")
                    .append(str(source)).append('\n');
        }
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#dead()
     */
    @Override
    public void dead() {
        msg().append("YOU DIE! GAME OVER!\n");
        quit(mobile.getWorld());
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#attacked(com.blockwithme.hacktors.Mobile, com.blockwithme.hacktors.Item, boolean)
     */
    @Override
    public void attacked(final Mobile other, final Item item,
            final boolean killed) {
        final StringBuilder msg = msg();
        msg.append("You attacked the ").append(str(other));
        if (item != null) {
            msg.append(" with a ").append(str(item));
        }
        if (killed) {
            msg.append(" and killed it!");
        }
        msg.append('\n');
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#attacked(com.blockwithme.hacktors.Block, com.blockwithme.hacktors.Item, boolean)
     */
    @Override
    public void attacked(final Block block, final Item item,
            final boolean destroyed) {
        final StringBuilder msg = msg();
        msg.append("You attacked the ").append(str(block));
        if (item != null) {
            msg.append(" with a ").append(str(item));
        }
        if (destroyed) {
            msg.append(" and destroyed it!");
        }
        msg.append('\n');
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#itemAdded(com.blockwithme.hacktors.Item)
     */
    @Override
    public void itemAdded(final Item theItem) {
        msg().append("Added to your inventory: ").append(str(theItem))
                .append('\n');
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#itemRemoved(com.blockwithme.hacktors.Item)
     */
    @Override
    public void itemRemoved(final Item theItem) {
        msg().append("Removed from your inventory: ").append(str(theItem))
                .append('\n');
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#ate(com.blockwithme.hacktors.Item)
     */
    @Override
    public void ate(final Item theItem) {
        final StringBuilder msg = msg();
        msg.append("You ate a ").append(str(theItem)).append('.');
        if (theItem.getType().getFood() < 0) {
            msg.append(" You feel poisoned!");
        }
        msg.append('\n');
    }

    /** Display game area around player. */
    private void displayArea(final World world) {
        final Position pos = mobile.getPositionClone();
        final MobileType type = mobile.getType();
        final int perception = type.getPerception();
        final int x = pos.getX();
        final int y = pos.getY();
        final int xMin = x - perception;
        final int xMax = x + perception;
        final int yMin = y - perception;
        final int yMax = y + perception;
        final int size = 1 + 2 * perception;
        final char[][] area = new char[size][];
        for (int i = 0; i < area.length; i++) {
            area[i] = new char[size];
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
                            final int color = mob.getType().getColor()
                                    .ordinal();
                            area[m][n] = (char) (mob.getType().getDisplay() | (color << 8));
                        } else {
                            final Item[] items = chunk.getItems(px, py);
                            if (items.length > 0) {
                                if (items.length == 1) {
                                    final int color = items[0].getType()
                                            .getColor().ordinal();
                                    area[m][n] = (char) (items[0].getType()
                                            .getDisplay() | (color << 8));
                                } else {
                                    area[m][n] = '*';
                                }
                            } else {
                                final Block block = chunk.getBlock(px, py);
                                final int color = block.getType().getColor()
                                        .ordinal();
                                area[m][n] = (char) (block.getType()
                                        .getDisplay() | (color << 8));
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
    private void displayStats(final World world) {
        final Position pos = mobile.getPositionClone();
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        final int cycle = world.getClock().getCycle();
        final int life = mobile.getLife();
        final MobileType type = mobile.getType();
        final char dir = pos.getDirection().getDisplay();
        output.append("(").append(x).append(",").append(y).append(",")
                .append(z).append(") TIME: ").append(cycle).append(" HP: ")
                .append(life).append(" DIR: ").append(dir).append(" RACE: ")
                .append(type).append('\n');
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
        final Level level = world.getOrCreateLevel(pos.getZ());
        final Chunk chunk = level.getOrCreateChunkOf(x, y);
        final Item[] itemsUnderAvatar = chunk.getItems(x, y);
        final Block blockUnderAvatar = chunk.getBlock(x, y);
        final boolean stuff = (itemsUnderAvatar.length > 0);
        final boolean notEmpty = (blockUnderAvatar.getType() != BlockType.Empty);
        if (stuff || notEmpty) {
            if (newline) {
                output.append('\n');
            }
            output.append("You are standing on:");
            if (notEmpty) {
                output.append(' ').append(str(blockUnderAvatar));
            }
            if (stuff) {
                for (final Item item : itemsUnderAvatar) {
                    output.append(' ').append(str(item));
                }
            }
            newline = true;
        }
        if (newline) {
            output.append('\n');
        }
    }

    /** Gives out the help. */
    private void help() {
        final StringBuilder msg = msg();
        msg.append("h - help (What you see now)\n");
        msg.append("q - quit (Terminate the game)\n");
        msg.append("w - up (Move towards the top of the screen)\n");
        msg.append("s - down (Move towards the bottom of the screen)\n");
        msg.append("a - left (Move towards the left of the screen)\n");
        msg.append("d - right (Move towards the right of the screen)\n");
        msg.append("o - open (A chest or door)\n");
        msg.append("c - close (A chest or door)\n");
        msg.append("< - go upstairs (If there is a stairs up!)\n");
        msg.append("> - go downstairs (If there is a stairs down!)\n");
        msg.append("e - eat (If you have food)\n");
        msg.append("b - put down block (If you have a block)\n");
        msg.append("p - pickup items on floor (If any)\n");
        msg.append("i - craft items (If you see an anvil)\n");
        msg.append("0-9 - Throw item with given number.\n");
    }

    /** Quits the game. */
    private void quit(final World world) {
        msg().append("QUITTING!\n");
        world.getClock().stop();
    }

    /** Try to go, or attack, in the specified direction. */
    private void move(final Direction direction) {
        mobile.setDirection(direction);
        if (!mobile.move()) {
            mobile.attack();
        }
    }

    /** Throws an item. */
    private void fire(final char item) {
        final int index = item - '0';
        if (!mobile.fire(index)) {
            msg().append("FAILED TO THROW ITEM ").append(index).append("!\n");
        }
    }

    /** Process the player input. */
    private void processInput(final World world) {
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
                move(Direction.XUp);
                break;

            case 's':
                move(Direction.XDown);
                break;

            case 'a':
                move(Direction.YDown);
                break;

            case 'd':
                move(Direction.YUp);
                break;

            case 'o':
                if (!mobile.open()) {
                    msg().append("FAILED TO OPEN!\n");
                }
                break;

            case 'c':
                if (!mobile.close()) {
                    msg().append("FAILED TO CLOSE!\n");
                }
                break;

            case '<':
                if (!mobile.goUp()) {
                    msg().append("FAILED TO GO UP!\n");
                }
                break;

            case '>':
                if (!mobile.goDown()) {
                    msg().append("FAILED TO GO DOWN!\n");
                }
                break;

            case 'e':
                if (!mobile.eat()) {
                    msg().append("FAILED TO EAT!\n");
                }
                break;

            case 'b':
                if (!mobile.layBlock()) {
                    msg().append("FAILED TO LAY BLOCK!\n");
                }
                break;

            case 'p':
                if (!mobile.pickup()) {
                    msg().append("FAILED TO PICKUP!\n");
                }
                break;

            case 'i':
                if (!mobile.craftItems()) {
                    msg().append("FAILED TO CRAFT ITEM!\n");
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
                fire(cmd);
                break;

            default:
                msg().append("UNKNOWN COMMAND: ").append(cmd).append('\n');
                help();
                break;
            }
        }
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#act()
     */
    @Override
    public void act() {
        final World world = mobile.getWorld();
        if (world == null) {
            System.out.println("I'm sorry Dave, I can't let you do that.");
        } else if (mobile == null) {
            System.out.println("You're a gonner!");
        } else {
            displayArea(world);
            displayStats(world);
            processInput(world);
            flushOutput(true);
        }
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#stop()
     */
    @Override
    public void stop() {
        flushOutput(false);
        while (!messages.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                break;
            }
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).delete()) {
                    messages.remove(i--);
                }
            }
        }
        console.exit();
    }

    /** Flushes the output */
    private void flushOutput(final boolean clearScreen) {
        if (output.length() > 0) {
            for (int i = 0; i < messages.size(); i++) {
                final Message msg = messages.get(i);
                if (msg.delete()) {
                    messages.remove(i--);
                } else {
                    output.append(msg);
                }
            }
            console.output(output.toString(), clearScreen);
            output.setLength(0);
        }
    }
}
