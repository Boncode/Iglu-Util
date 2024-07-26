package org.ijsberg.iglu.util.io.model;

public class UploadedFileCommentDto {

    private String fileName;
    private String comment;

    public String getFileName() {
        return fileName;
    }

    public String getComment() {
        return comment;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
