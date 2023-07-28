package org.ijsberg.iglu.util.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualFileCollection implements FileCollection {

    private Map<String, byte[]> fileContentsByName = new HashMap<>();
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
        return fileContentsByName.get(fileName);
    }

    @Override
    public String getFileContentsAsString(String fileName) throws IOException {
        return new String(getFileContents(fileName));
    }

    @Override
    public boolean containsFile(String fileName) {
        return fileContentsByName.containsKey(fileName);
    }

    @Override
    public Directory getRootDirectory() {
        return null;
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
        return null;
    }

    public void addFile(String name, byte[] fileContents) {
        fileContentsByName.put(name, fileContents);
    }

}
