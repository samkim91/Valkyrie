package com.example.teamproject.CheckDoc;

public class CheckDocRCData {

    String title, content, docInfo, writer, photoURL, time;
    String allData;

    public CheckDocRCData(){
    }

    public CheckDocRCData(String title, String docInfo, String time, String writer, String allData){
        this.title = title;
        this.docInfo = docInfo;
        this.writer = writer;
        this.time = time;
        this.allData = allData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDocInfo() {
        return docInfo;
    }

    public void setDocInfo(String docInfo) {
        this.docInfo = docInfo;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAllData() {
        return allData;
    }

    public void setAllData(String allData) {
        this.allData = allData;
    }
}
