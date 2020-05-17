package com.test.concurrent.high.score.backend.model;

import com.test.concurrent.high.score.backend.unit.Session;

public interface UserSessionHandler {
    /**
     * Generates a unique session id for user that is valid for 10 minutes.
     * @param userId
     * @return session generated
     */
    Session generateUserSession(int userId);

    /**
     * Validates the session
     * @param sessionId
     * @return true, if session is valid and active. else false.
     */
    boolean validateSession(String sessionId);

    /**
     * Gets the userId for a session.
     * @param sessionId
     * @return userId if the session exists else -1.
     */
    int getUserIdFromSessionId(String sessionId);
}
