package org.ijsberg.iglu.util.io;

import java.io.File;

/**
 * Created by J Meetsma on 18-11-2016.
 */
public interface WatcherHandler {

    void onFileModified(File file);

    void onDirectoryCreated(File file);

    void onFileCreated(File file);

    void onDirectoryDeleted(File file);

    void onFileDeleted(File file);
}
