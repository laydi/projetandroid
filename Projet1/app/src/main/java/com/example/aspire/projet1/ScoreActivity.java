package com.example.aspire.projet1;

        import android.app.Activity;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.view.Window;
        import android.widget.TextView;

public class ScoreActivity extends Activity {
    private TextView[] tv;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_score);
        context = this;
        initTextView();
        updateText();
    }

    private void initTextView() {
        tv = new TextView[1];
        tv[0] = (TextView) findViewById(R.id.scoreone);


    }

    private void updateText() {

        for (short i = 1; i <= 1; i++) {
            if (context != null) {
                SharedPreferences settings = context.getSharedPreferences(
                        "COLORMATCH", 0);
                int tmp = settings.getInt("SCORE" + i, 0);
                tv[i - 1].setText(tmp + "");
            }

        }

    }

}

