package com.example.campusassistance.message.entity;

import java.sql.Timestamp;

public class User {

    private String receiveUserId;
    private String sendUserId;
    private Timestamp dateTime;
    private String message;
    private boolean isSend;
    private String bothUserId;

    public String getBothUserId() {
        return bothUserId;
    }

    public void setBothUserId(String bothUserId) {
        this.bothUserId = bothUserId;
    }

    public String getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(String receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }


    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"receiveUserId\":\"")
                .append(receiveUserId).append('\"');
        sb.append(",\"sendUserId\":\"")
                .append(sendUserId).append('\"');
        sb.append(",\"dateTime\":\"")
                .append(dateTime).append('\"');
        sb.append(",\"message\":\"")
                .append(message).append('\"');
        sb.append(",\"isSend\":")
                .append(isSend);
        sb.append(",\"bothUserId\":\"")
                .append(bothUserId).append('\"');
        sb.append('}');
        return sb.toString();
    }

}
