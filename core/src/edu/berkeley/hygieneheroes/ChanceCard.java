package edu.berkeley.hygieneheroes;

import java.util.ArrayList;

public class ChanceCard {
    private String cardImage;
    private String cardSound;
    private String cardText;
    private String defaultSound;
    private ArrayList<String> actions;

    public ChanceCard(String image, String sound, String text, ArrayList<String> chanceActions) {
        cardImage = image;
        cardSound = sound;
        cardText = text;
        actions = chanceActions;
    }

    public ArrayList<String> getActions() {
        return actions;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (String act : actions) {
            result.append(act);
            result.append(" ");
        }
        return result.toString();
    }
}
