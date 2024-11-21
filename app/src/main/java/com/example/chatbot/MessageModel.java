package com.example.chatbot;

public class MessageModel {
    static String SENT_BY_ME = "me";
    static String SENT_BY_BOT = "bot";

    String message;
    String sent_by;
    public MessageModel(String message,String sent_by){
        this.message=message;
        this.sent_by= sent_by;
    }
    public void setMessage(String message){
        this.message=message;
    }
    public String getMessage(){
        return message;
    }
    public void setSentBy(String sent_by){
        this.sent_by=sent_by;
    }
    public String getSentBy(){
        return sent_by;
    }
}
