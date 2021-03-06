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

import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

/**
 * General purpose utility methods.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public final class Util {
    /** Cannot be instantiated. */
    private Util() {
        // NOP
    }

    /** Shared random number generator. */
    private static final Random RND = new Random();

    /** Returns a random float. */
    public static float nextFloat() {
        final float result = RND.nextFloat();
        //        System.out.println("nextFloat(): " + result);
        return result;
    }

    /** Returns a random boolean. */
    public static boolean nextBoolean() {
        final boolean result = RND.nextBoolean();
        //        System.out.println("nextBoolean(): " + result);
        return result;
    }

    /** Returns a random int. */
    public static int nextInt() {
        final int result = RND.nextInt();
        //        System.out.println("nextInt(): " + result);
        return result;
    }

    /** Returns a random int in [0,maxExclusive[. */
    public static int nextInt(final int maxExclusive) {
        final int result = RND.nextInt(maxExclusive);
        //        System.out.println("nextInt(" + maxExclusive + "): " + result);
        return result;
    }

    /** Checks that the given array is neither null, nor contains null. */
    public static <E> E[] checkNotNull(final E[] array) {
        Preconditions.checkNotNull(array, "array cannot be null");
        for (int i = 0; i < array.length; i++) {
            Preconditions.checkNotNull(array[i], "array cannot contain null");
        }
        return array;
    }

    /** Checks that the given array is neither null, nor is empty, nor contains null. */
    public static <E> E[] checkNotNullOrEmpty(final E[] array) {
        Preconditions.checkNotNull(array, "array cannot be null");
        Preconditions.checkArgument(array.length > 0, "array cannot be empty");
        for (int i = 0; i < array.length; i++) {
            Preconditions.checkNotNull(array[i], "array cannot contain null");
        }
        return array;
    }
}
