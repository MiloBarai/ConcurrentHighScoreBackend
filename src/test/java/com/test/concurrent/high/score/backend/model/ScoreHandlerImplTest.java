package com.test.concurrent.high.score.backend.model;

import com.test.concurrent.high.score.backend.unit.Score;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ScoreHandlerImplTest extends TestCase {

    ScoreHandlerImpl scoreHandler = ScoreHandlerImpl.getInstance();

    @Test
    public void testAddHocLevelList(){
        scoreHandler.clean();

        List<Score> scores = scoreHandler.getRanking(1);
        assertTrue(scores.isEmpty());
    }

    @Test
    public void testAddHocLevelFlatString(){
        scoreHandler.clean();

        String flatList = scoreHandler.getFlatRanking(1);
        assertTrue(flatList.isEmpty());
    }

    @Test
    public void testAddToRankingLevelList(){
        scoreHandler.clean();
        int userId = 1;
        int level = 1;
        int score = 50;
        scoreHandler.addToRanking(userId, level,score);
        List<Score> scores = scoreHandler.getRanking(1);

        assertEquals(scores.get(0).getUserId(), userId);
        assertEquals(scores.get(0).getScore(), score);
    }

    @Test
    public void testAddToRankingLevelFlatString(){
        scoreHandler.clean();
        int userId = 1;
        int level = 1;
        int score = 50;
        scoreHandler.addToRanking(userId, level, score);
        String scores = scoreHandler.getFlatRanking(1);

        assertEquals(scores, userId+"="+score);
    }

    @Test
    public void testRankingOrder(){
        scoreHandler.clean();
        int[] userId = {1, 2, 5};
        int[] score = {50, 20, 55};
        int[] expectedUserOrder = {5,1,2};

        int level = 1;

        for(int i=0; i<userId.length; i++){
            scoreHandler.addToRanking(userId[i], level, score[i]);
        }

        int [] userOrder = new int[userId.length];
        List<Score> scores = scoreHandler.getRanking(level);

        for(int i=0; i<scores.size(); i++){
            userOrder[i] = scores.get(i).getUserId();
        }

        Assert.assertArrayEquals(userOrder, expectedUserOrder);
    }

    @Test
    public void testRankingOrderFlat(){
        scoreHandler.clean();
        int[] userId = {1, 2, 5};
        int[] score = {50, 20, 55};
        String expectedResult = "5=55,1=50,2=20";

        int level = 1;

        for(int i=0; i<userId.length; i++){
            scoreHandler.addToRanking(userId[i], level, score[i]);
        }

        String scores = scoreHandler.getFlatRanking(level);

        assertEquals(scores, expectedResult);
    }

    @Test
    public void testRankingOverflowOrdering(){
        scoreHandler.clean();
        int[] userId = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        int[] score = {10, 24, 32, 43, 55, 61, 72, 83, 95, 101, 11, 12, 13, 145, 15, 17, 17, 18, 192, 201};
        int[] expectedUserOrder = {20, 19, 14, 10, 9, 8, 7, 6, 5, 4, 3, 2, 18, 16, 17};

        int level = 1;

        for(int i=0; i<userId.length; i++){
            scoreHandler.addToRanking(userId[i], level, score[i]);
        }

        int [] userOrder = new int[expectedUserOrder.length];
        List<Score> scores = scoreHandler.getRanking(level);

        for(int i=0; i<scores.size(); i++){
            System.out.println("Score: "+scores.get(i).getScore()+ ", user:"+scores.get(i).getUserId());
            userOrder[i] = scores.get(i).getUserId();
        }

        Assert.assertArrayEquals(userOrder, expectedUserOrder);
    }

    @Test
    public void testRankingIsPerLevel(){
        scoreHandler.clean();
        int[] userId = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        int[] score = {10, 24, 32, 43, 55, 61, 72, 83, 95, 101, 11, 12, 13, 145, 15, 17, 17, 18, 192, 201};

        int level = 1;

        for(int i=0; i<userId.length; i++){
            scoreHandler.addToRanking(userId[i], level, score[i]);
        }

        List<Score> scores = scoreHandler.getRanking(2);

        assertTrue(scores.isEmpty());
    }


}
