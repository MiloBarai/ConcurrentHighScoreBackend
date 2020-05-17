package com.test.concurrent.high.score.backend.model;


import com.test.concurrent.high.score.backend.unit.Session;
import junit.framework.TestCase;
import org.junit.Test;

public class UserSessionHandlerImplTest extends TestCase {
    UserSessionHandlerImpl uSessionHandler = UserSessionHandlerImpl.getInstance();

    @Test
    public void testIncorrectUserSession(){
        uSessionHandler.clean();
        String sessionKey = "NONEXISTINGSESSIONKEY";
        assertFalse(uSessionHandler.validateSession(sessionKey));
    }

    @Test
    public void testUserSessionKey(){
        uSessionHandler.clean();
        Session sessionKey = uSessionHandler.generateUserSession(1);
        assertTrue(uSessionHandler.validateSession(sessionKey.getSessionId()));
    }

    @Test
    public void testUserSessionKeyToUserMapping(){
        uSessionHandler.clean();
        int userId = 99;
        Session sessionKey = uSessionHandler.generateUserSession(userId);
        int sessionExpectedUser = uSessionHandler.getUserIdFromSessionId(sessionKey.getSessionId());

        assertEquals(userId, sessionExpectedUser);
    }

    @Test
    public void testUserSessionKeyDuration() throws InterruptedException {
        uSessionHandler.clean();

        //Each session is alive for 1 seconds
        uSessionHandler.setSessionLifeDuration(1);

        Session sessionKey = uSessionHandler.generateUserSession(33);
        assertTrue(uSessionHandler.validateSession(sessionKey.getSessionId()));
        Thread.sleep(900);
        Session sessionKey2 = uSessionHandler.generateUserSession(33);
        Thread.sleep(100);
        assertFalse(uSessionHandler.validateSession(sessionKey.getSessionId()));
        assertTrue(uSessionHandler.validateSession(sessionKey2.getSessionId()));
    }

    @Test
    public void testMultipleSessions(){
        uSessionHandler.clean();

        Session s1 = uSessionHandler.generateUserSession(30);
        Session s2 = uSessionHandler.generateUserSession(20);

        assertTrue(uSessionHandler.validateSession(s1.getSessionId()));
        assertTrue(uSessionHandler.validateSession(s2.getSessionId()));
    }

}
