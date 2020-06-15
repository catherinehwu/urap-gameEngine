package edu.berkeley.hygieneheroes;

import java.util.ArrayList;

public class Board {
    private Square[] ordering;
    private float Xrange;
    private float Yrange;
    private int totalSqNum;
    private Square start;
    private Square end;

    public Board(float XRange, float YRange, int endPosNum) {
        Xrange = XRange;
        Yrange = YRange;
        totalSqNum = endPosNum;
        ordering = new Square[endPosNum + 1];
    }

    public void setSquare(int num, float sqX, float sqY,
                          String picture, String text, String sound,
                          ArrayList<String> listOfActions) {
        Square sq = new Square(num, sqX, sqY, picture, text, sound, listOfActions);
        ordering[num] = sq;

        if (num == 0) {
            start = sq;
        } else if (num == totalSqNum - 1) {
            end = sq;
        }
    }

    public Square getSquare(int num) {
        return ordering[num];
    }

    public Square getStart() {
        return start;
    }

    public Square getEnd() {
        return end;
    }

    public int getTotalSqNum() {
        return totalSqNum;
    }

    public float getXrange() {
        return Xrange;
    }

    public float getYrange() {
        return Yrange;
    }
}