/*
 * Copyright 2011-2013 Jeroen Meetsma - IJsberg
 *
 * This file is part of Iglu.
 *
 * Iglu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Iglu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Iglu.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ijsberg.iglu.util.misc;


import org.junit.Test;

public class KeyGeneratorTest {

    @Test
    public void test() {
        long currentTimeInMillis = 1649850554992l;//System.currentTimeMillis();
        System.out.println("input:" + currentTimeInMillis);
        String key = KeyGenerator.generateKeyFromLong(currentTimeInMillis, 0);
        System.out.println("key:" + key);
        System.out.println("restoredKey:" + KeyGenerator.getLongFromKey(key));
    }

}

