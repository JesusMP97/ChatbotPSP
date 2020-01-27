package com.example.chatbotpsp;

import java.util.HashMap;
import java.util.Map;

public class Mensaje {
    public String time, sentenceEs, sentenceEn, talker;

    public Mensaje(){

    }

    public Mensaje(String time, String sentenceEs, String sentenceEn, String talker) {
        this.time = time;
        this.sentenceEs = sentenceEs;
        this.sentenceEn = sentenceEn;
        this.talker = talker;
    }

    public Mensaje(String time, String talker) {
        this.time = time;
        this.sentenceEs = "";
        this.sentenceEn = "";
        this.talker = talker;
    }

    public void setSentenceEs(String sentenceEs) {
        this.sentenceEs = sentenceEs;
    }

    public void setSentenceEn(String sentenceEn) {
        this.sentenceEn = sentenceEn;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTalker(String talker) {
        this.talker = talker;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("sentenceEn", sentenceEn);
        result.put("sentenceEs", sentenceEs);
        result.put("time", time);
        result.put("talker", talker);
        return result;
    }

}
