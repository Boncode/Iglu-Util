package org.ijsberg.iglu.util;

/**
 * Created by J Meetsma on 23-11-2016.
 */

import org.ijsberg.iglu.util.io.FileSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;

/**
 * Copies a directory structure from test resources to temporary directory tmpDir.
 * Subclasses may use the directory structure to perform all kinds of tests.
 *
 */
public abstract class DirStructUsingIntegrationTestHelper {

    protected static File tmpDir;
    protected static String dirStructRoot;

    public static File populateTmpDirectory(String relativeDir) throws Exception {

        tmpDir = FileSupport.createTmpDir("UtilIntegrationTest");
        dirStructRoot = tmpDir.getPath() + '/';

        byte[] bytes = FileSupport.getBinaryFromClassLoader(relativeDir + "/files.txt");
        String fileList = new String(bytes);
        StringReader reader = new StringReader(fileList);
        BufferedReader bf = new BufferedReader(reader);
        String line;
        while((line = bf.readLine()) != null) {
            if(!line.isEmpty()) {
                FileSupport.copyClassLoadableResourceToFileSystem(relativeDir + "/" + line, dirStructRoot + "/" + line);
            }
        }
        return tmpDir;
    }

    public static void deleteTmpDirectory(File tmpDir) throws Exception {
        FileSupport.deleteFile(tmpDir);
    }

    public static File getFile(String name) {
        return new File(dirStructRoot + '/' + name);
    }




}
