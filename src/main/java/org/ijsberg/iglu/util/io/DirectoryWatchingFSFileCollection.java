package org.ijsberg.iglu.util.io;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by J Meetsma on 23-11-2016.
 */
public class DirectoryWatchingFSFileCollection extends FSFileCollection implements WatcherHandler {

    private List<File> directories;

    private FileWatcher fileWatcher;

    private FileCollectionWatcher delegatedWatcherHandler;

    public DirectoryWatchingFSFileCollection(String baseDir, FileFilterRuleSet fileFilterRuleSet) {
        super(baseDir, fileFilterRuleSet);
    }

    public DirectoryWatchingFSFileCollection(String baseDir, FileFilterRuleSet fileFilterRuleSet, FileCollectionWatcher delegatedWatcherHandler) {
        super(baseDir, fileFilterRuleSet);
        this.delegatedWatcherHandler = delegatedWatcherHandler;
    }

    public void refreshFiles() {
        super.refreshFiles();
        directories = FileSupport.getDirectoriesInDirectoryTree(baseDir);

        if(fileWatcher == null) {
            fileWatcher = new FileWatcher(500, directories.toArray(new File[]{}));
        } else {
            fileWatcher.stopWatcher();
        }
        fileWatcher.startWatcher(this);
    }

    private int nrFilesCreated = 0;
    private int nrFilesModified = 0;
    private int nrFilesDeleted = 0;
    private int nrDirectoriesCreated = 0;
    private int nrDirectoriesDeleted = 0;

    private boolean compliesToFilter(File file) {
        return includedFilesRuleSet.fileMatchesRules(file) && containsFile(getRelativePathAndName(file));
    }


    @Override
    public void onFileCreated(File file) {
        System.out.println(compliesToFilter(file) + " " + new Date() + " created: " + file.getPath());

        if(includedFilesRuleSet.fileMatchesRules(file)) {
            String relativePathAndName = getRelativePathAndName(file);
            filesByRelativePathAndName.put(relativePathAndName, file);
        }
        nrFilesCreated++;
    }

    @Override
    public void onFileDeleted(File file) {
        System.out.println(compliesToFilter(file) + " " + new Date() + " deleted file: " + file.getPath());
        if(includedFilesRuleSet.fileMatchesRules(file)) {
            String relativePathAndName = getRelativePathAndName(file);
            filesByRelativePathAndName.remove(relativePathAndName);
        }
        nrFilesDeleted++;
    }

    @Override
    public void onFileModified(File file) {
        //re-examine
        System.out.println(compliesToFilter(file) + " " + new Date() + " modified file: " + file.getPath());

        if(includedFilesRuleSet.fileMatchesRules(file)) {
            String relativePathAndName = getRelativePathAndName(file);
            if(delegatedWatcherHandler != null) {
                delegatedWatcherHandler.onFileTouched(relativePathAndName);
            }
        }
        nrFilesModified++;
    }

    @Override
    public void onDirectoryCreated(File file) {
        //if directory contains files, everything should be refreshed, analysis from start
        System.out.println(compliesToFilter(file) + " " + new Date() + " created dir: " + file.getPath());
        nrDirectoriesCreated++;
    }

    @Override
    public void onDirectoryDeleted(File file) {
        System.out.println(compliesToFilter(file) + " " + new Date() + " deleted dir: " + file.getPath());
        nrDirectoriesDeleted++;
    }


    public int getNrFilesCreated() {
        return nrFilesCreated;
    }

    public int getNrFilesModified() {
        return nrFilesModified;
    }

    public int getNrFilesDeleted() {
        return nrFilesDeleted;
    }

    public int getNrDirectoriesCreated() {
        return nrDirectoriesCreated;
    }

    public int getNrDirectoriesDeleted() {
        return nrDirectoriesDeleted;
    }

    public void stopWatching() {
        if(fileWatcher != null) {
            fileWatcher.stopWatcher();
        }
    }
}
