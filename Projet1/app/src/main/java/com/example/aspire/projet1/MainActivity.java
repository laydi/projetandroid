package com.example.aspire.projet1;



        import android.app.Activity;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Typeface;
        import android.media.MediaPlayer;
        import android.os.Bundle;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.Window;
        import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initButton();

        SharedPreferences settings = getSharedPreferences("COLORMATCH", 0);
        soundOn = settings.getBoolean("isSoundOn", true);
        changeSoundImage();
        // fonts
        font = Typeface.createFromAsset(this.getAssets(),
                "fonts/OpenSans_Bold.ttf");

        play.setTypeface(font);
        help.setTypeface(font);
        score.setTypeface(font);
    }

    private void changeSoundImage() {
        if (!soundOn)
            sound.setBackgroundResource(R.drawable.soundoff);
    }

    public void initButton() {
        // getting Buttons
       Button play = (Button) findViewById(R.id.playBtn);
        Button help = (Button) findViewById(R.id.helpBtn);
        Button score = (Button) findViewById(R.id.scoreBtn);
        Button sound = (Button) findViewById(R.id.soundBtn);

        // assigning listeners to Buttons
        play.setOnClickListener(this);
        help.setOnClickListener(this);
        score.setOnClickListener(this);
        sound.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent inten;
        switch (v.getId()) {
            case R.id.playBtn:
                inten = new Intent(MainActivity.this, GameView.class);
                inten.putExtra("isSoundOn", soundOn);
                startActivity(inten);
                break;
            case R.id.helpBtn:
                inten = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(inten);
                break;
            case R.id.scoreBtn:
                inten = new Intent(MainActivity.this, ScoreActivity.class);
                startActivity(inten);
                break;
            case R.id.soundBtn:
                if (soundOn) {
                    soundOn = false;
                    sound.setBackgroundResource(R.drawable.soundoff);
                } else {
                    soundOn = true;
                    sound.setBackgroundResource(R.drawable.sound);
                    playSound();

                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences("COLORMATCH", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isSoundOn", soundOn);
        editor.commit();
    }

    private void playSound() {

        try {
         //   mediaPlayer = MediaPlayer.create(this, R.raw.sound);
        } catch (Exception e) {
            mediaPlayer = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.setVolume(6, 6);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }
    }

    private Button play, help, score, sound;
    private boolean soundOn;
    private Typeface font;
    private MediaPlayer mediaPlayer;
}


