package org.ijsberg.iglu.util.io.model;

public class FileDto {

    private String fileName;
    private String dirName;
    private String lastModified;

    public FileDto(String fileName, String dirName, String lastModifiedString) {
        this.fileName = fileName;
        this.dirName = dirName;
        this.lastModified = lastModifiedString;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
