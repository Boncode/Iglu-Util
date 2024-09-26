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

package org.ijsberg.iglu.util.misc;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Helper class for encoding and decoding byte arrays.
 */
public abstract class EncodingSupport {
	//String containing 64 characters used for encoding
	public static final String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	/**
	 * Converts a chunk of data into base 64 encoding.
	 *
	 * @param rawData
	 * @return
	 */
	public static String encodeBase64(byte[] rawData) {
		return encodeBase64(rawData, 0);
	}


	/**
	 * Converts a chunk of data into base 64 encoding.
	 *
	 * @param rawData
	 * @param lineLength optional length of lines in return value
	 * @return
	 */
	public static String encodeBase64(byte[] rawData, int lineLength) {
		if (rawData == null) {
			return "";
		}
		StringBuffer retval = new StringBuffer();
		int i = 0;
		int n = 0;
		for (; i < rawData.length - 2; i += 3) {
			if (lineLength > 0 && i > 0 && i % lineLength == 0) {
				retval.append("\n");
			}
			//n is a 32 bit number
			//shift all the way to left first to get rid of sign
			//  3.shift last 8 bits to left    2.shift next 8 bits left of first 1. start here with last 8 bits shifted into int
			n = (((rawData[i] << 24) >>> 8) + ((rawData[i + 1] << 24) >>> 16) + ((rawData[i + 2] << 24) >>> 24));
			//this results in a 24 bit number (stored in 32 bit int)
			//of which 4 chunks of 6 bits are used to pick characters of base64Chars
			retval.append(base64Chars.charAt((n >>> 18) & 63));
			retval.append(base64Chars.charAt((n >>> 12) & 63));
			retval.append(base64Chars.charAt((n >>> 6) & 63));
			retval.append(base64Chars.charAt(n & 63));
		}
		//finish according to spec
		if (i + 1 == rawData.length) {
			n = ((rawData[i] << 24) >>> 8);

			retval.append(base64Chars.charAt((n >>> 18) & 63));
			retval.append(base64Chars.charAt((n >>> 12) & 63));
			retval.append("==");
		}
		if (i + 2 == rawData.length) {
			n = ((rawData[i] << 24) >>> 8) + ((rawData[i + 1] << 24) >>> 16);

			retval.append(base64Chars.charAt((n >>> 18) & 63));
			retval.append(base64Chars.charAt((n >>> 12) & 63));
			retval.append(base64Chars.charAt((n >>> 6) & 63));
			retval.append('=');
		}

		return retval.toString();
	}

	public static byte[] decodeBase64(String encodedData) {
		return decodeBase64(encodedData, Charset.defaultCharset());
	}

	/**
	 * Decodes base 64 encoded string.
	 *
	 * @param encodedData
	 * @return
	 */
	public static byte[] decodeBase64(String encodedData, Charset charSet) {
		BufferedReader reader = new BufferedReader(new StringReader(encodedData));
		int length = encodedData.length();
		byte[] retval = new byte[length];
		int actualLength = 0;

		String line;
		try {
			while ((line = reader.readLine()) != null) { //fixme use a buffer
				byte[] rawData = line.getBytes(charSet);
				int n = 0;
				for (int i = 0; i < rawData.length; i += 4) {
					if(i + 3 >= rawData.length) {
						throw new UnsupportedEncodingException("encoded data is not properly base64 encoded");
					}
					n = (base64Chars.indexOf(rawData[i]) << 18) |
							(base64Chars.indexOf(rawData[i + 1]) << 12);

					retval[actualLength++] = (byte) ((n >>> 16) & 255);

					if (rawData[i + 2] != '=') {
						n |= (base64Chars.indexOf(rawData[i + 2]) << 6);
						retval[actualLength++] = (byte) ((n >>> 8) & 255);
					}
					if (rawData[i + 3] != '=') {
						n |= (base64Chars.indexOf(rawData[i + 3]));
						retval[actualLength++] = (byte) (n & 255);
					}
				}
			}
		}
		catch (IOException ioe) {
			throw new IllegalStateException("exception while reading input with message: " + ioe.getMessage(), ioe);
		}
		if (actualLength != length) {
			byte[] actualRetval = new byte[actualLength];
			System.arraycopy(retval, 0, actualRetval, 0, actualLength);
			return actualRetval;
		}
		return retval;
	}

	public static String encodeXor(String s, String key) {
		return encodeBase64(xorWithKey(s.getBytes(), key.getBytes()));
	}

	public static String decodeXor(String s, String key) {
		return new String(xorWithKey(decodeBase64(s), key.getBytes()));
	}

	public static byte[] xorWithKey(byte[] a, byte[] key) {
		byte[] out = new byte[a.length];
		for (int i = 0; i < a.length; i++) {
			out[i] = (byte) (a[i] ^ key[i%key.length]);
		}
		return out;
	}

	/***
	 * Deflate text data using the "deflate" algorithm
	 * @param data data to be compressed
	 * @return the compressed data as a byte array
	 * @throws IOException
	 */
	public static byte[] deflate(byte[] data) throws IOException {
		Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		DeflaterOutputStream outputStream = new DeflaterOutputStream(arrayOutputStream, deflater);
		outputStream.write(data);
		outputStream.close();
		return arrayOutputStream.toByteArray();
	}

	/***
	 * Inflate text data that has been compressed using the deflate method
	 * @param data input data to be decompressed
	 * @return the decompressed data as a byte array
	 * @throws IOException
	 */
	public static byte[] inflate(byte[] data) throws IOException {
		byte[] readBuffer = new byte[5000];
		Inflater inflater = new Inflater(true);
		ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
		InflaterInputStream inputStream = new InflaterInputStream(arrayInputStream, inflater);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		int len;
		while((len = inputStream.read(readBuffer)) != -1){
			outputStream.write(readBuffer, 0, len);
		}
		inputStream.close();
		outputStream.close();

		return outputStream.toByteArray();
	}
}
