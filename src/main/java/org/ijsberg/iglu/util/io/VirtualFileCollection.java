package org.ijsberg.iglu.util.io;

import org.ijsberg.iglu.util.ResourceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VirtualFileCollection implements FileCollection {

    private Map<String, byte[]> fileContentsByName = new TreeMap<>();
    private Directory rootDir = new Directory("ROOT");
    private String name;

    public VirtualFileCollection(String name) {
        this.name = name;
    }

    @Override
    public List<String> getFileNames() {
        return new ArrayList<>(fileContentsByName.keySet());
    }

    @Override
    public byte[] getFileContents(String fileName) throws IOException {
        String convertedFileName = FileSupport.convertToUnixStylePath(fileName);
        if(!fileContentsByName.containsKey(convertedFileName)) {
            throw new IOException("File not found: " + fileName);
        }
        return fileContentsByName.get(convertedFileName);
    }

    @Override
    public String getFileContentsAsString(String fileName) throws IOException {
        return new String(getFileContents(fileName));
    }

    @Override
    public boolean containsFile(String fileName) {
        return fileContentsByName.containsKey(FileSupport.convertToUnixStylePath(fileName));
    }

    @Override
    public Directory getRootDirectory() {
        return rootDir;
    }

    @Override
    public String getDescription() {
        return "file collection containing " + fileContentsByName.size() + " entries";
    }

    @Override
    public int size() {
        return fileContentsByName.size();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public String getName() {
        return name;
    }

    public void addFile(String fileName, byte[] fileContents) {
        String convertedFileName = FileSupport.convertToUnixStylePath(fileName);
        fileContentsByName.put(convertedFileName, fileContents);
        rootDir.addFile(convertedFileName);
    }

    public static VirtualFileCollection create(FileCollection fileCollection) {
        VirtualFileCollection virtualFileCollection = new VirtualFileCollection(fileCollection.getName());
        for(String fileName : fileCollection.getFileNames()) {
            try {
                virtualFileCollection.addFile(fileName, fileCollection.getFileContents(fileName));
            } catch (IOException e) {
                throw new ResourceException("Could not add file " + fileName, e);
            }
        }
        return virtualFileCollection;
    }
}
