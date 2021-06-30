package com.migi.toeic.dto;

public class FileDTO {

    private String fileName;
    private String fileType;
    private long size;
    private String pathLocation;

    public FileDTO(String fileName, String fileType, long size , String pathLocation) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
        this.pathLocation = pathLocation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPathLocation() {
        return pathLocation;
    }

    public void setPathLocation(String pathLocation) {
        this.pathLocation = pathLocation;
    }
}
