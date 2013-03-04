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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Interface between the game and a player.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class PlayerConsole {

    /** Maps our colors for the terminal colors. */
    private static final Map<Color, Terminal.Color> COLOR = new HashMap<>();

    static {
        for (final Color color : Color.values()) {
            final String name = color.name();
            final Terminal.Color mapping = Terminal.Color.valueOf(name);
            COLOR.put(color, mapping);
        }
    }

    /** The Terminal. */
    private final Terminal terminal;

    /** Constructor */
    public PlayerConsole() {
        if (System.getProperty("os.name", "").toLowerCase().contains("windows")) {
            terminal = TerminalFacade.createSwingTerminal();
        } else {
            terminal = TerminalFacade.createTerminal();
        }
        terminal.enterPrivateMode();
    }

    /** Terminates the player console. */
    public void exit() {
        terminal.exitPrivateMode();
    }

    /**
     * Sends output to the player.
     * Note that no new-line is added to the text.
     */
    public void output(final String text, final boolean clearScreen) {
        if (text.isEmpty()) {
            return;
        }
        if (clearScreen) {
            terminal.clearScreen();
        }
        int x = 0;
        int y = 0;
        terminal.moveCursor(x, y);
        final char[] chars = text.toCharArray();
        final Color[] colors = Color.values();
        for (int i = 0; i < chars.length; i++) {
            final char raw = chars[i];
            // Default is white
            final int color = raw >> 8;
            final char c = (char) (raw & 0xFF);
            if (c == '\n') {
                x = 0;
                y++;
                terminal.moveCursor(x, y);
            } else {
                terminal.applyForegroundColor(COLOR.get(colors[color]));
                terminal.putCharacter(c);
                x++;
            }
        }
        terminal.flush();
    }

    /** Reads input form the user, if any. */
    public String input() {
        final Key key = terminal.readInput();
        if (key == null) {
            return "";
        }
        return String.valueOf(key.getCharacter());
    }
}
