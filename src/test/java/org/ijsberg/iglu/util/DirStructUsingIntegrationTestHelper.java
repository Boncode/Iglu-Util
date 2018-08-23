package org.ijsberg.iglu.util;

/**
 * Created by J Meetsma on 23-11-2016.
 */

import org.ijsberg.iglu.util.io.FileSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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

        List<String> lines = FileSupport.loadTextFromClassLoader(relativeDir + "/files.txt");
        for(String line : lines) {
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
