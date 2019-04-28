package com.udemy.spark.app;

import java.io.Serializable;

public class groupPlayer implements Serializable {
    String playerName;
    int matchCount;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public groupPlayer(String playerName, int matchCount) {
        this.playerName = playerName;
        this.matchCount = matchCount;
    }
}
