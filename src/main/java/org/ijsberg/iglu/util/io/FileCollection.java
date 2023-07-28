/*
 * Copyright 2011-2014 Jeroen Meetsma - IJsberg Automatisering BV
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

package org.ijsberg.iglu.util.io;

import java.io.IOException;
import java.util.List;

/**
 */
public interface FileCollection {

    List<String> getFileNames();

    byte[] getFileContents(String fileName) throws IOException;

    String getFileContentsAsString(String fileName) throws IOException;

	boolean containsFile(String fileName);

	Directory getRootDirectory();

	String getDescription();

	int size();

	void close() throws IOException;

	String getName();
}
