package com.example.aspire.projet1;

import java.util.Random;
import java.util.Vector;





import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import junit.framework.*;

public class GameView extends SurfaceView implements SurfaceHolder.Callback,
        Runnable {

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e("-> Constructeur <-", "GameView ");
        holder = getHolder();
        holder.addCallback(this);
        mContext = context;

        mRes = mContext.getResources();
        loadImages(mRes);
        rd = new Random();

        gameThread = new Thread(this);
        setFocusable(true);
        font = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/OpenSans_Bold.ttf");

    }
    // matrice pour couper  le terrain en des points  **************map=l'ecran
    private void initMap() {
        for (short i = 0; i < mapHeight; i++) {
            for (short j = 0; j < mapWidth; j++) {
                map[i][j] = EMPTY_BLOCK;
            }
        }
    }
    //pour pouvoir inserer les couleur alors on a devise  l'image par une matrice
    private void insertBlocks(GameLevel gl) {
        int x, y;
        int notEmpty = mapWidth * mapHeight - gl.emptyCase;
        stillToPlay = notEmpty;
        for (short i = 0; i < notEmpty; i++) {
            do {
                x = Math.abs(rd.nextInt() % mapHeight);
                y = Math.abs(rd.nextInt() % mapWidth);
            } while (map[x][y] != 0);

            map[x][y] = Math.abs(rd.nextInt() % gl.numberOfColor) + 1;//math.abs elle retourne la  valeur absolu
        }

    }


    public void initParameters() {

        Log.e("-> FCT <-", "initParameters");
        map = new int[mapHeight][mapWidth];
        mapTileSize = mapTile[0].getWidth();
        timeTileWidth = timeBar[0].getWidth();

        mapTopAnchor = (myHeight() - mapHeight * mapTileSize);
        mapLeftAnchor = (myWidth() - mapWidth * mapTileSize) / 2;
        centerScore = ((myWidth() / 2) - minScoreImg.getWidth()) / 2;
        centerLevel = (myWidth() / 2) + ((myWidth() / 2) - levelImg.getWidth())
                / 2;

        if (mContext != null) {
            SharedPreferences settings = mContext.getSharedPreferences(
                    "COLORMATCH", 0);
            gameInProgress = settings.getBoolean("GAMEINPROG", false);
        }
        if (gameInProgress) {
            load();
        } else {
            currentLevel = 1;
            gameLevel = GameLevel.getInstance(1);
            initMap();
            insertBlocks(gameLevel);
            startingTime = System.currentTimeMillis();

        }

        // lancement
        win = false;
        endOfGame = false;
        counter = gameLevel.timeOut;
        testcase = new TestCase[4];
        vect = new Vector<Point>();

        in = true;

        if ((gameThread != null) && (!gameThread.isAlive())) {
            gameThread.start();
        } else {
            gameThread = new Thread(this);
        }
    }

    private void loadImages(Resources res) {

        mapTile[0] = BitmapFactory.decodeResource(res, R.drawable.empty);
        mapTile[1] = BitmapFactory.decodeResource(res, R.drawable.green);
        mapTile[2] = BitmapFactory.decodeResource(res, R.drawable.red);
        mapTile[3] = BitmapFactory.decodeResource(res, R.drawable.yellow);
        mapTile[4] = BitmapFactory.decodeResource(res, R.drawable.violet);
        mapTile[5] = BitmapFactory.decodeResource(res, R.drawable.bleum);
        mapTile[6] = BitmapFactory.decodeResource(res, R.drawable.marron);
        mapTile[7] = BitmapFactory.decodeResource(res, R.drawable.blue);





        timeBar[0] = BitmapFactory.decodeResource(res, R.drawable.barfull);
        timeBar[1] = BitmapFactory.decodeResource(res, R.drawable.barstop);
        timeBar[2] = BitmapFactory.decodeResource(res, R.drawable.barempty);


        terrain = BitmapFactory.decodeResource(res, R.drawable.terrain);

        rule = BitmapFactory.decodeResource(res, R.drawable.rule);
        minScoreImg = BitmapFactory.decodeResource(res, R.drawable.minscore);

    }

    public void nDraw(Canvas canvas) {
        canvas.drawRGB(24, 28, 40);

        paintTerrain(canvas);
        paintMap(canvas);
        paintTimeBar(canvas);
        paintScore(canvas);
        paintPoint(canvas);
    }

    private void paintTerrain(Canvas canvas) {

        canvas.drawBitmap(terrain, mapLeftAnchor, mapTopAnchor, null);
    }

    private void paintMap(Canvas c) {
        Bitmap tmpimg = mapTile[0];

        for (short i = 0; i < mapHeight; i++) {
            for (short j = 0; j < mapWidth; j++) {

                if (map[i][j] == EMPTY_BLOCK) {
                    tmpimg = mapTile[0];
                } else {
                    tmpimg = mapTile[map[i][j]];
                }
                c.drawBitmap(tmpimg, mapLeftAnchor + (j * mapTileSize),
                        mapTopAnchor + (i * mapTileSize), null);
            }

        }
    }

    private void paintTimeBar(Canvas canvas) {
        int w = (myWidth() / timeTileWidth); // w -- > counter
        int diff = (timeBar[1].getWidth()) - timeTileWidth;

        for (int i = 0; i <= w; i++) {
            if (i < ratio * w) {
                canvas.drawBitmap(timeBar[2], (timeTileWidth * (w - i)) + diff
                        - (timeBar[1].getWidth()), 0, null);
            } else if (i > ratio * w) {
                canvas.drawBitmap(timeBar[0], timeTileWidth * (w - i)
                        - (timeBar[1].getWidth()), 0, null);
            }

        }
        canvas.drawBitmap(timeBar[1], timeTileWidth * (w - (int) (w * ratio))
                - (timeBar[1].getWidth()), 0, null);

    }

    private void paintScore(Canvas canvas) {
        String tmp = gameLevel.minScore + "";

        canvas.drawBitmap(minScoreImg, centerScore, mapTileSize * 2, null); // pour


    }

    private void paintPoint(Canvas c) {

        if (vect.size() >= 1) {
            for (Point p : vect) {
                c.drawBitmap(rule, mapLeftAnchor + (mapTileSize) * p.x,
                        mapTopAnchor + (mapTileSize * p.y), null);

            }

           for (short i = 0; i < testcase.length; i++) {
                if (testcase[i] != null && testcase[i].doitdisparetre) {

                    map[testcase[i].y][testcase[i].x] = 0;
                }
            }
            vect = null;
            testcase = null;
            testcase = new TestCase[4];
            vect = new Vector<Point>();
        }

    }

    @Override
    public void run() {
        Canvas c = null;
        buffer = Bitmap.createBitmap(myWidth(), myHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c2 = new Canvas(buffer);

        int diff;

        while (in) {
            try {
                gameThread.sleep(40);
                try {
                    if (!loading) {
                        diff = (int) (System.currentTimeMillis() - startingTime) / 1000;
                        ratio = (float) diff / (float) counter;

                        if (ratio >= 1 || isAllEmpty()) {
                            endOfGame = true;
                            win = hasWon();
                        }
                    }
                    if (endOfGame) {





                        score = 0;
                        endOfGame = false;
                        startingTime = System.currentTimeMillis();
                    }
                    nDraw(c2);
                    c = holder.lockCanvas(null);
                    if (c != null)
                        c.drawBitmap(buffer, new Rect(0, 0, myWidth(),
                                myHeight()), new Rect(0, 0, getWidth(),
                                getHeight()), null);

                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            } catch (Exception e) {
                Log.e("-> RUN <-", "PB DANS RUN");
            }
        }

    }


    public void setGameThread(Thread gameThread) {
        this.gameThread = gameThread;
    }

    private synchronized void askToReplay() {

        parentActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.e(" askToReplay", " run");
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                TextView tv = new TextView(mContext);
                tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));

                tv.setPadding(30, 40, 30, 30);
                tv.setTextSize(20);
                tv.setTextColor(parentActivity.getResources().getColor(
                        R.color.blue));
                tv.setBackgroundColor(Color.WHITE);
                tv.setTypeface(font);
                tv.setGravity(Gravity.CENTER);
                tv.setText(mContext.getResources().getString(
                        R.string.replayContent));

                dialog.setView(tv);
                dialog.setPositiveButton(
                        mContext.getResources().getString(R.string.yes),
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                initMap();
                                insertBlocks(gameLevel);
                                endOfGame = false;
                                counter = gameLevel.timeOut;
                                startingTime = System.currentTimeMillis();
                            }

                        });
                dialog.setNegativeButton(
                        mContext.getResources().getString(R.string.no),
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                parentActivity.finish();

                            }

                        });
                dialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        parentActivity.finish();
                    }

                });
                dialog.show();

            }

        });

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float myx = 1 * event.getX();
        float myy = 1 * event.getY();
        myx = myx / ((float) getWidth() / (float) myWidth());
        myy = myy / ((float) getHeight() / (float) myHeight());

        if (myy > mapTopAnchor && myx > mapLeftAnchor
                && myx < (mapWidth * mapTileSize) + mapLeftAnchor) {
            x = ((int) myx - mapLeftAnchor) / mapTileSize;
            y = ((int) myy - mapTopAnchor) / mapTileSize;

            Log.e("-> FCT <-", "onTouchEvent: " + x + "," + y);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (map[y][x] == 0)
                        play();
                    break;
                case MotionEvent.ACTION_MOVE:
                    Point p = new Point(event.getX(), event.getY());
                    Log.e("-> FCT <-", "onTouchEvent: moving " + p.x + "," + p.y);

                    break;
            }
        }

        return super.onTouchEvent(event);

    }

    private void play() {
        int scoreCounter = 0;
        gameInProgress = true;
        Vector<Point> vectTop, vectBottom, vectRight, vectLeft;
        vectTop = new Vector<Point>();
        vectBottom = new Vector<Point>();
        vectRight = new Vector<Point>();
        vectLeft = new Vector<Point>();

        int top = -1, right = -1, left = -1, bottom = -1;
        vect.add(new Point(x, y));

        for (int i = y; i >= 0; i--) {
            if (map[i][x] != 0 && i != y) {
                top = i;
                testcase[1] = new com.example.aspire.projet1.TestCase(x, i, map[i][x], TOP);
               testcase[1] = new TestCase(x, i, map[i][x], TOP);
                addTopPoints(x, y, top, vectTop);
                break;
            }
        }
        for (int i = y; i < mapHeight; i++) {
            if (map[i][x] != 0 && i != y) {
                bottom = i;
               testcase[3] = new TestCase(x, i, map[i][x], BOTTOM);
                addBottomPoints(x, y, bottom, vectBottom);
                break;
            }
        }


        for (int i = x; i >= 0; i--)
            if (map[y][i] != 0 && i != x) {
                left = i;
                testcase[0] = new TestCase(i, y, map[y][i], LEFT);
                addLeftPoints(x, y, left, vectLeft);
                break;
            }
        for (int i = x; i < mapWidth; i++) {
            if (map[y][i] != 0 && i != x) {
                right = i;
                testcase[2] = new TestCase(i, y, map[y][i], RIGHT);
                addRightPoints(x, y, right, vectRight);
                break;
            }
        }

      Log.e("-> FCT <-", left + "," + top + "," + right + "," + bottom);

        for (int i = 0; i < (testcase.length - 1); i++) {
            for (int j = 1; j < testcase.length; j++) {
                if (testcase[i] != null && testcase[j] != null
                        && (testcase[i].couleur == testcase[j].couleur)
                        && testcase[i].couleur != -1 && i != j) {
                    testcase[i].doitdisparetre = true;
                    testcase[j].doitdisparetre = true;
                }
            }
        }
       for (int i = 0; i < testcase.length; i++) {
            if (testcase[i] != null && testcase[i].doitdisparetre) {
                scoreCounter++;
                if (testcase[i].localisaton == TOP) {
                    vect.addAll(vectTop);
                    stillToPlay--;
                }
                if (testcase[i].localisaton == BOTTOM) {
                    vect.addAll(vectBottom);
                    stillToPlay--;
                }
                if (testcase[i].localisaton == LEFT) {
                    vect.addAll(vectLeft);
                    stillToPlay--;
                }
                if (testcase[i].localisaton == RIGHT) {
                    vect.addAll(vectRight);
                    stillToPlay--;
                }
            }
        }
        if (scoreCounter != 0) {
            newScore(scoreCounter);
            playSound(0);
        } else {
            // redduire le temps
            startingTime -= 6000 / currentLevel;
            vibrate();

        }


    }

    private void newScore(int scoreCounter) {




        switch (scoreCounter) {
            case 2:
                score = score + 20;
                break;
            case 3:
                score = score + 60;
                break;
            case 4:
                score = score + 120;
                break;
            default:
                break;
        }
    }

    private void addTopPoints(int x_pressed, int y_pressed, int y,
                              Vector<Point> v) {
        for (int i = y_pressed; i >= y; i--) {
            v.add(new Point(x, i));
        }
    }

    private void addBottomPoints(int x_pressed, int y_pressed, int y,
                                 Vector<Point> v) {
        for (int i = y_pressed; i <= y; i++) {
            v.add(new Point(x, i));
        }
    }

    private void addLeftPoints(int x_pressed, int y_pressed, int x,
                               Vector<Point> v) {
        for (int i = x_pressed; i >= x; i--) {
            v.add(new Point(i, y_pressed));
        }
    }

    private void addRightPoints(int x_pressed, int y_pressed, int x,
                                Vector<Point> v) {
        for (int i = x_pressed; i <= x; i++) {
            v.add(new Point(i, y_pressed));
        }
    }

    private boolean hasWon() {
        long diff_time = (System.currentTimeMillis() - startingTime) / 1000;
        if (score >= gameLevel.minScore && counter >= (int) diff_time)
            return true;
        return false;
    }

    private void playSound(int choice) {


        try {
            switch (choice) {
                case 0:
                    if (mediaPlayer != null)
                        mediaPlayer.release();

                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            mediaPlayer = null;
        }

        if (mediaPlayer != null && soundOn == true) {
            mediaPlayer.setVolume(3, 3);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();

        }

    }

    protected synchronized void askToLoad() {
        loading = true;
        parentActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        parentActivity);

                TextView tv = new TextView(parentActivity);
                tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
                tv.setPadding(30, 40, 30, 40);
                tv.setTextSize(20);
                tv.setTextColor(parentActivity.getResources().getColor(
                        R.color.blue));
                tv.setBackgroundColor(Color.WHITE);
                tv.setTypeface(font);
                tv.setGravity(Gravity.CENTER);
                tv.setText(parentActivity.getResources().getString(
                        R.string.recover));

                dialog.setView(tv);
                dialog.setPositiveButton(parentActivity.getResources()
                                .getString(R.string.yes),
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                SharedPreferences settings = parentActivity
                                        .getSharedPreferences("COLORMATCH", 0);
                                SharedPreferences.Editor editor = settings
                                        .edit();
                                editor.putBoolean("GAMEINPROG", true);

                                load();
                                loading = false;
                                // faire ici
                                editor.commit();
                            }

                        });
                dialog.setNegativeButton(parentActivity.getResources()
                                .getString(R.string.no),
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                SharedPreferences settings = parentActivity
                                        .getSharedPreferences("COLORMATCH", 0);
                                SharedPreferences.Editor editor = settings
                                        .edit();
                                editor.putBoolean("GAMEINPROG", false);
                                gameLevel = null;
                                gameLevel = GameLevel.getInstance(1);
                                initMap();
                                insertBlocks(gameLevel);
                                score = 0;
                                loading = false;
                                startingTime = System.currentTimeMillis();
                                editor.commit();
                            }

                        });
                dialog.show();

            }

        });

    }

    protected synchronized void celebrate() {
        loading = true;

        parentActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        parentActivity);

                TextView tv = new TextView(parentActivity);
                tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
                tv.setPadding(30, 40, 30, 30);
                tv.setTextSize(20);
                tv.setTextColor(parentActivity.getResources().getColor(
                        R.color.blue));
                tv.setBackgroundColor(Color.WHITE);
                tv.setGravity(Gravity.CENTER);
                tv.setTypeface(font);
                tv.setText(message);

                dialog.setView(tv);

                dialog.setNegativeButton(parentActivity.getResources()
                                .getString(R.string.keep),
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                loading = false;
                                startingTime = System.currentTimeMillis();
                                message = "";
                            }

                        });
                dialog.show();

            }

        });

    }

    public void load() {
        if (mContext != null) {
            SharedPreferences settings = mContext.getSharedPreferences(
                    "COLORMATCH", 0);
            currentLevel = settings.getInt("IDLEVEL", 1);
            score = settings.getInt("SCORE", 0);
            stillToPlay = settings.getInt("STILLTOPLAY", 90);
            startingTime = settings.getLong("STARTINGTIME",
                    System.currentTimeMillis());
            exitingTime = settings.getLong("EXITINGTIME",
                    System.currentTimeMillis());

            startingTime = startingTime
                    + (System.currentTimeMillis() - exitingTime);

            for (short i = 0; i < mapHeight; i++) {
                for (short j = 0; j < mapWidth; j++) {
                    map[i][j] = settings.getInt("M:" + i + "," + j, 0);

                }
            }
            gameLevel = GameLevel.getInstance(currentLevel);

        }
    }

    public void save() {

        if (mContext != null) {
            SharedPreferences settings = mContext.getSharedPreferences(
                    "COLORMATCH", 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("IDLEVEL", gameLevel.id);
            editor.putInt("SCORE", score);
            editor.putInt("STILLTOPLAY", stillToPlay);
            editor.putLong("STARTINGTIME", startingTime);
            editor.putBoolean("GAMEINPROG", gameInProgress);
            editor.putLong("EXITINGTIME", System.currentTimeMillis());
            for (short i = 0; i < mapHeight; i++) {
                for (short j = 0; j < mapWidth; j++) {
                    editor.putInt("M:" + i + "," + j, map[i][j]);

                }
            }
            editor.commit();
        }

    }

    public void vibrate() {
        vibrator = (Vibrator) mContext
                .getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    private int myWidth() {
        return mapTileSize * mapWidth;
    }

    private int myHeight() {
        return mapTileSize * mapHeight + mapTileSize * 4; /*
														 * mapTileSize * 4 :
														 * pour dessiner
														 * l'en-tete
														 */
    }

    private boolean loadBestScore(int level, int score) {

        if (mContext != null) {
            SharedPreferences settings = mContext.getSharedPreferences(
                    "COLORMATCH", 0);
            int tmp = settings.getInt("SCORE" + level, 0);

            if (score > tmp) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("SCORE" + level, score);
                editor.commit();
                return true;
            }
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mContext != null) {
            SharedPreferences settings = mContext.getSharedPreferences(
                    "COLORMATCH", 0);
            gameInProgress = settings.getBoolean("GAMEINPROG", false);
        }
        if (gameInProgress == true) {
            // premier depart c'est caché
            askToLoad();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        initParameters();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private boolean isAllEmpty() {
        // zero or one bubble's left
        if (stillToPlay == 0 || stillToPlay == 1)
            return true;
        return false;
    }

    private Vibrator vibrator;
    private final int TOP = 1;
    private final int LEFT = 2;
    private final int BOTTOM = 3;
    private final int RIGHT = 4;
    private static Bitmap[] mapTile = new Bitmap[9];
    private static Bitmap[] timeBar = new Bitmap[3];

    private static Bitmap terrain;
    private static Bitmap rule;
    private static Bitmap minScoreImg;
    private static Bitmap levelImg;
    private static Bitmap buffer;
    private int timeTileWidth;
    private int mapTopAnchor;
    private int mapLeftAnchor;
    private int score = 0;
    final int EMPTY_BLOCK = 0;
    final int mapHeight = 12;
    final int mapWidth = 10;
    float centerScore;
    float centerLevel;
    int mapTileSize;

    private int x, y;
    private int currentLevel = 1;
    private int counter;
    private long startingTime, exitingTime;
    private float ratio; // passed time / counter
    public static GameLevel gameLevel;

    public boolean soundOn;
    public boolean in = true;
    private boolean endOfGame;
    private boolean gameInProgress = false;
    private Typeface font;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder holder;
    private Context mContext;
    private Resources mRes;
    private Thread gameThread;
    private Random rd;
    private boolean loading = false;
    private boolean win = false;
    Paint paint;
    private String message;
    public int[][] map;
    public TestCase[] testcase;
    private int stillToPlay;
    public Activity parentActivity;
    public Vector<Point> vect;



}


