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
 * Offers default implementation of an AI-controlled MobileController,
 * appropriate to the type of Mobile.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class MobileControllers {

    /** Human controller. */
    public static final MobileController HUMAN = new MobileController() {
        // TODO Implement Human AI
    };

    /** Zombie controller. */
    public static final MobileController ZOMBIE = new MobileController() {
        // TODO Implement Zombie AI
    };

    /** Pig controller. */
    public static final MobileController PIG = new MobileController() {
        // TODO Implement Pig AI
    };

    /** Dog controller. */
    public static final MobileController DOG = new MobileController() {
        // TODO Implement Dog AI
    };

    /** Returns an appropriate MobileController for this type of Mobile. */
    public static MobileController defaultControllerFor(final MobileType type) {
        if (type == null) {
            throw new IllegalArgumentException("type is null");
        }
        if (type == MobileType.Human) {
            return HUMAN;
        }
        if (type == MobileType.Zombie) {
            return ZOMBIE;
        }
        if (type == MobileType.Pig) {
            return PIG;
        }
        if (type == MobileType.Dog) {
            return DOG;
        }
        throw new IllegalArgumentException("Unknows MobileType: " + type);
    }
}
