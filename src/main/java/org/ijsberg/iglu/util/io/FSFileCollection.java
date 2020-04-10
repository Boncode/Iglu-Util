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

/**
 */
public class FSFileCollection implements FileCollection {

    protected String baseDir;

    protected Map<String, File> filesByRelativePathAndName = new TreeMap<String, File>();
    protected FileFilterRuleSet includedFilesRuleSet;

    public FSFileCollection(String baseDir, FileFilterRuleSet fileFilterRuleSet) {
        this.baseDir = FileSupport.convertToUnixStylePath(baseDir);
        this.includedFilesRuleSet = fileFilterRuleSet;
        refreshFiles();
    }

	public FSFileCollection(String baseDir) {
    	this(baseDir, new FileFilterRuleSet().setIncludeFilesWithNameMask("*"));
	}

    @Override
    public List<String> getFileNames() {
//        refreshFiles();
        return new ArrayList<>(filesByRelativePathAndName.keySet());
    }

    @Override
    public byte[] getFileContents(String fileName) throws IOException {
        return FileSupport.getBinaryFromFS(filesByRelativePathAndName.get(fileName));
    }

    public File getActualFileByName(String fileName) throws IOException {
        return filesByRelativePathAndName.get(fileName);
    }

    @Override
    public String getFileContentsAsString(String fileName) throws IOException {
        return new String(getFileContents(fileName));
    }

	@Override
	public FileFilterRuleSet getFileFilter() {
		return includedFilesRuleSet;
	}

	@Override
	public boolean containsFile(String fileName) {
		return filesByRelativePathAndName.containsKey(FileSupport.convertToUnixStylePath(fileName));
	}

	@Override
	public Directory getRootDirectory() {
		return rootDir;
	}

	public void refreshFiles() {

        filesByRelativePathAndName.clear();
		rootDir = new Directory("ROOT");

        List<File> files = FileSupport.getFilesInDirectoryTree(baseDir, includedFilesRuleSet);

        for (File file : files) {
			String relativePathAndName = getRelativePathAndName(baseDir, file.getPath());
			filesByRelativePathAndName.put(relativePathAndName, file);
			rootDir.addFile(relativePathAndName);
        }
    }

	public static String getRelativePathAndName(String baseDir, String path) {
		String relativePathAndName = FileSupport.convertToUnixStylePath(path.substring(
                baseDir.length()));
		if(relativePathAndName.startsWith("/")) {
            relativePathAndName = relativePathAndName.substring(1);
        }
		return relativePathAndName;
	}

	@Override
	public String getDescription() {
		return "directory: '" + baseDir + "'";
	}

	@Override
	public int size() {
		return filesByRelativePathAndName.size();
	}

	@Override
	public void close() throws IOException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String getName() {
		return baseDir;
	}

	Directory rootDir = new Directory("ROOT");


	public void delete(String fileName) throws IOException {
		File file = filesByRelativePathAndName.get(fileName);
		java.nio.file.Files.delete(file.toPath());
		refreshFiles();
		//filesByRelativePathAndName.remove(fileName);
		//rootDir.addFile();
	}

	public void save(String fileName, byte[] fileContents) throws IOException {
		File file = FileSupport.createFile(baseDir + "/" + fileName);
		FileSupport.saveBinaryFile(fileContents, new File(baseDir + "/" + fileName));
		filesByRelativePathAndName.put(FileSupport.convertToUnixStylePath(fileName), file);
	}

	public void saveWithDate(String fileName, File srcFile) throws IOException {
		File file = FileSupport.createFile(baseDir + "/" + fileName);
		FileSupport.copyFileKeepDate(srcFile, baseDir + "/" + fileName, true);
		filesByRelativePathAndName.put(FileSupport.convertToUnixStylePath(fileName), file);
	}

	public void copyTo(String newBaseDir) throws IOException {
		for(String fileName : getFileNames()) {
			FileSupport.copyFile(baseDir + "/" + fileName, newBaseDir + "/" + fileName, true);
		}
	}

	public void copyWithDateTo(String newBaseDir) throws IOException {
		for(String fileName : getFileNames()) {
			FileSupport.copyFileKeepDate(baseDir + "/" + fileName, newBaseDir + "/" + fileName);
		}
	}

	public void copyWithDateTo(String fileName, FSFileCollection targetCollection) throws IOException {
		targetCollection.saveWithDate(fileName, new File(baseDir + "/" + fileName));
	}
}
