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
public class Position {
    private int x;
    private int y;
    private int z;
    private Direction direction = Direction.choose();
    private World world;
}
