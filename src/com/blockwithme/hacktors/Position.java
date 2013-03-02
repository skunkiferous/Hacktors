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

import com.google.common.base.Preconditions;

/**
 * Represents the position, and orientation if needed, of something.
 * We reuse the same class, even for objects that only make use of a part of it.
 *
 * An positioned game object is implicitly "detached", when it's "world" is set
 * to null.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
@Data
public class Position implements Cloneable {
    /** The position in the X axis. */
    private int x;
    /** The position in the Y axis. */
    private int y;
    /** The position in the Z axis. */
    private int z;
    /** The direction. */
    private Direction direction = Direction.choose();
    /** The world. If null, then the object is not currently used in the game. */
    private World world;

    /** Cloneable */
    @Override
    public Position clone() {
        try {
            return (Position) super.clone();
        } catch (final CloneNotSupportedException e) {
            // Impossible!
            throw new RuntimeException(e);
        }
    }

    /** The direction. */
    public void setDirection(final Direction theDirection) {
        direction = Preconditions.checkNotNull(theDirection);
    }

    /** Returns the next position in the current direction. */
    public Position next() {
        final Position result = clone();
        if (direction == Direction.XUp) {
            result.x++;
        } else if (direction == Direction.XDown) {
            result.x--;
        } else if (direction == Direction.YUp) {
            result.y++;
        } else if (direction == Direction.YDown) {
            result.y--;
        }
        return result;
    }

    /** Sets the x,y,z coordinates. */
    public void setXYZ(final Position other) {
        x = other.x;
        y = other.y;
        z = other.z;
    }
}
