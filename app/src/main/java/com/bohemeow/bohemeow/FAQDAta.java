package com.bohemeow.bohemeow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FAQDAta implements Serializable {
    public String question;
    public String answer;

    public FAQDAta() {

    }

    public FAQDAta(String question, String answer) {
        // set by constructor
        this.question = question;
        this.answer = answer;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("question", question);
        result.put("answer", answer);

        return result;
    }
}
