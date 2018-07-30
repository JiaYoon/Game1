package com.example.ga.rps.data;

import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Created by GA on 2018. 4. 14..
 */

public enum HandType {
    ROCK("Rock", "Paper", "Scissors"),
    SCISSOR("Scissors", "Rock", "Paper"),
    PAPER("Paper", "Scissors", "Rock");

    private String attr;
    //보다 이기는 애
    private String winner;
    //보다 지는 애
    private String loser;

    HandType(String attr, String winner, String loser) {
        this.attr = attr;
        this.winner = winner;
        this.loser = loser;
    }

    public static HandType getRandomHandType() {
        switch (new Random().nextInt(3)) {
            case 0:
                Log.d("asd", "rock");
                return ROCK;
            case 1:
                Log.d("asd", "SCISSOR");

                return SCISSOR;
            case 2:
                Log.d("asd", "paper");

                return PAPER;
        }
        return ROCK;
    }

    @Nullable
    public static HandType getHandType(String attr) {
        for (HandType handType : values()) {
            if (handType.attr.equals(attr)) {
                return handType;
            }
        }
        return null;
    }

    public String getAttr() {
        return attr;
    }

    public String getWinner() {
        return winner;
    }

    public String getLoser() {
        return loser;
    }
}