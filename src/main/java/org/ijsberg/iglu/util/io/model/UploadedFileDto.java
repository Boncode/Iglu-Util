package org.ijsberg.iglu.util.io.model;

public class UploadedFileDto extends FileDto {

    private final String uploadedBy;
    private final Long uploadedTimestamp;
    private final String comment;

    public UploadedFileDto(String fileName, String dirName, String uploadedBy, Long uploadTimestamp, String comment) {
        super(fileName, dirName);
        this.uploadedBy = uploadedBy;
        this.uploadedTimestamp = uploadTimestamp;
        this.comment = comment;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public Long getUploadedTimestamp() {
        return uploadedTimestamp;
    }

    public String getComment() {
        return comment;
    }
}
