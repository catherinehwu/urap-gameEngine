package edu.berkeley.hygieneheroes;

public class ComputerPlayer extends Player {

    public ComputerPlayer(String playerName, String imageFile, GameEngine curGame, int pNum, int tokenN) {
        super(playerName, imageFile, curGame, pNum, tokenN);
    }

    public boolean isComputerPlayer() {
        return true;
    }
}
