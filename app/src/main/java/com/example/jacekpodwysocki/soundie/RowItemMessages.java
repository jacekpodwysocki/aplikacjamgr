package com.example.jacekpodwysocki.soundie;


/**
 * Created by jacekpodwysocki on 11/11/2016.
 */

public class RowItemMessages {
    private String messageAuthor;
    private String messageContent;


    public RowItemMessages(String messageAuthor, String messageContent){
        this.messageAuthor = messageAuthor;
        this.messageContent = messageContent;

    }

    public String getMessageAuthor(){
        return messageAuthor;
    }
    public String getMessageContent(){
        return messageContent;
    }

    public void setMessageAuthor(String songTitle){
        this.messageAuthor = messageAuthor;
    }
    public void setMessageContent(String songArtist){
        this.messageContent = messageContent;
    }

}
