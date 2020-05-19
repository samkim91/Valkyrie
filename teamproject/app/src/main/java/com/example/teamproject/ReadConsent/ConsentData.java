package com.example.teamproject.ReadConsent;

public class ConsentData {
    String docName;
    String patName;
    String time;
    String content;
    String surgeryName;
    String papaername, paperver;

    public String getPapaername() {
        return papaername;
    }

    public void setPapaername(String papaername) {
        this.papaername = papaername;
    }

    public String getPaperver() {
        return paperver;
    }

    public void setPaperver(String paperver) {
        this.paperver = paperver;
    }

    public String getSurgeryName() {
        return surgeryName;
    }

    public void setSurgeryName(String surgeryName) {
        this.surgeryName = surgeryName;
    }


    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getPatName() {
        return patName;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
