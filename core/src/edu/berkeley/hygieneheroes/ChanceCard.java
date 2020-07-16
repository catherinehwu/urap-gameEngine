package edu.berkeley.hygieneheroes;

import java.util.ArrayList;

/**
 * Class representing a single Chance Card.
 * Each chance card can have an image, sound, and associated text string.
 * All chance cards also have a list of actions - although they may or may
 * not have more than one associated action.
 */
public class ChanceCard {
    private String cardImage;
    private String cardSound;
    private String cardText;
    private String defaultSound;
    private ArrayList<String> actions;

    /**
     * Constructor
     * @param image - image for this chance card
     * @param sound - sound to play when this card is drawn
     * @param text - text to display when card is drawn
     * @param chanceActions - actions that will occur to the player token when card is drawn
     */
    public ChanceCard(String image, String sound, String text, ArrayList<String> chanceActions) {
        cardImage = image;
        cardSound = sound;
        cardText = text;
        actions = chanceActions;
    }

    /**
     * Returns all the actions corresponding to this Chance Card
     */
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
