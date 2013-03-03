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

import java.io.IOException;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface between the game and a player.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class PlayerConsole {
    /**
     * Sends output to the player.
     * Note that no new-line is added to the text.
     */
    public void output(final String text) {
        System.out.print(text);
    }

    /** Reads input form the user, if any. */
    public String input() {
        try {
            final int available = System.in.available();
            if (available > 0) {
                final byte[] in = new byte[available];
                final int read = System.in.read(in);
                final String str = new String(in);
                if (read == available) {
                    return str;
                }
                if (read > 0) {
                    return str.substring(0, read);
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
            System.out.println();
        }
        return "";
    }
}
