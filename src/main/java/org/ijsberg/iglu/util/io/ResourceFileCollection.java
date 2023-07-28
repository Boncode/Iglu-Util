package org.ijsberg.iglu.util.io;

import org.ijsberg.iglu.util.ResourceException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourceFileCollection implements FileCollection {

    private Class clasz;
    protected Directory rootDir = new Directory("ROOT");
    protected String baseDir;

    protected Set<String> filePathAndNames = new HashSet<>();
    protected FileFilterRuleSet includedFilesRuleSet;

    public ResourceFileCollection(Class clasz, String baseDir, FileFilterRuleSet fileFilterRuleSet) {
        this.clasz = clasz;
        this.baseDir = FileSupport.convertToUnixStylePath(baseDir);
        this.includedFilesRuleSet = fileFilterRuleSet;
        refreshFiles();
    }

    @Override
    public List<String> getFileNames() {
        return new ArrayList<>(filePathAndNames);
    }

    @Override
    public byte[] getFileContents(String fileName) throws IOException {
        return FileSupport.getBinaryFromClassLoader(fileName);
    }

    @Override
    public String getFileContentsAsString(String fileName) throws IOException {
        return new String(getFileContents(fileName));
    }

    @Override
    public boolean containsFile(String fileName) {
        return filePathAndNames.contains(FileSupport.convertToUnixStylePath(fileName));
    }

    @Override
    public Directory getRootDirectory() {
        return rootDir;
    }

    public void refreshFiles() {

        filePathAndNames.clear();
        rootDir = new Directory("ROOT");

        List<String> fileNames;
        try {
            fileNames = FileSupport.getResourceFolderFilesRecursive(clasz, baseDir, includedFilesRuleSet);
        } catch (IOException | URISyntaxException e) {
            throw new ResourceException("unable to get resources for path " + baseDir);
        }
        for (String fileName : fileNames) {
            String relativePathAndName = FSFileCollection.getRelativePathAndName(baseDir, fileName);
            filePathAndNames.add(relativePathAndName);
            rootDir.addFile(relativePathAndName);
        }
    }

    @Override
    public String getDescription() {
        return "directory: '" + baseDir + "'";
    }

    @Override
    public int size() {
        return filePathAndNames.size();
    }

    @Override
    public void close() throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return baseDir;
    }

    public void copyTo(String newBaseDir) throws IOException {
        for(String fileName : getFileNames()) {
            FileSupport.copyClassLoadableResourceToFileSystem(baseDir + "/" + fileName, newBaseDir + "/" + fileName);
        }
    }
}
