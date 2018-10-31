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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 */
public class ZippedFileCollection implements FileCollection {

    private Map<String, ZipEntry> filesByRelativePathAndName = new TreeMap<String, ZipEntry>();
	private Map<String, ZipEntry> filesNoDirectoriesByRelativePathAndName = new TreeMap<String, ZipEntry>();
    private FileFilterRuleSet includedFilesRuleSet;
    private ZipFile zipFile;

	private String relativeDir = "";

    public ZippedFileCollection(String zipFileName, FileFilterRuleSet fileFilterRuleSet) throws IOException {
        this(new ZipFile(zipFileName), fileFilterRuleSet);
    }

	public ZippedFileCollection(String zipFileName, String relativeDir, FileFilterRuleSet fileFilterRuleSet) throws IOException {
		this(new ZipFile(zipFileName), relativeDir, fileFilterRuleSet);
	}

	public ZippedFileCollection(File file) throws IOException {
		this(new ZipFile(file), new FileFilterRuleSet().setIncludeFilesWithNameMask("*.*"));
	}

	public ZippedFileCollection(ZipFile zipFile, FileFilterRuleSet fileFilterRuleSet) {
        this.includedFilesRuleSet = fileFilterRuleSet;
        this.zipFile = zipFile;
		if(fileFilterRuleSet.getBaseDir() != null) {
			this.relativeDir = FileSupport.convertToUnixStylePath(fileFilterRuleSet.getBaseDir());
		}
		refreshFiles();
    }

	public ZippedFileCollection(ZipFile zipFile, String relativeDir, FileFilterRuleSet fileFilterRuleSet) {
		this.includedFilesRuleSet = fileFilterRuleSet;
		this.zipFile = zipFile;
		if(relativeDir != null && !"".equals(relativeDir)) {
			this.relativeDir = FileSupport.convertToUnixStylePath(relativeDir);
			if(!this.relativeDir.endsWith("/") /*&& !relativeDir.isEmpty()*/) {
				this.relativeDir += "/";
			}
		}
//		System.out.println("====================> " + relativeDir);
		//fileFilterRuleSet.setBaseDir(this.relativeDir);
		refreshFiles();
	}

	public ZippedFileCollection(File file, FileFilterRuleSet fileFilter) throws IOException {
		this(new ZipFile(file), fileFilter);
	}

	@Override
    public List<String> getFileNames() {
        return new ArrayList<String>(filesByRelativePathAndName.keySet());
    }

//	@Override
	public List<String> getFileNotDirectoryNames() {
		return new ArrayList<String>(filesNoDirectoriesByRelativePathAndName.keySet());
	}

    @Override
    public byte[] getFileContents(String fileName) throws IOException {
        return FileSupport.getBinaryFromZip(relativeDir + fileName, zipFile);
    }

	/**
	 * Converts bytes in file to String, using default encoding
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
    @Override
    public String getFileContentsAsString(String fileName) throws IOException {
		//fails if relativeDir not OK
        return FileSupport.getTextFileFromZip(relativeDir + fileName, zipFile);
    }

	@Override
	public FileFilterRuleSet getFileFilter() {
		return includedFilesRuleSet;
	}

	@Override
	public boolean containsFile(String fileName) {
		boolean retval = filesByRelativePathAndName.containsKey(FileSupport.convertToUnixStylePath(fileName));
		return retval;
	}

	protected void refreshFiles() {
        filesByRelativePathAndName.clear();
		rootDir = new Directory("ROOT");

        List<ZipEntry> zipEntries = FileSupport.getContentsFromZipFile(zipFile, includedFilesRuleSet);

        for (ZipEntry zipEntry : zipEntries) {
            String relativePathAndName = FileSupport.convertToUnixStylePath(zipEntry.getName());
			if(!zipEntry.isDirectory() && (relativePathAndName.startsWith(relativeDir) || relativePathAndName.startsWith("/" + relativeDir))) {
				relativePathAndName = relativePathAndName.substring(relativeDir.length());

				if (relativePathAndName.startsWith("/")) {
					relativePathAndName = relativePathAndName.substring(1);
				}
				if(!zipEntry.isDirectory()) {
					filesNoDirectoriesByRelativePathAndName.put(relativePathAndName, zipEntry);
				}
				filesByRelativePathAndName.put(relativePathAndName, zipEntry);
				rootDir.addFile(relativePathAndName);
			}
		}
	}

	@Override
	public Directory getRootDirectory() {
		return rootDir;
	}

	@Override
	public String getDescription() {
		return "file: '" + zipFile.getName() + "' subdirectory: '" + relativeDir + "'";
	}

	@Override
	public int size() {
		return filesByRelativePathAndName.size();
	}

	@Override
	public void close() throws IOException {
		zipFile.close();
	}

	Directory rootDir = new Directory("ROOT");

	public String getRootFileName() {
		return zipFile.getName();
	}


	public String getZipFileName() {
		return zipFile.getName();
	}
}
