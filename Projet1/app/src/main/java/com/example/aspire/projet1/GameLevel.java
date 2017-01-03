package com.example.aspire.projet1;



public class GameLevel {

    private GameLevel(int id, int timeOut, int numberOfColor, int emptyCase,
                      int minScore) {
        super();
        this.id = id;
        this.timeOut = timeOut;
        this.numberOfColor = numberOfColor;
        this.emptyCase = emptyCase;
        this.minScore = minScore;
    }

    public static GameLevel getInstance(int niveau) {
        GameLevel gl = null;
        switch (niveau) {
            case 1:
                gl = new GameLevel(1, 50, 7, 25, 1680);
                break;


            default:

                gl = new GameLevel(1, 50, 3, 25, 1680);

                break;

        }

        return gl;
    }

    public int id;
    public int timeOut;
    public int numberOfColor;
    public int emptyCase;
    public int minScore;

}
