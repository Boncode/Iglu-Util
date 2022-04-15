package org.ijsberg.iglu.util;

/**
 * Created by J Meetsma on 23-11-2016.
 */

import org.ijsberg.iglu.util.io.FileFilterRuleSet;
import org.ijsberg.iglu.util.io.FileSupport;
import org.ijsberg.iglu.util.io.ResourceFileCollection;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Copies a directory structure from test resources to temporary directory tmpDir.
 * Subclasses may use the directory structure to perform all kinds of tests.
 *
 */
public abstract class DirStructUsingIntegrationTestHelper {

    protected static File tmpDir;
    protected static String dirStructRoot;

    public static File populateTmpDirectoryUsingFilesTxt(String relativeDir) throws IOException {

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


    public static File populateTmpDirectory(Class clasz, String resourceDir, String relativeTargetDir) throws IOException {

        ResourceFileCollection rfc = new ResourceFileCollection(clasz, resourceDir, new FileFilterRuleSet().setIncludeFilesWithNameMask("*"));
        tmpDir = FileSupport.createTmpDir(clasz.getSimpleName());
        dirStructRoot = tmpDir.getPath() + '/' + relativeTargetDir + "/";
        rfc.copyTo(dirStructRoot);
        return tmpDir;
    }

    public static void deleteTmpDirectory(File tmpDir) {
        try {
            FileSupport.deleteFile(tmpDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getFile(String name) {
        return new File(dirStructRoot + '/' + name);
    }




}
