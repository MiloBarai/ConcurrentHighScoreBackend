package com.test.concurrent.high.score.backend.controller;

import com.test.concurrent.high.score.backend.unit.Score;
import com.test.concurrent.high.score.backend.model.ScoreHandlerImpl;
import com.test.concurrent.high.score.backend.model.UserSessionHandlerImpl;
import junit.framework.TestCase;
import org.junit.Test;

/*
 * Some functions here would probably be considered integration tests since there
 * are several modules working together.
 */
public class ServerControllerImplTest extends TestCase {

    UserSessionHandlerImpl uSession = UserSessionHandlerImpl.getInstance();
    ScoreHandlerImpl scoreHandler = ScoreHandlerImpl.getInstance();
    ServerControllerImpl sc = new ServerControllerImpl(uSession, scoreHandler);

    @Test
    public void testLogin(){
        uSession.clean();

        String session = sc.login(13);
        assertTrue(uSession.validateSession(session));
    }

    @Test
    public void testValidateSession(){
        uSession.clean();

        String session = sc.login(30);
        assertTrue(sc.validateSession(session));
    }

    @Test
    public void testInvalidateSession(){
        uSession.clean();
        String notValid = "TOTALYNOTVALID";
        assertFalse(sc.validateSession(notValid));
    }

    @Test
    public void  testSetScoreValidSession(){
        uSession.clean();
        scoreHandler.clean();

        String session = sc.login(30);
        assertTrue(sc.setUserScore(session, 1, 10));
    }

    @Test
    public void testSetScoreInvalidSession(){
        uSession.clean();
        scoreHandler.clean();

        String session = "NOTAVALIDSESSIONAGAIN";
        assertFalse(sc.setUserScore(session, 1, 10));
    }

    @Test
    public void testSetScore(){
        uSession.clean();
        scoreHandler.clean();

        int userId = 10;
        int score = 30;

        String session = sc.login(userId);
        sc.setUserScore(session, 1, score);
        Score scoreUnit = scoreHandler.getRanking(1).get(0);
        assertEquals(scoreUnit.getUserId(), userId);
        assertEquals(scoreUnit.getScore(), score);
    }
}
