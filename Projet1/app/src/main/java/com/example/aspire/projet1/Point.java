package com.example.aspire.projet1;



public class Point {

    public Point(int p_x, int p_y) {
        x = p_x;
        y = p_y;
    }

    public Point(float x2, float y2) {
        x = (int)x2;
        y = (int)y2;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public int x;
    public int y;

}
