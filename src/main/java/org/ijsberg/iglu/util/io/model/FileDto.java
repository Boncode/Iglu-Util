package org.ijsberg.iglu.util.io.model;

public class FileDto {

    private String fileName;
    private String dirName;

    public FileDto(String fileName, String dirName) {
        this.fileName = fileName;
        this.dirName = dirName;
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
}
