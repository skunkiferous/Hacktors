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

    /** toString */
    @Override
    public String toString() {
        return "Position(x=" + x + ",y=" + y + ",z=" + z + ",direction="
                + direction + ")";
    }

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

    /** Completely copies this position. Returns true on a non-direction change. */
    public boolean setPosition(final Position thePosition) {
        boolean result = false;
        if (x != thePosition.x) {
            x = thePosition.x;
            result = true;
        }
        if (y != thePosition.y) {
            y = thePosition.y;
            result = true;
        }
        if (z != thePosition.z) {
            z = thePosition.z;
            result = true;
        }
        if (world != thePosition.world) {
            world = thePosition.world;
            result = true;
        }
        direction = thePosition.direction;
        return result;
    }

    /** The direction. */
    public void setDirection(final Direction theDirection) {
        direction = Preconditions.checkNotNull(theDirection);
    }

    /** Returns the next position in the current direction. */
    public Position next() {
        final Position result = clone();
        if (direction == Direction.XDown) {
            result.x++;
        } else if (direction == Direction.XUp) {
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

    /**
     * Returns the direction pointing toward the other position.
     * Will return null if both position have same x and y.
     */
    public Direction towards(final Position other) {
        final int dx = x - other.x;
        final int dy = y - other.y;
        return towards2(dx, dy);
    }

    /**
     * Returns the direction pointing toward the other position.
     * Will return null if both position have same x and y.
     */
    public Direction towards(final int px, final int py) {
        final int dx = x - px;
        final int dy = y - py;
        return towards2(dx, dy);
    }

    private Direction towards2(final int dx, final int dy) {
        if ((dx == 0) && (dy == 0)) {
            return null;
        }
        if (Math.abs(dx) > Math.abs(dy)) {
            return (dx > 0) ? Direction.XUp : Direction.XDown;
        }
        return (dy > 0) ? Direction.YDown : Direction.YUp;
    }

    /** Returns the direction pointing away from the other position. */
    public Direction awayFrom(final Position other) {
        final Direction towards = towards(other);
        // if both position are the same, any direction will do ...
        return (towards == null) ? Direction.choose() : towards.opposite();
    }

    /** Returns the distance to another position, taking only x and y in account. */
    public float distance(final Position other) {
        final int dx = x - other.x;
        final int dy = y - other.y;
        return distance2(dx, dy);
    }

    /** Returns the distance to another position, taking only x and y in account. */
    public float distance(final int px, final int py) {
        final int dx = x - px;
        final int dy = y - py;
        return distance2(dx, dy);
    }

    private float distance2(final int dx, final int dy) {
        if ((dx == 0) && (dy == 0)) {
            return 0.0f;
        }
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
