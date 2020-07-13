package edu.berkeley.hygieneheroes;

import java.util.ArrayList;

public class ChanceCard {
    private String cardImage;
    private String cardText;
    private String cardSound;
    private String defaultSound;
    private ArrayList<String> actions;

    public ChanceCard(String image, String text, String sound, ArrayList<String> chanceActions) {
        cardImage = image;
        cardText = text;
        cardSound = sound;
        actions = chanceActions;
    }

    public ArrayList<String> getActions() {
        return actions;
    }
}
