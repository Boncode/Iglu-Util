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

import org.ijsberg.iglu.util.mail.MimeTypeSupport;
import org.ijsberg.iglu.util.misc.EncodingSupport;

import java.io.File;


//TODO it is not obvious that raw data needs to be set

/**
 * Is a transient carrier for a file stored in the file system.
 */
public class FileData {
	//contents
	private byte[] rawData;
	private String description = "";
	//
	protected String fileName = "";
	private String fileNameNoExt = "";
	private String path = "";
	private String extension = "";
	private String mimeType = "";

	protected String fileNameAndPath;
	private long lastModified;

	public FileData(File file) {
		this(file.getName(), file.lastModified());
	}

	/**
	 * @param fullFileName
	 */
	public FileData(String fullFileName) {
		this(fullFileName, System.currentTimeMillis());
	}

	/**
	 * @param fullFileName
	 */
	public FileData(String fullFileName, long lastModified) {
		fileNameAndPath = fullFileName;
		this.lastModified = lastModified;
		setFullFileName(fullFileName);
	}


	/**
	 * Copy constructor.
	 * Creates a shallow copy of the original.
	 *
	 * @param fileData
	 */
	public FileData(FileData fileData) {
		fileNameAndPath = fileData.fileNameAndPath;
		rawData = fileData.rawData;
		description = fileData.description;
		//
		fileName = fileData.fileName;
		fileNameNoExt = fileData.fileNameNoExt;
		path = fileData.path;
		extension = fileData.extension;
		mimeType = fileData.mimeType;
		lastModified = fileData.lastModified;
	}

	/**
	 * @param fullFileName
	 * @param mimeType
	 */
	public FileData(String fullFileName, String mimeType) {
		fileNameAndPath = fullFileName;
		setFullFileName(fullFileName);
		this.mimeType = mimeType;
	}

	public String getFileNameAndPath() {
		return fileNameAndPath;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * Sets the file name including path and extension
	 *
	 * @param fullFileName
	 */
	public void setFullFileName(String fullFileName) {

		fullFileName = FileSupport.convertToUnixStylePath(fullFileName);
		int lastFileSeparator = fullFileName.lastIndexOf('/');

		if (lastFileSeparator != -1) {
			path = fullFileName.substring(0, lastFileSeparator);
		}
		else {
			path = "";
		}
		setFileName(fullFileName.substring(lastFileSeparator + 1, fullFileName.length()));
	}


	/**
	 * @return the data contained in the file
	 */
	public byte[] getRawData() {
		return rawData;
	}

	/**
	 * @return
	 */
	public String getRawDataBase64Encoded() {
		return EncodingSupport.encodeBase64(rawData, 57);
	}


	/**
	 * @param data
	 */
	public void setRawData(byte[] data) {
		this.rawData = data;
	}

	/**
	 * @param mimeType
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
		if (fileName.lastIndexOf('.') != -1) {
			extension = fileName.substring(fileName.lastIndexOf('.') + 1);
			fileNameNoExt = fileName.substring(0, fileName.lastIndexOf('.'));
		}
		else {
			extension = "";
			fileNameNoExt = fileName;
		}
		setMimeType(MimeTypeSupport.getMimeTypeForFileExtension(extension));
	}

	/**
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @return
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return
	 */
	public String getFileNameWithoutExtension() {
		return fileNameNoExt;
	}

	/**
	 * @return
	 */
	public String getFullFileName() {
		return (!"".equals(path) ? path + '/' : "") + fileName;
	}

	public String getFullFileNameWithoutExtension() {
		return (!"".equals(path) ? path + '/' : "") + fileNameNoExt;
	}

	/**
	 * @return
	 */
	public int getSize() {
		if (rawData != null) {
			return rawData.length;
		}
		return 0;
	}

	public long lastModified() {
		return lastModified;
	}

	/**
	 * @return a brief description
	 */
	public String toString() {
		return "file " + fileName + " (" + mimeType + ") size=" + getSize() + " bytes (" + description + ')';
	}
}
