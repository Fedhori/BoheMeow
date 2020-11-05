package com.bohemeow.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class bugReportData {
    public String bugReport;
    public String username;

    public bugReportData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public bugReportData(String bugReport, String username) {
        this.bugReport = bugReport;
        this.username = username;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("bugReport", bugReport);
        result.put("username", username);
        return result;
    }
}
