package edu.berkeley.hygieneheroes;

import java.util.ArrayList;

public class Square {

    private float x; //Float Value
    private float y; //Float Value
    private int seqNum;
    private String squareImage;
    private String squareText;
    private String squareSound;
    private String defaultSound;
    private ArrayList<String> actions;

    // Changing x and y to be floats
    public Square (int num, float sqX, float sqY,
                   String picture, String text, String sound,
                   ArrayList<String> listOfActions) {
        x = sqX;
        y = sqY;
        seqNum = num;
        squareImage = picture;
        squareText = text;
        squareSound = sound;
        actions = listOfActions; // copying reference of list

    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public ArrayList<String> getActions() {
        return actions;
    }

    public String getSquareSound() {
        return squareSound;
    }

    public String getDefaultSound() {
        return defaultSound;
    }

    public void setDefaultSound(String sound) {
        defaultSound = sound;
    }

    // Gets sound - either specific or default (by game or by column type)
    public String getSound() {
        if (getSquareSound() != null) {
            return getSquareSound();
        }
        return getDefaultSound();
    }

    // Resets default sound back to null
    public void resetSound() {
        if (defaultSound != null) {
            defaultSound = null;
        }
    }

}
