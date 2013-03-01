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
 * The possible item general categories.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public class ItemCategory extends Enum40<ItemCategory> {
	public static final ItemCategory Food = new ItemCategory();
	public static final ItemCategory Material = new ItemCategory();
	public static final ItemCategory Tool = new ItemCategory();
	public static final ItemCategory Weapon = new ItemCategory();
	public static final ItemCategory Armor = new ItemCategory();

	/** Default constructor. */
	protected ItemCategory() {
		this(ItemCategory.class);
	}

	/** Constructor for super-classes. */
	protected ItemCategory(final Class<? extends ItemCategory> cls) {
		super(cls);
	}
}
