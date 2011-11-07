/*
 * Copyright 2011 Jeroen Meetsma
 *
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

package org.ijsberg.iglu.util.codegeneration;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Java source that is part of a structure of sources that can be compiled.
 */
public class JavaSource {
	private ArrayList javadoc = new ArrayList();

	protected String javaDocToString(String prefixWhitespace) {
		StringBuffer retval = new StringBuffer(prefixWhitespace + "/**\n");
		Iterator i = javadoc.iterator();
		while (i.hasNext()) {
			retval.append(prefixWhitespace + " * " + i.next() + "\n");
		}
		retval.append(prefixWhitespace + " */\n");
		return retval.toString();
	}

	public void addJavaDocLine(String line) {
		javadoc.add(line);
	}

}
