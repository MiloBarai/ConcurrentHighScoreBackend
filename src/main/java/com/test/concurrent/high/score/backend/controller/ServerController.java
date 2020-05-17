package com.test.concurrent.high.score.backend.controller;

public interface ServerController {
    /**
     * Generates a session key for the userId that by default is available for 10 minutes
     * @param userId , userId to generate session id for
     * @return session key in string format for input user
     */
    String login(int userId);

    /**
     * Validates input session key
     * @param sessionId , session key to validate
     * @return true if session key still valid, false otherwise
     */
    boolean validateSession(String sessionId);

    /**
     * Sets the users score into the high score list if eligible
     * @param sessionId , user session key
     * @param level , level to set score for
     * @param score , score to set
     * @return ture if successful, false if unsuccessful/invalid sessionId
     */
    boolean setUserScore(String sessionId, int level, int score);

    /**
     * get a string representation of the high score list of input level
     * in the format: "userId1=Score1,userId2=score2,userId3=score3"
     * in descending scoring order.
     *
     * @param level , level to get high score for
     * @return string representation of the high scores
     */
    String getLevelHighScoreList(int level);
}
