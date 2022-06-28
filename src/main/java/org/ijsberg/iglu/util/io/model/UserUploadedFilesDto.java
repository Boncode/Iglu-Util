package org.ijsberg.iglu.util.io.model;

import java.util.Map;

public class UserUploadedFilesDto {

    private Map<String, FileCollectionDto> userFileCollectionDtoMap;

    public UserUploadedFilesDto(Map<String, FileCollectionDto> userFileCollectionDtoMap) {
        this.userFileCollectionDtoMap = userFileCollectionDtoMap;
    }

    public void setUserFileCollectionDtoMap(Map<String, FileCollectionDto> userFileCollectionDtoMap) {
        this.userFileCollectionDtoMap = userFileCollectionDtoMap;
    }

    public Map<String, FileCollectionDto> getUserFileCollectionDtoMap() {
        return userFileCollectionDtoMap;
    }

}
