package com.test.concurrent.high.score.backend.model;

import com.test.concurrent.high.score.backend.unit.Score;

import java.util.List;

public interface ScoreHandler {
    /**
     * Attempts to add score for level made by user to the ranking lists.
     * Will not add if the ranking-board is full and score is not eligible
     * @param userId , user that got the score
     * @param level , level score was received on
     * @param score , score received
     */
    void addToRanking(int userId, int level, int score);

    /**
     * Get a ranking-board as a list format
     * @param level , level to get the ranking-board for
     * @return
     */
    List<Score> getRanking(int level);

    /**
     * Get the ranking-board for input level as a flat string in format:
     * "userid1=score1,userid2=score2", in descending order.
     * @param level , level to get the ranking-board for
     * @return
     */
    String getFlatRanking(int level);
}
