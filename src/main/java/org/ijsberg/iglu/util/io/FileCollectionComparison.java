package org.ijsberg.iglu.util.io;

import org.ijsberg.iglu.util.tool.SynchronizeDirectories;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileCollectionComparison {

    private Map<String, File> filesMissing;
    private Map<String, File> filesOutdated;
    private Map<String, File> filesWithDifferentSize;

    public FileCollectionComparison(Map<String, File> filesMissing, Map<String, File> filesOutdated, Map<String, File> filesWithDifferentSize) {
        this.filesMissing = filesMissing;
        this.filesOutdated = filesOutdated;
        this.filesWithDifferentSize = filesWithDifferentSize;
    }

    public static FileCollectionComparison compare(FSFileCollection fileCollectionA, FSFileCollection fileCollectionB) throws IOException {
        Map<String, File> filesMissing = new LinkedHashMap<>();
        Map<String, File> filesOutdated = new LinkedHashMap<>();
        Map<String, File> filesWithDifferentSize = new LinkedHashMap<>();

        for(String fileName : fileCollectionA.getFileNames()) {
            File fileA = fileCollectionA.getActualFileByName(fileName);
            File fileB = fileCollectionB.getActualFileByName(fileName);
            if(fileB == null) {
                filesMissing.put(fileName, fileA);
            } else {
                if(fileA.lastModified() > fileB.lastModified()) {
                    filesOutdated.put(fileName, fileA);
                }
                if(fileA.length() != fileB.length()) {
                    filesWithDifferentSize.put(fileName, fileA);
                }
            }
        }
        return new FileCollectionComparison(filesMissing, filesOutdated, filesWithDifferentSize);
    }

    public Map<String, File> getFilesMissing() {
        return filesMissing;
    }

    public Map<String, File> getFilesOutdated() {
        return filesOutdated;
    }

    public Map<String, File> getFilesWithDifferentSize() {
        return filesWithDifferentSize;
    }
}
