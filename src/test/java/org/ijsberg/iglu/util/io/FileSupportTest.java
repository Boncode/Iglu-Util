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

package org.ijsberg.iglu.util.io;


import org.ijsberg.iglu.util.tool.SynchronizeDirectories;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;

public class FileSupportTest extends DirStructureDependentTest {


	@Test
	public void testReplaceStringsInFilesInDirectoryTree() throws Exception {

		List<File> files = FileSupport.getFilesInDirectoryTree(dirStructRoot,
				new FileFilterRuleSet().setIncludeFilesWithNameMask("*.html").
						setIncludeFilesContainingText("IJsberg"));

		assertEquals(15, files.size());

		files = FileSupport.getFilesInDirectoryTree(dirStructRoot,
				new FileFilterRuleSet().setIncludeFilesWithNameMask("*.html").
						setIncludeFilesContainingText("Boncode"));

		assertEquals(0, files.size());

		FileSupport.replaceStringsInFilesInDirectoryTree(
				dirStructRoot, new FileFilterRuleSet().setIncludeFilesWithNameMask("*.html"),
				"IJsberg", "Boncode");


		files = FileSupport.getFilesInDirectoryTree(dirStructRoot,
				new FileFilterRuleSet().setIncludeFilesWithNameMask("*.html").
						setIncludeFilesContainingText("IJsberg"));

		assertEquals(0, files.size());

		files = FileSupport.getFilesInDirectoryTree(dirStructRoot,
				new FileFilterRuleSet().setIncludeFilesWithNameMask("*.html").
						setIncludeFilesContainingText("Boncode"));

		assertEquals(15, files.size());
	}

	@Test
	public void testGetFilesInDirectoryTree() {

		File file = new File(dirStructRoot);
		
		assertTrue(file.exists());
		List<File> foundFiles = FileSupport.getFilesInDirectoryTree(dirStructRoot);
		
		assertEquals(171, foundFiles.size());

		String testDirPath = dirStructRoot + '/';

		foundFiles = FileSupport.getFilesInDirectoryTree(dirStructRoot);
		assertEquals(171, foundFiles.size());

		for(File foundFile : foundFiles) {
			System.out.println(foundFile.getName() + " : " + SynchronizeDirectories.convertToReadableByteSize(foundFile.length()));
		}
	}

	@Test
	public void testGetFilesInDirectoryTreeWithMask() {

		File file = new File(dirStructRoot);
		

		assertTrue(file.exists());

        List<File> foundFiles = FileSupport.getFilesInDirectoryTree(file, "*/_d0/*");
        assertEquals(27, foundFiles.size());

		foundFiles = FileSupport.getFilesInDirectoryTree(dirStructRoot, "*/_d0/*");
		assertEquals(27, foundFiles.size());

		String testDirPath = dirStructRoot + '/';

		foundFiles = FileSupport.getFilesInDirectoryTree(testDirPath, "*.LOG");
		assertEquals(19, foundFiles.size());
//        System.out.println(foundFiles);

        foundFiles = FileSupport.getFilesInDirectoryTree(testDirPath, "*/WWW/*.LOG");
        assertEquals(19, foundFiles.size());

//		foundFiles = FileSupport.getFilesInDirectoryTree(testDirPath, "WWW/*.LOG");
//		assertEquals(19, foundFiles.size());
//        System.out.println(foundFiles);


    }


	@Test
	public void testGetFilesInDirectoryTreeWithRuleSet() {

		File file = new File(dirStructRoot);
		assertTrue(file.exists());
		
		FileFilterRuleSet ruleSet = new FileFilterRuleSet().setIncludeFilesWithNameMask("*.LOG");
		
		List<File> foundFiles = FileSupport.getFilesInDirectoryTree(dirStructRoot, ruleSet);
		assertEquals(19, foundFiles.size());
		
		ruleSet.setIncludeFilesWithNameMask("*/_d0/*");
		foundFiles = FileSupport.getFilesInDirectoryTree(dirStructRoot, ruleSet);
		assertEquals(27, foundFiles.size());
		
		ruleSet.setExcludeFilesWithNameMask("*.LOG");
		foundFiles = FileSupport.getFilesInDirectoryTree(dirStructRoot, ruleSet);		
		assertEquals(26, foundFiles.size());
		
		
		ruleSet.setExcludeFilesWithNameMask("*.LOG|*.css");
		foundFiles = FileSupport.getFilesInDirectoryTree(dirStructRoot, ruleSet);		
		assertEquals(25, foundFiles.size());

		ruleSet.setExcludeFilesWithNameMask("*.gif|*.jpg");
		foundFiles = FileSupport.getFilesInDirectoryTree(dirStructRoot, ruleSet);		
		assertEquals(2, foundFiles.size());

		System.out.println(foundFiles);

		ruleSet.setIncludeFilesContainingText("ijsberg");
		foundFiles = FileSupport.getFilesInDirectoryTree(dirStructRoot, ruleSet);		
		assertEquals(1, foundFiles.size());

		ruleSet.setIncludeFilesContainingText("title");
		foundFiles = FileSupport.getFilesInDirectoryTree(dirStructRoot, ruleSet);		
		assertEquals(2, foundFiles.size());

		ruleSet.setExcludeFilesContainingText("ijsberg");
		foundFiles = FileSupport.getFilesInDirectoryTree(dirStructRoot, ruleSet);		
		assertEquals(1, foundFiles.size());
		
		assertEquals("ijsberg.css", foundFiles.get(0).getName());
}

	
	
	@Test
	public void testCreateTmpDir() throws Exception {
		File dir = FileSupport.createTmpDir();
		assertTrue(dir.isDirectory());
		assertTrue(dir.exists());
		assertTrue(dir.delete());
	}


	@Test
	public void getFileNameFromPath() throws Exception {
		assertEquals("TestFile.tst", FileSupport.getFileNameFromPath("/this/is/a/TestFile.tst"));
		assertEquals("TestFile.tst", FileSupport.getFileNameFromPath("/this/is/a//TestFile.tst"));
		assertEquals("TestFile.tst", FileSupport.getFileNameFromPath("TestFile.tst"));
		assertEquals("TestFile.tst", FileSupport.getFileNameFromPath("\\this\\is/a\\\\TestFile.tst"));
		try {
			FileSupport.getFileNameFromPath("/this/is/a/");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {}
	}



	@Test
	public void testGetInputStreamFromClassLoader() throws Exception{
		//NOTE: IDEs will not automatically regard any file as resource
		InputStream input = FileSupport.getInputStreamFromClassLoader("iglu_logo_ice.gif");
		try {
			FileSupport.getInputStreamFromClassLoader("not_existing.file");
			fail("IOException expected");
		} catch (IOException expected) {}

		input = FileSupport.getInputStreamFromClassLoader("test/ijsberg.jpg");

		//a dir can be loaded
		input = FileSupport.getInputStreamFromClassLoader(RELATIVE_DIR_PATH + "/root/WWW");
		//(input.available produces NullPointer on Apple)
		
		input = FileSupport.getInputStreamFromClassLoader(RELATIVE_DIR_PATH + "/root/WWW/route.gif");
		
		byte[] thing = StreamSupport.absorbInputStream(input);

		input.close();
	}

	@Test
	public void testCopyClassLoadableResourceToFileSystem() throws IOException{

		int nrofFilesInTmpDir = tmpDir.listFiles().length;
		FileSupport.copyClassLoadableResourceToFileSystem("iglu_logo_ice.gif", tmpDir.getPath() + "/iglu_logo.gif");
		assertEquals(nrofFilesInTmpDir + 1, tmpDir.listFiles().length);

		assertEquals("iglu_logo.gif", tmpDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		})[0].getName());

		FileSupport.copyClassLoadableResourceToFileSystem("iglu_logo_ice.gif", tmpDir.getPath());
		assertEquals(nrofFilesInTmpDir + 2, tmpDir.listFiles().length);

		assertTrue(new File(tmpDir.getPath() + "/iglu_logo_ice.gif").exists());
	}

	@Test
	public void testGetDirNameFromPath() throws Exception {
		assertEquals("/hop/", FileSupport.getDirNameFromPath("/hop/la"));
		assertEquals("/hop/", FileSupport.getDirNameFromPath("/hop/"));
		assertEquals("/hop/", FileSupport.getDirNameFromPath("/hop/la"));
		assertEquals("/", FileSupport.getDirNameFromPath("/hop"));
		assertEquals("", FileSupport.getDirNameFromPath("hop"));
	}

	@Test
	public void testMergeZipFiles() throws IOException {
		FileCollection sourceFileCollection = null;
		FileCollection targetFileCollection = null;
		try {
			sourceFileCollection = new ZippedFileCollection(new File(tmpDir.getPath() + "/root/source.zip"));
			targetFileCollection = new ZippedFileCollection(new File(tmpDir.getPath() + "/root/target.zip"));
			assertEquals(6, targetFileCollection.size());
		} finally {
			targetFileCollection.close();
		}
		try {
			FileSupport.mergeInZipFile(tmpDir.getPath() + "/root/target.zip", sourceFileCollection);
			targetFileCollection = new ZippedFileCollection(new File(tmpDir.getPath() + "/root/target.zip"));
			assertEquals(11, targetFileCollection.size());
		} finally {
			sourceFileCollection.close();
			targetFileCollection.close();
		}
	}


	@Test
	public void testGetResourceFolderFilesRecursive() throws IOException, URISyntaxException {
		List<String> fileNames = FileSupport.getResourceFolderFilesRecursive(this.getClass(), "");
		//This changes every time test resources are added or removed
		assertEquals(215, fileNames.size());
		//System.out.println(fileNames);
	}

}
