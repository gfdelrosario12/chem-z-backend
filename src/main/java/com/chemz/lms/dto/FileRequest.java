package com.chemz.lms.dto;

public class FileRequest {
    private String fileName;
    private String contentType; // optional but recommended (pdf, docx, png...)

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
