package org.ijsberg.iglu.util.io.model;

import java.util.List;

public class FileCollectionDto {

    private List<FileDto> fileDtos;

    public FileCollectionDto(List<FileDto> fileDtos) {
        this.fileDtos = fileDtos;
    }

    public List<FileDto> getFileDtos() {
        return fileDtos;
    }

    public void setFileDtos(List<FileDto> fileDtos) {
        this.fileDtos = fileDtos;
    }

}
