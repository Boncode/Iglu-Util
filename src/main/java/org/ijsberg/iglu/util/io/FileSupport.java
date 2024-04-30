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

import org.ijsberg.iglu.util.ResourceException;
import org.ijsberg.iglu.util.collection.CollectionSupport;
import org.ijsberg.iglu.util.collection.ListTreeMap;
import org.ijsberg.iglu.util.formatting.PatternMatchingSupport;
import org.ijsberg.iglu.util.misc.Line;
import org.ijsberg.iglu.util.misc.StringSupport;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Supports retrieval and deletion of particular files in a directory structure
 * as well as other file system manipulation.
 */
public abstract class FileSupport {
	private static final int COPY_BUFFER = 100000;

	/**
	 * Retrieves all files from a directory and its subdirectories.
	 *
	 * @param path path to directory
	 * @return A list containing the found files
	 */
	public static List<File> getFilesInDirectoryTree(String path) {
		File directory = new File(path);
		return getContentsInDirectoryTree(directory, "*", true, false);
	}

	public static String[] listDirsInDirectory(String path) {
		ArrayList<String> result = new ArrayList();
		File directory = new File(path);
		for(String fileName : directory.list()) {
			File file = new File(path + '/' + fileName);
			if(file.isDirectory()) {
				result.add(fileName);
			}
		}
		return result.toArray(new String[0]);
	}

	public static String[] listFilesInDirectory(String path, FileFilterRuleSet ruleSet) {
		ArrayList<String> result = new ArrayList();
		File directory = new File(path);
		if(directory.list() != null) {
			for (String fileName : directory.list()) {
				if (ruleSet.fileMatchesRules(new File(path + "/" + fileName))) {
					result.add(fileName);
				}
			}
		}
		return result.toArray(new String[0]);
	}

	public static void assertDirExists(File dir) {
		if(!dir.exists() || !dir.isDirectory()) {
			throw new ResourceException(dir.getAbsolutePath() + " is not a valid directory");
		}
	}

	public static boolean dirExists(String dirLoc) {
		File dir = new File(dirLoc);
		return dir.exists() && dir.isDirectory();
	}

	public static boolean fileExists(String fileLoc) {
		File file = new File(fileLoc);
		return file.exists();
	}

	public static void assertDirExistsFallbackCreate(File dir) {
		try {
			assertDirExists(dir);
		} catch (ResourceException e) {
			if (!dir.exists()) {
				try {
					createDirectory(dir.getPath());
				} catch (IOException ignore) {
					throw e;
				}
			} else {
				throw e;
			}
		}
	}

	/**
	 * Retrieves all files from a directory and its subdirectories.
	 *
	 * @param directory directory
	 * @return a list containing the found files
	 */
	public static List<File> getFilesInDirectoryTree(File directory) {
		return getContentsInDirectoryTree(directory, "*", true, false);
	}


	/**
	 * Retrieves files for a given mask from a directory and its subdirectories.
	 *
	 * @param path root of directory tree
	 * @param includeMask exact filename, or mask containing wildcards
	 * @return A list containing the found files
	 */
	public static List<File> getFilesInDirectoryTree(String path, String includeMask) {
		File file = new File(path);
		return getContentsInDirectoryTree(file, includeMask, true, false);
	}

	/**
	 * Retrieves files for a given mask from a directory and its subdirectories.
	 *
	 * @param path root of directory tree
	 * @param includeRuleSet rule set defining precisely which files to include
	 * @return A list containing the found files
	 */
	public static List<File> getFilesInDirectoryTree(String path, FileFilterRuleSet includeRuleSet) {
		File file = new File(path);
		return getContentsInDirectoryTree(file, includeRuleSet, true, false);
	}


	/**
	 */
	public static List<File> replaceStringsInFilesInDirectoryTree (
			String path, FileFilterRuleSet includeRuleSet,
			String searchString, String replaceString) throws IOException {
		File directory = new File(path);
		List<File> files = getContentsInDirectoryTree(directory, includeRuleSet, true, false);

		for(File file : files) {
			String text = getTextFileFromFS(file);
			String modifiedText = StringSupport.replaceAll(text, searchString, replaceString);
			saveTextFile(modifiedText, file);
		}
		return files;
	}



	/**
	 * Retrieves all files from a directory and its subdirectories
	 * matching the given mask.
	 *
	 * @param file directory
	 * @param includeMask mask to match
	 * @return a list containing the found files
	 */
	public static List<File> getFilesInDirectoryTree(File file, String includeMask) {
		return getContentsInDirectoryTree(file, includeMask, true, false);
	}


	/**
	 * Retrieves contents from a directory and its subdirectories matching a given mask.
	 *
	 * @param directory directory
	 * @param includeMask file name to match
	 * @param returnFiles return files
	 * @param returnDirs return directories
	 * @return a list containing the found contents
	 */
	private static List<File> getContentsInDirectoryTree(File directory, String includeMask, boolean returnFiles, boolean returnDirs) {
		return getContentsInDirectoryTree(directory, new FileFilterRuleSet().setIncludeFilesWithNameMask(includeMask), returnFiles, returnDirs);
	}


	/**
	 * Retrieves contents from a directory and its subdirectories matching a given rule set.
	 *
	 * @param directory directory
	 * @param ruleSet file name to match
	 * @param returnFiles return files
	 * @param returnDirs return directories
	 * @return a list containing the found contents
	 */
	private static List<File> getContentsInDirectoryTree(File directory, FileFilterRuleSet ruleSet, boolean returnFiles, boolean returnDirs) {
		ListTreeMap<String, File> sortedResult = getSortedContentsInDirectoryTree(directory, ruleSet, returnFiles, returnDirs);
		return sortedResult.values();
	}

	/**
	 * Retrieves contents from a directory and its subdirectories matching a given rule set.
	 *
	 * @param directory directory
	 * @param ruleSet file name to match
	 * @param returnFiles return files
	 * @param returnDirs return directories
	 * @return a list containing the found contents
	 */
	private static ListTreeMap<String, File> getSortedContentsInDirectoryTree(File directory, FileFilterRuleSet ruleSet, boolean returnFiles, boolean returnDirs) {
		ListTreeMap<String, File> sortedResult = new ListTreeMap<String, File>();
		if (directory != null && directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {

					if (files[i].isDirectory()) {
						if (returnDirs && ruleSet.fileMatchesRules(files[i])) {
							sortedResult.put(files[i].getName(), files[i]);
						}
						sortedResult.putAll(getSortedContentsInDirectoryTree(files[i], ruleSet, returnFiles, returnDirs));
					}
					else if (returnFiles && ruleSet.fileMatchesRules(files[i])) {
//						System.out.println(files[i].getName());
						sortedResult.put(files[i].getName(), files[i]);
					}
				}
			}
		}
		return sortedResult;
	}

	/**
	 * Retrieves all directories from a directory and its subdirectories.
	 *
	 * @param path path to directory
	 * @return A list containing the found directories
	 */
	public static List<File> getDirectoriesInDirectoryTree(String path) {
		File file = new File(path);
		return getContentsInDirectoryTree(file, "*", false, true);
	}


	/**
	 * Retrieves all directories from a directory and its subdirectories.
	 *
	 * @param path path to directory
	 * @param includeMask file name to match
	 * @return A list containing the found directories
	 */
	public static List<File> getDirectoriesInDirectoryTree(String path, String includeMask) {
		File file = new File(path);
		return getContentsInDirectoryTree(file, includeMask, false, true);
	}


	/**
	 * Retrieves all files from a directory and its subdirectories.
	 *
	 * @param path path to directory
	 * @param includeMask file name to match
	 * @return a list containing the found files
	 */
	public static List<File> getFilesAndDirectoriesInDirectoryTree(String path, String includeMask) {
		File file = new File(path);
		return getContentsInDirectoryTree(file, includeMask, true, true);
	}

	/**
	 * Tries to retrieve a class as File from all directories mentioned in system property java.class.path
	 *
	 * @param className class name as retrieved in myObject.getClass().getName()
	 * @return a File if the class file was found or null otherwise
	 */
	public static File getClassFileFromDirectoryInClassPath(String className) {
		String fileName = StringSupport.replaceAll(className, ".", "/");
		fileName += ".class";
		return getFileFromDirectoryInClassPath(fileName, System.getProperty("java.class.path"));
	}


	/**
	 * Locates a file in the classpath.
	 *
	 * @param fileName
	 * @param classPath
	 * @return the found file or null if the file can not be located
	 */
	public static File getFileFromDirectoryInClassPath(String fileName, String classPath) {

		Collection<String> paths = StringSupport.split(classPath, ";:", false);

		for(String singlePath : paths) {
			File dir = new File(singlePath);
			if (dir.isDirectory()) {
				File file = new File(singlePath + '/' + fileName);
				if (file.exists()) {
					return file;
				}
			}
		}
		return null;
	}

	/**
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBinaryFromFS(String fileName) throws IOException {
		File file = new File(fileName);
		if (file.exists()) {
			return getBinaryFromFS(file);
		}
		throw new FileNotFoundException("file '" + fileName + "' does not exist");
	}

    /**
     * @param existingTextFile
     * @return
     * @throws IOException
     */
    public static String getTextFileFromFS(File existingTextFile) throws IOException {
        return new String(getBinaryFromFS(existingTextFile));
    }

	/**
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBinaryFromFS(File existingFile) throws IOException {
		FileInputStream in = new FileInputStream(existingFile);
		try {
			return StreamSupport.absorbInputStream(in);
		}
		finally {
			in.close();
		}
	}

	/**
	 * Tries to retrieve a class as ZipEntry from all jars mentioned in system property java.class.path
	 *
	 * @param className class name as retrieved in myObject.getClass().getName()
	 * @return a ZipEntry if the class file was found or null otherwise
	 */
	public static ZipEntry getClassZipEntryFromZipInClassPath(String className) throws IOException {
		String fileName = StringSupport.replaceAll(className, ".", "/");
		fileName += ".class";

		Collection<String> jars = StringSupport.split(System.getProperty("java.class.path"), ";:", false);
		for(String jarFileName : jars) {
			if (jarFileName.endsWith(".jar") || jarFileName.endsWith(".zip")) {
				ZipEntry entry = getZipEntryFromZip(fileName, jarFileName);
				if (entry != null) {
					return entry;
				}
			}
		}
		return null;
	}

	public static void zip(String path, String zipFileName, String includeMask) throws IOException {
		String zipFilePath = convertToUnixStylePath(path);
		List<File> files = getFilesInDirectoryTree(path, includeMask);
		zip(zipFileName, zipFilePath, files);
	}

	private static void zip(String zipFileName, String zipFilePath, List<File> files) throws IOException {
		FileStreamProvider output = new ZipFileStreamProvider(zipFileName);
		for (File file : files) {
			String fullFileName = convertToUnixStylePath(file.getPath());
			String fileName = fullFileName.substring(zipFilePath.length() + (zipFilePath.endsWith("/") ? 0 : 1));
			OutputStream outputStream = output.createOutputStream(fileName);
			copyFileResource(fullFileName, outputStream);
			output.closeCurrentStream();
		}
		output.close();
	}

	public static void zip(File file) throws IOException {
		String dirName = file.getParent();
		FileData fileData = new FileData(file.getName());
		FSFileCollection fileCollection = new FSFileCollection(dirName, new FileFilterRuleSet().setIncludeFilesWithNameMask("*/" + fileData.getFileName()));
		FileSupport.zip(dirName + "/" + fileData.getFileName() + ".zip", fileCollection);
	}

	public static void zip(String zipFileName, FileCollection fileCollection) throws IOException {
		FileStreamProvider output = new ZipFileStreamProvider(zipFileName);
		for (String fileName : fileCollection.getFileNames()) {
			OutputStream outputStream = output.createOutputStream(fileName);
			copyFileResource(fileCollection.getFileContents(fileName), outputStream);
			output.closeCurrentStream();
		}
		output.close();
	}

	public static void unzip(ZipFile zipFile, String targetPath, FileFilterRuleSet ruleSet) throws IOException{

		ArrayList<ZipEntry> entries = getContentsFromZipFile(zipFile, ruleSet);
		for(ZipEntry entry : entries) {
			if(!entry.isDirectory()) {
				File file = new File(targetPath + "/" + entry.getName());
				if(file.getParent() != null) {
					new File(file.getParent()).mkdirs();
				}
				InputStream in = zipFile.getInputStream(entry);
				OutputStream out = new FileOutputStream(file);
				try {
					StreamSupport.absorbInputStream(in, out);
				}
				finally {
					out.close();
					in.close();
				}
			}
		}
	}

	public static void unzip(ZipFile zipFile, String targetPath) throws IOException {
		unzip(zipFile, targetPath, new FileFilterRuleSet().setIncludeFilesWithNameMask("*"));
	}

	public static void unzip(String zipFilePath, String targetPath) throws IOException {
		ZipFile zipFile = new ZipFile(zipFilePath);
		try {
			unzip(zipFile, targetPath, new FileFilterRuleSet().setIncludeFilesWithNameMask("*"));
		} finally {
			if(zipFile != null) {
				zipFile.close();
			}
		}
	}


	public static byte[] getBinaryFromZip(String fileName, String zipFileName) throws IOException {
		ZipFile zipFile = new ZipFile(zipFileName);
		try {
			return getBinaryFromZip(fileName, zipFile);
		} finally {
			zipFile.close();
		}
	}

	public static byte[] getBinaryFromZip(String fileName, ZipFile zipFile) throws IOException {

        ZipEntry entry = zipFile.getEntry(FileSupport.convertToUnixStylePath(fileName));
        if (entry == null) {
			entry = zipFile.getEntry("/" + FileSupport.convertToUnixStylePath(fileName));
			if (entry == null) {
	            throw new IOException("entry " + FileSupport.convertToUnixStylePath(fileName) + " not found in jar " + zipFile.getName());
			}
        }
       InputStream in = zipFile.getInputStream(entry);
        try {
            return StreamSupport.absorbInputStream(in);
        }
        finally {
            in.close();
        }
	}

	public static void putTextFileInZip(String zipFileName, String targetFileName, String fileContents) throws IOException {

		File tmpFile = File.createTempFile("FileSupport", "replaceBinaryInZip");
		FileSupport.writeTextFile(tmpFile.getPath(), fileContents);
		Path tmpFilePath = Paths.get(tmpFile.getPath());
		Path zipFilePath = Paths.get(zipFileName);
		FileSystem fs = null;
		try {
			fs = FileSystems.newFileSystem(zipFilePath, null);
			Path fileInsideZipPath = fs.getPath(targetFileName);
			if(Files.exists(fileInsideZipPath)) {
				Files.delete(fileInsideZipPath);
			}
			Files.copy(tmpFilePath, fileInsideZipPath);
		} finally {
			if(fs != null && fs.isOpen()){
				fs.close();
			}
			deleteFile(tmpFile);
		}
	}

	public static void mergeZipFiles(String zipFileName1, String zipFileName2, String targetZipFileName) throws IOException {
		if(!zipFileName1.equals(targetZipFileName)) {
			copyFile(zipFileName1, targetZipFileName, true);
		}
		ZippedFileCollection fileCollection = new ZippedFileCollection(zipFileName2, new FileFilterRuleSet().setIncludeFilesWithNameMask("*"));
		mergeInZipFile(targetZipFileName, fileCollection);
		fileCollection.close();
	}

	public static void mergeInZipFile(String zipFileName, FileCollection filesToMerge) throws IOException {

		File tmpDir = createTmpDir("mergeInZipfile");
		Path zipFilePath = Paths.get(zipFileName);

		FileSystem fs = null;
		try {
			fs = FileSystems.newFileSystem(zipFilePath, null);
			for(String fileToCopyName : filesToMerge.getFileNames()) {
				String fileToCopyPath = tmpDir.getPath() + "/" + fileToCopyName;
				saveBinaryFile(filesToMerge.getFileContents(fileToCopyName), FileSupport.createFile(fileToCopyPath));
				Path tmpFilePath = Paths.get(fileToCopyPath);
				Path fileInsideZipPath = fs.getPath(fileToCopyName);
				//System.out.println(fileInsideZipPath);
				if(Files.exists(fileInsideZipPath)) {
					Files.delete(fileInsideZipPath);
				} else {
					if(fileInsideZipPath.getParent() != null) {
						Files.createDirectories(fileInsideZipPath.getParent());
					}
				}
				Files.copy(tmpFilePath, fileInsideZipPath);
			}
		} finally {
			if(fs != null && fs.isOpen()){
				fs.close();
			}
			deleteFile(tmpDir);
		}
	}

	public static void mergeInZipFile(String zipFileName, String targetFilePathAndName, byte[] fileContents) throws IOException {

		File tmpDir = createTmpDir("mergeInZipfile");
		Path zipFilePath = Paths.get(zipFileName);

		FileSystem fs = null;
		try {
			fs = FileSystems.newFileSystem(zipFilePath, null);
//			for(String fileToCopyName : filesToMerge.getFileNames()) {
				String fileToCopyPath = tmpDir.getPath() + "/" + targetFilePathAndName;
				saveBinaryFile(fileContents, FileSupport.createFile(fileToCopyPath));
				Path tmpFilePath = Paths.get(fileToCopyPath);
				Path fileInsideZipPath = fs.getPath(targetFilePathAndName);
				//System.out.println(fileInsideZipPath);
				if(Files.exists(fileInsideZipPath)) {
					Files.delete(fileInsideZipPath);
				} else {
					if(fileInsideZipPath.getParent() != null) {
						Files.createDirectories(fileInsideZipPath.getParent());
					}
				}
				Files.copy(tmpFilePath, fileInsideZipPath);
//			}
		} finally {
			if(fs != null && fs.isOpen()){
				fs.close();
			}
			deleteFile(tmpDir);
		}
	}

	public static byte[] getBinaryFromJar(String fileName, String jarFileName) throws IOException {
       //zipfile is opened for READ on instantiation
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(jarFileName);
            return getBinaryFromZip(fileName, zipfile);
        }
        catch (IOException e) {
        	throw new IOException("unable to read '" + fileName + "' from " + jarFileName, e);
		}
        finally {
            if(zipfile != null) {
            	zipfile.close();
            }
        }
    }

    public static String getTextFileFromZip(String fileName, ZipFile zipFile) throws IOException {
        return new String(getBinaryFromZip(fileName, zipFile));
    }

	public static byte[] getBinaryFromClassPath(String fileName, String classPath) throws IOException {
		byte[] retval = null;
		Collection<String> paths = StringSupport.split(classPath, ";:", false);
		for(String path : paths) {
			if (path.endsWith(".zip") || path.endsWith(".jar")) {
				retval = getBinaryFromJar(fileName, path);
			}
			else {
				retval = getBinaryFromFS(path + '/' + fileName);
			}
			if (retval == null) {
				File dir = new File(path);
				if (dir.exists() && dir.isDirectory()) {
					Collection<File> jars = FileSupport.getFilesInDirectoryTree(dir, "*.jar");
					for(File jar : jars) {
						retval = getBinaryFromJar(fileName, jar.getPath());
						if (retval != null) {
							return retval;
						}
					}

				}
			}
			if (retval != null) {
				return retval;
			}
		}
		return null;
	}


	public static byte[] getBinaryFromClassLoader(String path) throws IOException {
		InputStream in = getInputStreamFromClassLoader(path);
		byte[] retval = StreamSupport.absorbInputStream(in);
		in.close();
		return retval;
	}

	public static List<String> loadTextFromClassLoader(String path) throws IOException {
		List<String> retval = new ArrayList<>();
		byte[] bytes = FileSupport.getBinaryFromClassLoader(path);
		String string = new String(bytes);
		StringReader reader = new StringReader(string);
		BufferedReader bf = new BufferedReader(reader);
		String line;
		while((line = bf.readLine()) != null) {
			retval.add(line);
		}
		return retval;
	}

	public static boolean resourceIsDirectory(Class clazz, String path) throws URISyntaxException {
		URL dirURL = Thread.currentThread().getContextClassLoader().getResource(path);
		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			return new File(dirURL.toURI()).list() != null;
		}
		return true;
	}

	public static List<String> getResourceFolderFilesRecursive(Class clazz, String path) throws URISyntaxException, IOException {
		return getResourceFolderFilesRecursive(clazz, path, new FileFilterRuleSet().setIncludeFilesWithNameMask("*"));
	}

	public static List<String> getResourceFolderFilesRecursive(Class clazz, String path, FileFilterRuleSet filter) throws URISyntaxException, IOException {
		List<String> fileNames = new ArrayList<>();
		List<String> fileNamesInDir = getResourceFolderFiles(clazz, path);
		for(String fileNameInDir : fileNamesInDir) {
			String fullFileName = "".equals(path) ? fileNameInDir : path + "/" + fileNameInDir;
			if(!resourceIsDirectory(clazz, fullFileName)) {
				if(filter.fileNameMatchesRules(fullFileName)) {
					fileNames.add(fullFileName);
				}
			} else {
				fileNames.addAll(getResourceFolderFilesRecursive(clazz, fullFileName));
			}
		}
		return fileNames;
	}

	public static List<String> getResourceFolderFiles(Class clazz, String path) throws URISyntaxException, IOException {
		String unixStylePath = convertToUnixStylePath(path);
		URL dirURL = Thread.currentThread().getContextClassLoader().getResource(unixStylePath);
		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			return Arrays.asList(new File(dirURL.toURI()).list());
		} else {

		//if (dirURL == null) {
			/*
			 * In case of a jar file, we can't actually find a directory.
			 * Have to assume the same jar as clazz.
			 */
			String me = clazz.getName().replace(".", "/")+".class";
			dirURL = clazz.getClassLoader().getResource(me);
//		}

			if (dirURL.getProtocol().equals("jar")) {
				/* A JAR path */
				String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
				JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
				Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
				Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
				while (entries.hasMoreElements()) {
					JarEntry jarEntry = entries.nextElement();
					String name = jarEntry.getName();
					if (name.startsWith(unixStylePath)) { //filter according to the path
						String entry = name.substring(unixStylePath.length());
						int checkSubdir = entry.indexOf("/");
						if (checkSubdir >= 0) {
							// if it is a subdirectory, we just return the directory name
							entry = entry.substring(0, checkSubdir);
						}
						result.add(entry);
					}
				}
				return new ArrayList<>(result);
			} else {
				String pathToFile = dirURL.getPath().substring(0, dirURL.getPath().length() - me.length()) + unixStylePath;
				return Arrays.asList(new File(pathToFile).list());
			}
		}

		//throw new UnsupportedOperationException("Cannot list files for path: " + path + ", URL: " + dirURL + ", protocol: " + dirURL.getProtocol());
	}

	/**
	 *
	 * @param path path with regular path separators ('/')
	 * @return
	 * @throws IOException
	 */
	public static InputStream getInputStreamFromClassLoader(String path) throws IOException {
		ClassLoader classLoader = FileSupport.class.getClassLoader();
		InputStream retval = classLoader.getResourceAsStream(path);

		if(retval == null) {
			throw new IOException("class loader can not load resource '" + path  + "'");
		}
		return retval;
	}

	/**
	 * @param pathToResource
	 * @param outputPath
	 * @throws IOException
	 */
	public static void copyClassLoadableResourceToFileSystem(String pathToResource, String outputPath) throws IOException{

		File outputFile = createFile(outputPath);
		if(outputFile.isDirectory()) {
			outputFile = new File(outputFile.getPath() + '/' + getFileNameFromPath(pathToResource));
		}
		OutputStream output = new FileOutputStream(outputFile);
		copyClassLoadableResource(pathToResource, output);
		output.close();
	}

	/**
	 * @param pathToResource
	 * @param output
	 * @throws IOException
	 */
	public static int copyClassLoadableResource(String pathToResource, OutputStream output) throws IOException{

		InputStream input = getInputStreamFromClassLoader(convertToUnixStylePath(pathToResource));
		try {
			return StreamSupport.absorbInputStream(input, output);
		} finally {
			input.close();
		}
	}

	public static void copyFileResource(String pathToResource, OutputStream output) throws IOException{

		InputStream input = new FileInputStream(pathToResource);
		try {
			StreamSupport.absorbInputStream(input, output);
		} finally {
			input.close();
		}
	}

	public static void copyFileResource(byte[] fileContents, OutputStream output) throws IOException{
		StreamSupport.writeToOutputStream(fileContents, output);
	}

	public static boolean containsFileInZip(String fileName, ZipFile zipFile) {
		return zipFile.getEntry(fileName) != null;
	}


	public static ZipEntry getZipEntryFromZip(String fileName, String zipFileName) throws IOException {
		//zipfile is opened for READ on instantiation
		ZipFile zipfile = new ZipFile(zipFileName);
		return zipfile.getEntry(fileName);
	}

    public static ArrayList<ZipEntry> getContentsFromZipFile(ZipFile zipFile, FileFilterRuleSet ruleSet) {
        ArrayList<ZipEntry> result = new ArrayList<ZipEntry>();

        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
        while(zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = zipEntries.nextElement();

			if(ruleSet.fileMatchesRules(zipEntry, zipFile)) {
                result.add(zipEntry);
            }
        }
        return result;
    }


	/**
	 * Creates a file.
	 * The file, and the directory structure is created physically,
	 * if it does not exist already.
	 *
	 * @param filename
	 * @return
	 */
	public static File createFile(String filename) throws IOException {
		File file = new File(filename);
		if (!file.exists()) {
			if(file.getParent() != null) {
				File path = new File(file.getParent());
				path.mkdirs();
			} try {
				file.createNewFile();
			} catch (IOException e) {
				throw new IOException("file " + filename + " cannot be created", e);
			}
		}
		return file;
	}

	public static File assertDirectoryExists(String directoryName) {
		try {
			return createDirectory(directoryName);
		} catch (IOException e) {
			throw new ResourceException("directory '" + directoryName + "' does not exist", e);
		}
	}

	public static File createDirectory(String directoryname) throws IOException {
		File file = new File(directoryname);
		if (!file.exists()) {
			if(!file.mkdirs()) {
				throw new IOException("unable to create missing directory '" + directoryname + "'");
			}
		}
		return file;
	}

	public static File writeTextFile(String filename, String text) throws IOException {

		File file = createFile(filename);
		saveTextFile(text, file);
		return file;
	}

	/**
	 * Deletes all files and subdirectories from a directory.
	 *
	 * @param path
	 */
	public static void emptyDirectory(String path) throws IOException {
		deleteContentsInDirectoryTree(path, null);
	}

	/**
	 * Deletes all files and subdirectories from a directory.
	 *
	 * @param file
	 */
	public static void emptyDirectory(File file) throws IOException {
		deleteContentsInDirectoryTree(file, null);
	}


	public static void deleteActualFile(String fileName) throws IOException {
		try {
			Path path = Paths.get(fileName);
			Files.delete(path);
		} catch (IOException e) {
			throw new IOException("unable to delete file " + fileName, e);
		}
	}

	/**
	 * Deletes a file or a directory including its contents;
	 *
	 * @param file
	 */
	public static void deleteFile(File file) throws IOException {
		if(file.exists()) {
			deleteContentsInDirectoryTree(file, null);
			deleteActualFile(file.getPath());
		}
	}

	public static void deleteFile(String fileName) throws IOException {
		deleteFile(new File(fileName));
	}

	/**
	 * Copies a file.
	 *
	 * @param fileName
	 * @param newFileName
	 * @param overwriteExisting
	 * @throws IOException
	 */
	public static void copyFile(String fileName, String newFileName, boolean overwriteExisting) throws IOException {
		copyFile(new File(fileName), newFileName, overwriteExisting);
	}

	public static void copyFileKeepDate(String fileName, String newFileName) throws IOException {
		copyFile(new File(fileName), newFileName, true, true);
	}

	public static void touchFile(String fileName) throws IOException {
		File file = createFile(fileName);
		file.setLastModified(System.currentTimeMillis());
	}

	public static void renameFile(String fileName, String newFileName, boolean overwriteExisting) throws IOException {
		File destFile = new File(newFileName);
		if(destFile.exists()) {
			if(overwriteExisting) {
				boolean deleteSucceeded = destFile.delete();
				if(!deleteSucceeded) {
					throw new IOException("file '" + destFile.getAbsolutePath() + "' cannot be renamed; possibly it's in use by another process");
				}
			} else {
				throw new IOException("file '" + destFile.getAbsolutePath() + "' already exists");
			}
		}
		File sourceFile = new File(fileName);
		sourceFile.renameTo(destFile);
	}

	public static void moveFile(String fileName, String newFileName, boolean overwriteExisting) throws IOException {
		File sourceFile = new File(fileName);
		copyFile(new File(fileName), newFileName, overwriteExisting);
		deleteFile(sourceFile);
	}

	public static void copyFileKeepDate(File file, String newFileName, boolean overwriteExisting) throws IOException {
		copyFile(file, newFileName, overwriteExisting, true);
	}

	public static void copyFile(File file, String newFileName, boolean overwriteExisting) throws IOException {
		copyFile(file, newFileName, overwriteExisting, false);
	}

	public static void copyFile(File file, String newFileName, boolean overwriteExisting, boolean keepDate) throws IOException {

		if (!file.exists()) {
			throw new IOException("file '" + file.getAbsolutePath() + "' does not exist");
		}
		if (file.isDirectory()) {
			copyDirectory(file.getAbsolutePath(), newFileName, overwriteExisting);
			return;
		}
		File newFile = new File(newFileName);
		if(newFile.isDirectory()) {
			newFile = new File(newFileName + '/' + file.getName());
		}
		if (!overwriteExisting && newFile.exists()) {
			throw new IOException("file '" + newFile.getAbsolutePath() + "' already exists");
		} else {
			if(file.getParent() != null) {
				newFile.getParentFile().mkdirs();
			}
			if(!newFile.exists()) {
				newFile.createNewFile();
			}
		}
		byte[] buffer = new byte[COPY_BUFFER];

		int read = 0;


		FileInputStream in = new FileInputStream(file);
		FileOutputStream out = new FileOutputStream(newFile);

		while ((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
		out.close();
		in.close();

		if(keepDate) {
			newFile.setLastModified(file.lastModified());
		}
	}

	public static void copyDirectory(String directoryName, String newDirectoryName, boolean overwriteExisting) throws IOException {
		File srcDir = new File(directoryName);
		if(!srcDir.exists()) {
			throw new FileNotFoundException(srcDir.getAbsolutePath() + " does not exist");
		}
		File newDirectory = new File(newDirectoryName);
		newDirectory.mkdirs();
		FileCollection fileCollection = new FSFileCollection(directoryName, new FileFilterRuleSet());
		for(String fileName : fileCollection.getFileNames()) {
			copyFile(directoryName + "/" + fileName, newDirectoryName + "/" + fileName, overwriteExisting);
		}
		fileCollection.close();
	}

	public static void copyDirectoryFromZipFile(String zipFileName, String sourceDirectoryName, String newDirectoryName, boolean overwriteExisting) throws IOException {
		File srcFile = new File(zipFileName);
		if(!srcFile.exists()) {
			throw new FileNotFoundException(srcFile.getAbsolutePath() + " does not exist");
		}
		File newDirectory = new File(newDirectoryName);
		newDirectory.mkdirs();
		ZippedFileCollection fileCollection = new ZippedFileCollection(zipFileName, new FileFilterRuleSet().setIncludeFilesWithNameMask(sourceDirectoryName + "/*"));
		for(String fileName : fileCollection.getFileNotDirectoryNames()) {
			byte[] fileContents = fileCollection.getFileContents(fileName);
			FileData fileData = new FileData(fileName);
			File newFile =  new File(newDirectoryName + "/" + fileData.getFileName());
			saveBinaryFile(fileContents, newFile);
		}
		fileCollection.close();
	}

	/**
	 * Deletes from a directory all files and subdirectories targeted by a given mask.
	 * The method will recurse into subdirectories.
	 *
	 * @param path
	 * @param includeMask
	 */
	public static void deleteContentsInDirectoryTree(String path, String includeMask) throws IOException {
		deleteContentsInDirectoryTree(new File(path), includeMask);
	}

	/**
	 * Deletes from a directory all files and subdirectories targeted by a given mask.
	 * The method will recurse into subdirectories.
	 *
	 * @param root
	 * @param includeMask
	 */
	public static void deleteContentsInDirectoryTree(File root, String includeMask) throws IOException {
		Collection<File> files = getContentsInDirectoryTree(root, includeMask, true, true);
		for(File file : files) {
			if (file.exists()) {//file may meanwhile have been deleted
				if (file.isDirectory()) {
					//empty directory
					emptyDirectory(file.getAbsolutePath());
				}
				deleteFile(file);
			}
		}
	}



	/**
	 * @param file
	 * @param searchString
	 * @return a map containing the number of occurrences of the search string in lines, if 1 or more, keyed and sorted by line number
	 * @throws IOException if the file could not be found or is a directory or locked
	 */
	public static Map<Integer, Integer> countOccurencesInTextFile(File file, String searchString) throws IOException {
		TreeMap<Integer, Integer> retval = new TreeMap<Integer, Integer>();
		InputStream input;

		input = new FileInputStream(file);

		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		String line = reader.readLine();
		int lineCount = 1;
		while (line != null) {
			int occCount = -1;
			while ((occCount = line.indexOf(searchString, occCount + 1)) != -1) {
				Integer lineNo = new Integer(lineCount);
				Integer nrofOccurrencesInLine = (Integer) retval.get(lineNo);
				if (nrofOccurrencesInLine == null) {
					nrofOccurrencesInLine = new Integer(1);
				}
				else {
					nrofOccurrencesInLine = new Integer(nrofOccurrencesInLine.intValue() + 1);
				}
				retval.put(lineNo, nrofOccurrencesInLine);
				System.out.println(">" + line + "<");
			}


			lineCount++;
			line = reader.readLine();
		}
		return retval;
	}

	/**
	 * Prints a message to a user that invokes FileSupport commandline.
	 */
	private static void printUsage() {
		System.out.println("Commandline use of FileSupport only supports recursive investigation of directories");
		System.out.println("Usage: java FileSupport -{d(elete)|s(how)} <path> [<mask>] [-f(ind) <word>]");
	}



	/**
	 * @param files
	 * @param searchString
	 */
	public static void printOccurringString(List<File> files, String searchString) {
		//TODO use unix conventions for arguments
		//TODO print message if no files found
		for (File file : files) {
			try {
				Map<Integer, Integer> occurrences = countOccurencesInTextFile(file, searchString);
				if (occurrences.size() > 0) {
					int total = 0;
					StringBuffer message = new StringBuffer();
					for (Integer lineNo : occurrences.keySet()) {
						Integer no = (Integer) occurrences.get(lineNo);
						total += no.intValue();
						message.append("line " + lineNo + ": " + no + "\n");
					}
					System.out.println(total + " occurrence" +
							(total > 1 ? "s" : "") + " found in file '" +
							file.getAbsolutePath() + "'\n" + message);
				}
			}
			catch (IOException e) {
				System.out.println("error while trying to read file " + file.getAbsolutePath() + " with message: " + e.getMessage());
			}
		}
	}

	/**
	 * Converts backslashes into forward slashes.
	 *
	 * @param path
	 * @return
	 */
	public static String convertToUnixStylePath(String path) {
		String retval = StringSupport.replaceAll(path, "\\", "/");
		retval = StringSupport.replaceAll(retval, new String[]{"///", "//"}, new String[]{"/", "/"});
		return retval;
	}

	public static String convertToWindowsStylePath(String path) {
		String retval = StringSupport.replaceAll(path, "/", "\\");
		retval = StringSupport.replaceAll(retval, "\\\\", "\\");
		return retval;
	}

	public static String getUnixStyleAbsolutePath(File file) {
		String retval = file.getAbsolutePath();
		if(retval.length() >= 2 && retval.charAt(1) == ':') {
			retval = retval.substring(2);
		}
		return convertToUnixStylePath(retval);
	}

	public static String getFileNameFromPath(String path) {
		String unixStylePath = convertToUnixStylePath(path);
		if(unixStylePath.endsWith("/")) {
			throw new IllegalArgumentException("path '" + path + "' points to a directory");
		}
		return unixStylePath.substring(unixStylePath.lastIndexOf('/') + 1);
	}

	public static String getDirNameFromPath(String path) {
		String unixStylePath = convertToUnixStylePath(path);
		return unixStylePath.substring(0, unixStylePath.lastIndexOf('/') + 1);
	}


	public static File createTmpDir() throws IOException {
		File file = File.createTempFile("iglu_util_test_", null);
		file.delete();
		file.mkdirs();
		return file;
	}

	public static File createTmpDir(String prefix) throws IOException {
		File file = File.createTempFile(prefix, null);
		file.delete();
		file.mkdirs();
		return file;
	}

	public static List<String> convertToStringList(List<Line> lines) {
		List<String> stringList = new ArrayList<>();
		for(Line line : lines) {
			stringList.add(line.getLine());
		}
		return stringList;
	}

	public static List<Line> convertToTextFile(String input) {
		return convertToTextFile(input, false);
	}

	public static List<Line> convertToTextFile(String input, boolean skipEmptyLines) {

		return convertToTextFile(null, input, skipEmptyLines);
	}

	public static List<Line> convertToTextFile(String fileName, String input, boolean skipEmptyLines) {

		BufferedReader reader = new BufferedReader(new StringReader(input));
		List<Line> lines = new ArrayList<Line>();
		String line; int count = 0;
		try {
			while ((line = reader.readLine()) != null) {
				count++;
				if (!skipEmptyLines || !line.trim().isEmpty()) {
					lines.add(new Line(fileName, count, line));
				}
			}
		} catch (IOException ioe) {
			throw new RuntimeException("unexpected exception while reading from StringReader", ioe);
		}
		return lines;
	}


	public static List<Line> loadTextFile(String encoding, File file) throws IOException {
		return findLinesInTextFile(encoding, file, null);
	}

	public static List<Line> loadTextFile(File file) throws IOException {
		return findLinesInTextFile(file, null);
	}

    public static void saveTextFile(List<Line> lines, File file) throws IOException {

		createFile(file.getPath());
 		FileOutputStream outputStream = new FileOutputStream(file);
		PrintStream printStream = new PrintStream(outputStream);
		int nrLinesSaved = 0;
        for(Line line : lines) {
            nrLinesSaved++;
			if(lines.size() == nrLinesSaved) {
				printStream.print(line.getLine());
			} else {
				printStream.println(line.getLine());
			}
        }
		outputStream.close();
    }

	public static void saveTextFile(String text, String fileName) throws IOException {
		saveTextFile(text, createFile(fileName));
	}

	public static void appendToTextFile(String text, String fileName) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fileName, true);
			PrintStream printStream = new PrintStream(outputStream);
			printStream.println(text);
			outputStream.close();
		} catch (IOException e) {
			throw new ResourceException(e);
		}
	}

	public static void saveTextFile(String text, File file) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(file);
		PrintStream printStream = new PrintStream(outputStream);
		printStream.print(text);
		outputStream.close();
	}

	public static void saveBinaryFile(byte[] fileContents, File file) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(file);
		try {
			copyFileResource(fileContents, outputStream);
		} finally {
			outputStream.close();
		}
	}

	public static ArrayList<Line> getLinesInTextFile(File file) throws IOException {
		return getLinesInTextFile("UTF8", file);
	}

 	public static ArrayList<Line> getLinesInTextFile(String encoding, File file) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		InputStreamReader inputReader = null;
		if(encoding != null) {
			inputReader = new InputStreamReader(inputStream, encoding);
		} else {
			inputReader = new InputStreamReader(inputStream);
		}
		ArrayList<Line> lines = getLinesFromText(file.getName(), inputReader);
		inputReader.close();
		inputStream.close();
		return lines;
	}

	public static ArrayList<Line> getLinesFromText(String fileName, String input) throws IOException {
		StringReader reader = new StringReader(input);
		ArrayList<Line> lines = getLinesFromText(fileName, reader);
		reader.close();
		return lines;
	}

	public static String getFirstLineInText(File file) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		InputStreamReader inputReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputReader);
		String retval = bufferedReader.readLine();
		bufferedReader.close();
		inputReader.close();
		inputStream.close();
		return retval;
	}

	public static ArrayList<Line> getLinesFromText(String fileName, Reader inputReader) throws IOException {
		ArrayList<Line> lines = new ArrayList<Line>();
		BufferedReader reader = new BufferedReader(inputReader);
		String line;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			count++;
			lines.add(new Line(fileName, count, line));
		}
		reader.close();
		return lines;
	}

	public static ArrayList<Line> findLinesInTextFile(String encoding, File file, String regexp) throws IOException {
		ArrayList<Line> lines = new ArrayList<Line>();
		ArrayList<Line> linesInFile = getLinesInTextFile(encoding, file);
		lines.addAll(findLinesInTextFile(regexp, linesInFile));
		return lines;
	}

	public static ArrayList<Line> findLinesInTextFile(String fileName, String fileData, String regexp) throws IOException {
		ArrayList<Line> lines = new ArrayList<Line>();
		List<Line> linesInFile = convertToTextFile(fileName, fileData, false);
		lines.addAll(findLinesInTextFile(regexp, linesInFile));
		return lines;
	}

	private static ArrayList<Line> findLinesInTextFile(String regexp, List<Line> linesInFile) {
		ArrayList<Line> lines = new ArrayList<Line>();
		for(Line line : linesInFile) {
			if(regexp == null || PatternMatchingSupport.valueMatchesRegularExpression(line.getLine(), regexp)) {
				lines.add(line);
			}
		}
		return lines;
	}

	public static ArrayList<Line> findLinesInTextFile(File file, String regexp) throws IOException {
		return findLinesInTextFile(null, file, regexp);
	}

	public static ArrayList<Line> findLinesInFileCollection(FileCollection files, String regexp) throws IOException {
		ArrayList<Line> lines = new ArrayList<Line>();
		for(String fileName : files.getFileNames()) {
			String fileData = files.getFileContentsAsString(fileName);
			lines.addAll(findLinesInTextFile(fileName, fileData, regexp));
		}
		return lines;
	}


	public static void saveSerializable(Serializable serializable, String fileName) throws IOException {

		FileOutputStream fStream = new FileOutputStream(fileName);
		ObjectOutputStream oOutput = new ObjectOutputStream(fStream);

		try {
			oOutput.writeObject(serializable);
			oOutput.flush();
			fStream.flush();
		} finally {
			oOutput.close();
			fStream.close();
		}
	}


	public static Serializable readSerializable(String fileName) throws IOException, ClassNotFoundException {

		FileInputStream fStream = new FileInputStream(fileName);
		ObjectInputStream oInput = new ObjectInputStream(fStream);

		Serializable retval = null;
		try {
			retval = (Serializable)oInput.readObject();
		} finally {
			oInput.close();
			fStream.close();
		}
		return retval;
	}


	/**
	 * Commandline use of FileSupport only supports recursive investigation of directories.
	 * Usage: java FileSupport -<d(elete)|s(how)> [<path>] [<filename>]
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//TODO error message if root dir does not exist

		if (args.length >= 2) {
			if (args[0].startsWith("-d")) {
				if (args.length == 3) {
					deleteContentsInDirectoryTree(args[1], args[2]);
					return;
				}
				if (args.length == 2) {
					emptyDirectory(args[1]);
					return;
				}
			}
			if (args[0].startsWith("-s")) {
				if (args.length == 2) {
					CollectionSupport.print(getFilesAndDirectoriesInDirectoryTree(args[1], null));
					return;
				}
				if (args.length == 3) {
					CollectionSupport.print(getFilesAndDirectoriesInDirectoryTree(args[1], args[2]));
					return;
				}
				if (args.length > 3) {

					if (args.length == 4 && args[2].startsWith("-f")) {
						List files = getFilesInDirectoryTree(args[1]);
						printOccurringString(files, args[3]);
						return;
					}
					if (args.length == 5 && args[3].startsWith("-f")) {
						List files = getFilesInDirectoryTree(args[1], args[2]);
						printOccurringString(files, args[4]);
						return;
					}
				}
			}
		}
		printUsage();
	}

	public static void deleteFilesFromDir(String path, String mask) throws IOException {
		List<File> files = getFilesInDirectoryTree(path, mask);
		for(File file : files) {
			deleteFile(file);
		}
	}

	public static void saveFileCollectionToZip(FileCollection fileCollection, String zipFileName) throws IOException {
		saveFileCollectionToZip(fileCollection, zipFileName, null, null);
	}

	public static void saveFileCollectionToZip(FileCollection fileCollection, String zipFileName, String srcFileNamePart, String targetFileNamePart) throws IOException {
		FileStreamProvider output = new ZipFileStreamProvider(zipFileName);
		for(String fileName : fileCollection.getFileNames()) {
			String targetFileName = fileName;
			if(srcFileNamePart != null) {
				targetFileName = StringSupport.replaceAll(targetFileName, srcFileNamePart, targetFileNamePart);
			}
			byte[] fileContents = fileCollection.getFileContents(fileName);
			OutputStream outputStream = output.createOutputStream(targetFileName);
			copyFileResource(fileContents, outputStream);
			output.closeCurrentStream();
		}
		output.close();
	}

	public static boolean isValidZipFile(String fileName) {
		try {
			ZipFile zipFile = new ZipFile(fileName);
			zipFile.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Try to load zipFile using StandardCharsets.UTF_8 encoding. If that fails, try the Latin-1 encoding.
	 * Sadly Java ZipFile doesn't separate encoding and reading contents, so we have to try UTF_8 and if that fails
	 * we can try Latin-1.
	 * @param file
	 * @return
	 */
	public static ZipFile loadZipFile(File file) throws IOException {
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(file, StandardCharsets.UTF_8);
		} catch (ZipException e) {
			try {
				zipFile = new ZipFile(file, StandardCharsets.ISO_8859_1);
			} catch (IOException ex) {
				throw new IOException("Error while trying to read zipfile (iso-8859-1 encoding): " + file.getName(), ex);
			}
		} catch (IOException otherIoExceptions) {
			throw new IOException("Error while trying to read zipfile (utf-8 encoding): " + file.getName(), otherIoExceptions);
		}
		return zipFile;
	}

	public static ZipFile loadZipFile(String fileName) throws IOException {
		return loadZipFile(new File(fileName));
	}
}
