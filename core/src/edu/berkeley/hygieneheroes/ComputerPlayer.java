package edu.berkeley.hygieneheroes;

public class ComputerPlayer extends Player {

    public ComputerPlayer(String playerName, String imageFile, GameEngine curGame, int pNum) {
        super(playerName, imageFile, curGame, pNum);
    }

    public boolean isComputerPlayer() {
        return true;
    }
}
