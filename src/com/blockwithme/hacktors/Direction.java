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

import com.blockwithme.util.Enum40;

/**
 * Direction that some mobile is pointing to.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class Direction extends Enum40<Direction> {
    /** serialVersionUID */
    private static final long serialVersionUID = -3925717219015240483L;

    // CHECKSTYLE.OFF: ConstantName
    /** The positive X direction. */
    public static final Direction XUp = new Direction();
    /** The negative X direction. */
    public static final Direction XDown = new Direction();
    /** The positive Y direction. */
    public static final Direction YUp = new Direction();
    /** The negative Y direction. */
    public static final Direction YDown = new Direction();
    // CHECKSTYLE.ON: ConstantName

    /** All the values */
    public static final Direction[] VALUES = Enum40.values(Direction.class);

    /** The opposite direction. */
    private transient Direction opposite;

    /** The normal directions. */
    private transient Direction[] normal;

    /** Default constructor. */
    protected Direction() {
        this(Direction.class);
    }

    /** Constructor for subclasses. */
    protected Direction(final Class<? extends Direction> enumClass) {
        super(enumClass);
    }

    /** The opposite direction. */
    public Direction opposite() {
        return opposite;
    }

    /** The normal directions. */
    public Direction[] normal() {
        return normal;
    }

    /** Chooses one direction at random. */
    public static Direction choose() {
        return VALUES[Util.RND.nextInt(VALUES.length)];
    }

    @Override
    protected void postInit(final Direction[] allSet) {
        if (this == XUp) {
            opposite = XDown;
            normal = new Direction[] { YUp, YDown };
        } else if (this == XDown) {
            opposite = XUp;
            normal = new Direction[] { YUp, YDown };
        } else if (this == YUp) {
            opposite = YDown;
            normal = new Direction[] { XUp, XDown };
        } else if (this == YDown) {
            opposite = YUp;
            normal = new Direction[] { XUp, XDown };
        }
    }
}
