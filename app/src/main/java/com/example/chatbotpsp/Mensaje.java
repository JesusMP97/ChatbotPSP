package com.example.chatbotpsp;

import java.util.HashMap;
import java.util.Map;

public class Mensaje {
    public String time, sentenceEs, sentenceEn;
    public boolean user;

    public Mensaje(String time, String sentenceEs, String sentenceEn, boolean user) {
        this.time = time;
        this.sentenceEs = sentenceEs;
        this.sentenceEn = sentenceEn;
        this.user = user;
    }

    public void setSentenceEs(String sentenceEs) {
        this.sentenceEs = sentenceEs;
    }

    public void setSentenceEn(String sentenceEn) {
        this.sentenceEn = sentenceEn;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("sentenceEn", sentenceEn);
        result.put("sentenceEs", sentenceEs);
        result.put("time", time);
        result.put("user", user);
        return result;
    }

}
