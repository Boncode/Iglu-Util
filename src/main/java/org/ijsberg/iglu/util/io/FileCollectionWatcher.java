package org.ijsberg.iglu.util.io;

/**
 * Created by J Meetsma on 26-11-2016.
 */
public interface FileCollectionWatcher {
    void onFileTouched(String relativePathAndName);

    void onFileCollectionRefreshed();
}
