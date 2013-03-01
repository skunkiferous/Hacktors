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
public class Direction extends Enum40<Direction>{
	public static final Direction XUp = new Direction();
	public static final Direction XDown = new Direction();
	public static final Direction YUp = new Direction();
	public static final Direction YDown = new Direction();

	/** All the values */
	public static final Direction[] VALUES = Enum40.values(Direction.class);

	/** Default constructor. */
    protected Direction() {
		this(Direction.class);
	}

	/** Constructor for subclasses. */
    protected Direction(final Class<? extends Direction> enumClass) {
		super(enumClass);
	}

	/** Chooses one direction at random. */
    public static Direction choose() {
        return VALUES[Util.RND.nextInt(VALUES.length)];
    }
}
