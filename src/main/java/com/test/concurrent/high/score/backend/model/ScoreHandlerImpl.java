package com.test.concurrent.high.score.backend.model;

import com.test.concurrent.high.score.backend.unit.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ScoreHandleImpl,
 * Responsible for handling the scoreboards of different levels.
 */
public class ScoreHandlerImpl implements ScoreHandler {

    private static volatile ScoreHandlerImpl instance;

    public static ScoreHandlerImpl getInstance() {
        if (instance == null) {
            synchronized (ScoreHandlerImpl.class) {
                if (instance == null) {
                    instance = new ScoreHandlerImpl();
                }
            }
        }
        return instance;
    }

    private HashMap<Integer, ArrayList<Score>> levelScoreBoard = new HashMap<>();
    private int maxRank = 15;

    private ScoreHandlerImpl() {
    }

    /**
     * Cleans all the scoreboards.
     */
    public void clean() {
        levelScoreBoard = new HashMap<>();
    }

    /**
     * Gets the max ranks available in each scoreboard
     * @return
     */
    public int getMaxRankings() {
        return maxRank;
    }

    @Override
    public void addToRanking(int userId, int level, int score) {
        ArrayList<Score> scores;

        if (levelScoreBoard.containsKey(level)) {
            scores = levelScoreBoard.get(level);
        } else {
            scores = new ArrayList<>();
            levelScoreBoard.put(level, scores);
        }

        //If the ranking table is not full or the score is larger than the lowest rank change ranking.
        if (scores.size() < maxRank || scores.get(scores.size() - 1).getScore() < score) {
            addToRankingList(userId, level, score);
        } else {
            System.out.println("Scoring was to low to make it into the ranking tables [userId = " + userId + ", level = " + level + ", score = " + score + "]");
        }
    }


    private synchronized void addToRankingList(int userId, int level, int score) {
        /*
        * Synchronized due to it performing scoreboard changes. if several threads were in this
        * function at there could be issues with insertions and compares since all threads will or can
        * modify the scores.
        */
        ArrayList<Score> scores;

        if (levelScoreBoard.containsKey(level)) {
            scores = levelScoreBoard.get(level);
        } else {
            scores = new ArrayList<>();
            levelScoreBoard.put(level, scores);
        }

        //If the ranking table is not full or the score is larger than the lowest rank change ranking.
        if (scores.size() < maxRank || scores.get(scores.size() - 1).getScore() < score) {

            int insertIndex = scores.size();
            Score previousUserScore = null;

            for (int i = scores.size() - 1; i >= 0; i--) {
                if (score > scores.get(i).getScore()) {
                    insertIndex = i;
                }
                if (scores.get(i).getUserId() == userId) {
                    previousUserScore = scores.get(i);
                }
            }

            if (previousUserScore != null && previousUserScore.getScore() < score) {
                System.out.println("Ranked user achieved a better score: [userId = " + userId + ", level = " + level + ", score = " + score + "]");
                scores.add(insertIndex, new Score(userId, score));
                scores.remove(previousUserScore);
            } else if (previousUserScore == null) {
                System.out.println("User and Score added to ranks: [userId = " + userId + ", level = " + level + ", score = " + score + "]");
                scores.add(insertIndex, new Score(userId, score));
            }

            //remove overflow from ranking table
            if (scores.size() > maxRank) {
                Score removedScore = scores.remove(scores.size() - 1);
                System.out.println("User and Score fell out of ranks: [userId = " + removedScore.getUserId() + ", level = " + level + ", score = " + removedScore.getScore() + "]");
            }
        } else {
            System.out.println("Scoring was to low to make it into the ranking tables [userId = " + userId + ", level = " + level + ", score = " + score + "]");
        }
    }

    @Override
    public List<Score> getRanking(int level) {
        ArrayList<Score> scores;

        if (levelScoreBoard.containsKey(level)) {
            scores = levelScoreBoard.get(level);
        } else {
            scores = new ArrayList<>();
            levelScoreBoard.put(level, scores);
        }
        return scores;
    }

    @Override
    public String getFlatRanking(int level) {
        StringBuilder flatRanking = new StringBuilder();
        List<Score> ranking = getRanking(level);
        for (Score s : ranking) {
            flatRanking.append(s.getUserId() + "=" + s.getScore() + ",");
        }
        if (flatRanking.length() > 0) {
            flatRanking.deleteCharAt(flatRanking.length() - 1);
        }
        return flatRanking.toString();
    }
}
