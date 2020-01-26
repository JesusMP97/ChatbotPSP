package com.example.chatbotpsp;

import java.util.HashMap;
import java.util.Map;

public class MessageItem {

    private String sentenceEn, sentenceEs, talker, time;

    public MessageItem() {
    }

    public MessageItem(String sentenceEn, String sentenceEs, String talker, String time) {
        this.sentenceEn = sentenceEn;
        this.sentenceEs = sentenceEs;
        this.talker = talker;
        this.time = time;
    }

    public String getSentenceEn() {
        return sentenceEn;
    }

    public void setSentenceEn(String sentenceEn) {
        this.sentenceEn = sentenceEn;
    }

    public String getSentenceEs() {
        return sentenceEs;
    }

    public void setSentenceEs(String sentenceEs) {
        this.sentenceEs = sentenceEs;
    }

    public String getTalker() {
        return talker;
    }

    public void setTalker(String talker) {
        this.talker = talker;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ChatSentence{" +
                "sentenceEn='" + sentenceEn + '\'' +
                ", sentenceEs='" + sentenceEs + '\'' +
                ", talker='" + talker + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("sentenceEn", sentenceEn);
        result.put("sentenceEs", sentenceEs);
        result.put("talker", talker);
        result.put("time", time);
        return result;
    }
}
