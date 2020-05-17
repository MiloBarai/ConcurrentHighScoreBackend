package com.test.concurrent.high.score.backend.unit;

import java.util.Date;

/**
 * Data class for session information
 */
public class Session {
    int userId;
    String sessionId;
    Date expirationDate;

    public Session(int userId, String sessionId, Date expirationDate){
        this.userId = userId;
        this.sessionId = sessionId;
        this.expirationDate = expirationDate;
    }

    public int getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
}
