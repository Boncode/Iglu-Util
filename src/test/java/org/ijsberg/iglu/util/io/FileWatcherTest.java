package org.ijsberg.iglu.util.io;

import org.ijsberg.iglu.util.DirStructUsingIntegrationTestHelper;
import org.ijsberg.iglu.util.collection.ArraySupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by J Meetsma on 18-11-2016.
 */
public class FileWatcherTest {

    public static final String RELATIVE_DIR = "org/ijsberg/iglu/util/io/directory structure";
    private File tmpDir;

    @Before
    public void setUp() throws Exception {
        tmpDir = DirStructUsingIntegrationTestHelper.populateTmpDirectory(RELATIVE_DIR);
    }

    @After
    public void tearDown() {
        FileSupport.deleteFile(tmpDir);
    }

    private static class WatcherHandlerImpl implements WatcherHandler {

        private int nrFilesCreated = 0;
        private int nrFilesModified = 0;
        private int nrFilesDeleted = 0;
        private int nrDirectoriesCreated = 0;
        private int nrDirectoriesDeleted = 0;

        @Override
        public void onFileModified(File file) {
            System.out.println(new Date() + " modified file: " + file.getPath());
            nrFilesModified++;
        }

        @Override
        public void onDirectoryCreated(File file) {
            System.out.println(new Date() + " created: " + file.getPath());
            nrDirectoriesCreated++;
        }

        @Override
        public void onFileCreated(File file) {
            System.out.println(new Date() + " created: " + file.getPath());
            nrFilesCreated++;
        }

        @Override
        public void onDirectoryDeleted(File file) {
            System.out.println(new Date() + " deleted dir: " + file.getPath());
            nrDirectoriesDeleted++;
        }

        @Override
        public void onFileDeleted(File file) {
            System.out.println(new Date() + " deleted file: " + file.getPath());
            nrFilesDeleted++;
        }
    }

    @Test
    public void tesFileEvents() throws IOException, InterruptedException {
        System.out.println(ArraySupport.format(tmpDir.list(), ","));
        FileWatcher fileWatcher = new FileWatcher(20, tmpDir.listFiles()[0].listFiles());
        WatcherHandlerImpl watcherHandler = new WatcherHandlerImpl();
        fileWatcher.startWatcher(watcherHandler);

        File file = FileSupport.createFile(tmpDir.getPath() + "/root/WWW/test_create.txt");
        System.out.println(file.getPath());

        FileSupport.copyFile(tmpDir.getPath() + "/root/WWW/_d0/darwinsevolution.gif", tmpDir.getPath() + "/root/WWW/darwinsevolution.gif", true);

        Thread.sleep(100);

        FileSupport.copyFile(tmpDir.getPath() + "/root/WWW/_d0/darwinsevolution.gif", tmpDir.getPath() + "/root/WWW/test_create.txt", true);

        Thread.sleep(100);

        FileSupport.deleteFile(file);

        Thread.sleep(500);

        fileWatcher.stopWatcher();

        assertEquals(2, watcherHandler.nrFilesCreated);
        assertEquals(2, watcherHandler.nrFilesModified);
        assertEquals(1, watcherHandler.nrFilesDeleted);
        assertEquals(0, watcherHandler.nrDirectoriesCreated);
        assertEquals(0, watcherHandler.nrDirectoriesDeleted);
    }

    @Test
    public void tesDirectoryEvents() throws IOException, InterruptedException {
        System.out.println(ArraySupport.format(tmpDir.list(), ","));
        FileWatcher fileWatcher = new FileWatcher(20, tmpDir.listFiles()[0].listFiles());
        WatcherHandlerImpl watcherHandler = new WatcherHandlerImpl();
        fileWatcher.startWatcher(watcherHandler);

        File file = FileSupport.createDirectory(tmpDir.getPath() + "/root/WWW/bogus");
        assertTrue(file.isDirectory());
        System.out.println(file.getPath());

        Thread.sleep(100);

        assertEquals(1, watcherHandler.nrDirectoriesCreated);
        assertEquals(0, watcherHandler.nrDirectoriesDeleted);

        FileSupport.deleteFile(new File(tmpDir.getPath() + "/root/WWW/bogus"));
        FileSupport.deleteFile(new File(tmpDir.getPath() + "/root/WWW/_d0"));

        Thread.sleep(500);

        fileWatcher.stopWatcher();

        assertEquals(1, watcherHandler.nrDirectoriesCreated);
        assertEquals(2, watcherHandler.nrDirectoriesDeleted);
        assertEquals(0, watcherHandler.nrFilesDeleted);
        assertEquals(0, watcherHandler.nrFilesCreated);
        assertEquals(0, watcherHandler.nrFilesModified);
    }


}