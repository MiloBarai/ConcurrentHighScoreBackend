package com.test.concurrent.high.score.backend.model;

import com.test.concurrent.high.score.backend.unit.Session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * UserSessionHandlerImpl,
 * Responsible for generating, storing, validating and cleaning up user sessions.
 */
public class UserSessionHandlerImpl implements UserSessionHandler {

    private static volatile UserSessionHandlerImpl instance;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    public static UserSessionHandlerImpl getInstance() {
        if (instance == null) {
            synchronized (UserSessionHandlerImpl.class) {
                if (instance == null) {
                    instance = new UserSessionHandlerImpl();
                }
            }
        }
        return instance;
    }

    /*
     * TODO: The map from session to SessionId to session is required for quickly getting the session tied to the Id
     *  There could also be a user to session map inorder to invalidate or remove multiple sessions for the same user.
     *  For this simple solution this is not taken into account and sessions are active until they expire.
     */
    private HashMap<String, Session> sessionIdToSession = new HashMap<>();
    private LinkedList<Session> activeSessions = new LinkedList<>();
    private int sessionLifeDuration = 60*10; //10 minutes

    private UserSessionHandlerImpl() {

    }

    /**
     * Clears all available sessions and resets the sessionLifeDuration to default.
     */
    public void clean(){
        sessionIdToSession = new HashMap<>();
        activeSessions = new LinkedList<>();
        sessionLifeDuration = 60*10; //10 minutes
    }

    /**
     * Changes the session keep alive duration.
     *
     * IF SESSION TIME IS CHANGED AFTER SESSIONS HAVE BEEN CREATED
     * THE SESSION CLEANUP WILL NOT WORK PROPERLY
     *
     * @param sessionLifeDuration, new session life duration
     */
    public void setSessionLifeDuration(int sessionLifeDuration) {
        this.sessionLifeDuration = sessionLifeDuration;
    }

    @Override
    public Session generateUserSession(int userId) {
        /*
         * TODO: In a real world application the sessionId should not be
         *  this easy to deduct or gain information from. Preferably there would
         *  be an addition of some hashing type or using this value as a random seed
         *  in order to make it harder to steal a session id.
         *  But for this simple application i keep it simple at the moment.
         */
        String simpleSessionId = (userId + dateFormat.format(new Date()));
        //Special character cleanup and uppercase
        simpleSessionId = simpleSessionId.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

        var calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, sessionLifeDuration);

        Session newUserSession = new Session(userId, simpleSessionId, calendar.getTime());
        addToActiveSessions(newUserSession);

        return newUserSession;
    }

    @Override
    public boolean validateSession(String sessionId) {
        System.out.println("Validating Session id: "+sessionId);
        cleanupActiveSessions();
        Session sessionToValidate = sessionIdToSession.get(sessionId);
        Date currentDate = new Date();

        if (sessionToValidate != null && sessionToValidate.getExpirationDate().after(currentDate)){
            System.out.println("Session with id: "+sessionId+", VALID");
            return true;
        }

        System.out.println("Session with id: "+sessionId+", --NOT-- VALID");
        return false;
    }

    @Override
    public int getUserIdFromSessionId(String sessionId) {
        Session session = sessionIdToSession.get(sessionId);
        if (session != null)
            return session.getUserId();

        return -1;
    }

    /**
     * Adds the {@code sessionToAdd} into the LinkedList activeSessions Queue.
     *
     * @param sessionToAdd
     */
    private void addToActiveSessions(Session sessionToAdd) {
        cleanupActiveSessions();
        sessionIdToSession.put(sessionToAdd.getSessionId(), sessionToAdd);
        activeSessions.addLast(sessionToAdd);
    }

    /**
     * Cleans up the expired activeSessions from the hashmap and the linkedList.
     * The cleanup is ran whenever a new session is added or a session is validated.
     * The entire function is synchronized.
     */
    private synchronized void cleanupActiveSessions() {
        /*
         * The entire function is synchronized since the remove has a race condition when multithreading.
         * (There is a risk that two (or more) threads peeking the same value and both doing a "remove first" resulting
         * in two values being removed. One of which hasn't been validated for a remove.)
         */
        System.out.println("Running active session cleanup");
        Date currentTime = new Date();
        while (!activeSessions.isEmpty()) {
            if (activeSessions.peek().getExpirationDate().before(currentTime)) {
                var session = activeSessions.removeFirst();
                sessionIdToSession.remove(session.getSessionId());
                System.out.println("removed sessionid: "+session.getSessionId()+", for user: "+session.getUserId()+", from active sessions.");
            } else {
                break;
            }
        }
    }
}
