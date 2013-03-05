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

/**
 * The main class.
 *
 * @author monster
 */
public class Main {
    /** Starts the game. */
    public static void main(final String[] args) {
        final World world = new World();
        final PlayerConsole console = new PlayerConsole(world);
        final PlayerMobileController controller = new PlayerMobileController(
                console);
        final Mobile avatar = Mobile.create(controller, MobileType.Human);
        avatar.addItem(Item.create(ItemType.Sword));
        avatar.addItem(Item.create(ItemType.DogEgg));
        avatar.addItem(Item.create(ItemType.Meat));
        avatar.addItem(Item.create(ItemType.Iron));
        final Level level = world.getOrCreateLevel(0);
        final int x = World.X / 2;
        final int y = World.Y / 2;
        final Chunk chunk = level.getOrCreateChunkOf(x, y);
        chunk.setBlock(x, y, Block.EMPTY);
        chunk.setMobile(x, y, avatar);
        controller.setMobile(avatar);
        world.getClock().start();
        System.exit(0);
    }
}
