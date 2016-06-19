package com.simoncherry.averagefaceclient2.bean;

/**
 * Created by Simon on 2016/6/19.
 */
public class DirectoryBean {
    int imgID;
    String fileName;
    Long fileDate;
    int fileCount;

    public void setImgID(int imgID){
        this.imgID = imgID;
    }

    public int getImgID(){
        return this.imgID;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public String getFileName(){
        return this.fileName;
    }

    public void setFileDate(Long fileDate){
        this.fileDate = fileDate;
    }

    public Long getFileDate(){
        return this.fileDate;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public int getFileCount() {
        return fileCount;
    }
}
