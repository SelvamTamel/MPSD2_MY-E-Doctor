package com.example.myedoctor;

public class Class_Health_Edu {

    public String title, content, logoKey;

    public Class_Health_Edu (){

    }

    public Class_Health_Edu (String title, String content, String logoKey){
        this.title = title;
        this.content = content;
        this.logoKey = logoKey;
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

    public String getLogoKey() {
        return logoKey;
    }

    public void setLogoKey(String logoKey) {
        this.logoKey = logoKey;
    }
}