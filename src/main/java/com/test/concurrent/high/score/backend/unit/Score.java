package com.test.concurrent.high.score.backend.unit;

/**
 * Data class for Score information
 */
public class Score {
    private int userId;
    private int score;

    public Score(int userId, int score){
        this.userId = userId;
        this.score = score;
    }

    public int getUserId() {
        return userId;
    }

    public int getScore() {
        return score;
    }
}
