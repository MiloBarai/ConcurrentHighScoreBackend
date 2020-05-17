package com.test.concurrent.high.score.backend.controller;

import com.test.concurrent.high.score.backend.unit.Session;
import com.test.concurrent.high.score.backend.model.ScoreHandler;
import com.test.concurrent.high.score.backend.model.UserSessionHandler;

/**
 * ServerControllerImpl,
 * Responsible for handling order or execution for tasks.
 */
public class ServerControllerImpl implements ServerController{

    UserSessionHandler uSessionHandler;
    ScoreHandler scoreHandler;

    public ServerControllerImpl(UserSessionHandler uSession, ScoreHandler scoreHandler){
        this.uSessionHandler = uSession;
        this.scoreHandler = scoreHandler;
    }

    @Override
    public String login(int userId) {
        Session userSession = uSessionHandler.generateUserSession(userId);
        System.out.println("Generated Session: "+userSession.getSessionId() + ", for user: "+userSession.getUserId());
        return userSession.getSessionId();
    }

    @Override
    public boolean validateSession(String sessionId) {
        if(sessionId == null)
            return false;

        return uSessionHandler.validateSession(sessionId);
    }

    @Override
    public boolean setUserScore(String sessionId, int level, int score) {
        int userId = uSessionHandler.getUserIdFromSessionId(sessionId);
        if(userId == -1){//there is the risk of exactly matching a remove between the validation and score setting.
            return false;
        }

        scoreHandler.addToRanking(userId, level, score);
        return true;
    }

    @Override
    public String getLevelHighScoreList(int level) {
        return scoreHandler.getFlatRanking(level);
    }
}
