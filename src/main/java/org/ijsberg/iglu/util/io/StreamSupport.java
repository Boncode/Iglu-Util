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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Helper class for stream related functions.
 */
public abstract class StreamSupport {

	public static int BUF_SIZE = 100000;

	public static final int LEN_STREAM_END_INDICATOR = -1;
	public static final int LEN_STREAM_INTERRUPTED_INDICATOR = 0;

	/**
	 * Reads available bytes from an input stream
	 * Method returns if no more bytes currently available
	 *
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static byte[] readAvailableFromInputStream(InputStream input) throws IOException {
		return readInputStream(input, LEN_STREAM_INTERRUPTED_INDICATOR);
	}

	/**
	 * Reads all bytes from an input stream
	 * Note: method blocks if end not reached
	 *
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static byte[] absorbInputStream(InputStream input) throws IOException {
		return readInputStream(input, LEN_STREAM_END_INDICATOR);
	}

	/**
	 * Reads all bytes from an input stream
	 * Note: method may block if end not reached
	 *
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static byte[] readInputStream(InputStream input, int lengthToStop) throws IOException {
		ByteArrayOutputStream output = null;
		try{
			output = new ByteArrayOutputStream();
			readFromInputStream(input, output, lengthToStop);
			return output.toByteArray();
		} finally {
			output.close();
		}
	}

	/**
	 * Reads all bytes from an input stream and writes them to output stream
	 * Note: method blocks if end not reached
	 *
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public static int absorbInputStream(InputStream input, OutputStream output) throws IOException {
		return readFromInputStream(input, output, LEN_STREAM_END_INDICATOR);
	}

	/**
	 * Reads bytes from an input stream and writes them to output stream
	 * Note: method may block if end not reached
	 *
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public static int readFromInputStream(InputStream input, OutputStream output, int lengthToStop) throws IOException {
		int bytesAbsorbed = 0;
		int len = 1;
		while (len > lengthToStop) {
			byte[] buf = new byte[BUF_SIZE];
			len = input.available();
			if(len > 0) {
				len = input.read(buf);
			} else {
				len = lengthToStop;
			}
			if(len > 0) {
				output.write(buf, 0, len);
				bytesAbsorbed += len;
			}
		}
		return bytesAbsorbed;
	}

	public static void writeToOutputStream(byte[] contents, OutputStream output) throws IOException {
		output.write(contents, 0, contents.length);
	}
}
