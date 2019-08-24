package org.ijsberg.iglu.util.tool;

import org.ijsberg.iglu.util.DirStructUsingIntegrationTestHelper;
import org.ijsberg.iglu.util.io.FSFileCollection;
import org.ijsberg.iglu.util.io.FileCollectionComparison;
import org.ijsberg.iglu.util.io.FileFilterRuleSet;
import org.ijsberg.iglu.util.io.FileSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SynchronizeDirectoriesTest {

    private static final String RELATIVE_TARGET_DIR_A = "DIR_A";
    private static final String RELATIVE_TARGET_DIR_B = "DIR_B";
    private static final String RESOURCE_DIR = "org/ijsberg/iglu/util/io/directory structure";
    private File tmpDir;

    @Before
    public void setUp() throws IOException {
        tmpDir = DirStructUsingIntegrationTestHelper.populateTmpDirectory(this.getClass(), RESOURCE_DIR, RELATIVE_TARGET_DIR_A);
    }

    @After
    public void tearDown() {
//        DirStructUsingIntegrationTestHelper.deleteTmpDirectory(tmpDir);
    }

/*    
/root/WWW/_d0/t_visie_sml.gif
/root/WWW/_d1/logo.gif
/root/WWW/_d1/notetxt.gif
/root/WWW/_d1/pt_ijsbergarchitecten.gif
/root/WWW/_d1/pt_ijsbergautomatisering.gif
/root/WWW/_d1/pt_ijsbergexpertise.gif
*/    
    @Test
    public void testCopyResources() throws IOException, InterruptedException {
        FSFileCollection fileCollectionA = new FSFileCollection(tmpDir.getPath() + "/" + RELATIVE_TARGET_DIR_A, new FileFilterRuleSet());
        assertEquals(172, fileCollectionA.size());
        fileCollectionA.copyWithDateTo(tmpDir.getPath() + "/" + RELATIVE_TARGET_DIR_B);

        Thread.sleep(10);

        String rootA = tmpDir.getPath() + "/" + RELATIVE_TARGET_DIR_A + "/";
        String rootB = tmpDir.getPath() + "/" + RELATIVE_TARGET_DIR_B + "/";

        FileSupport.deleteFile(rootB + "root/WWW/_d0/t_softwaredevelopment_sml.gif");

        FileSupport.touchFile(rootA + "root/WWW/_d0/t_visie.gif");

        FileSupport.deleteFile(rootB + "root/WWW/_d1/logo.gif");
        FileSupport.touchFile(rootB + "root/WWW/_d1/logo.gif");

        FileSupport.deleteFile(rootB + "root/WWW/_d1/pt_ijsbergarchitecten.gif");

        FileSupport.touchFile(rootB + "root/WWW/_d1/new.png");

        FileSupport.touchFile(rootA + "root/WWW/_d1/pt_ijsbergautomatisering.gif");
        FileSupport.touchFile(rootA + "root/WWW/_d1/pt_ijsbergexpertise.gif");

//        Thread.sleep(500);

        FSFileCollection fileCollectionB = new FSFileCollection(tmpDir.getPath() + "/" + RELATIVE_TARGET_DIR_B, new FileFilterRuleSet());

        FileCollectionComparison collectionComparison = FileCollectionComparison.compare(fileCollectionA, fileCollectionB);

        System.out.println("missing " + collectionComparison.getFilesMissing());
        System.out.println("outdated " + collectionComparison.getFilesOutdated());
        System.out.println("different " + collectionComparison.getFilesWithDifferentSize());

        assertEquals(2, collectionComparison.getFilesMissing().size());
        assertEquals(3, collectionComparison.getFilesOutdated().size());
        assertEquals(1, collectionComparison.getFilesWithDifferentSize().size());

    }

    @Test
    public void testEvaluate() throws IOException {
        SynchronizeDirectories subject = new SynchronizeDirectories("src/test/resources", "src/test/resources");
        subject.evaluate();
    }


}