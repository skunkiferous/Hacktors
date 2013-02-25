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

/**
 * The random generator chooses a new generator every time it is called,
 * from the list that was given to it in the constructor.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class RandomGenerator implements Generator {
    /** The generators to choose from. */
    private final Generator[] generators;

    /**
     * Constructor, with a list of generators to choose from.
     * Should not be empty.
     */
    public RandomGenerator(final Generator[] theGenerators) {
        generators = Util.checkNotNullOrEmpty(theGenerators);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fill(final Chunk chunk) {
        generators[Util.RND.nextInt(generators.length)].fill(chunk);
    }
}
