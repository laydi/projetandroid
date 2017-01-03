package com.example.aspire.projet1;




        import android.app.Activity;
        import android.net.Uri;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.view.Window;

        import com.google.android.gms.appindexing.Action;
        import com.google.android.gms.appindexing.AppIndex;
        import com.google.android.gms.appindexing.Thing;
        import com.google.android.gms.common.api.GoogleApiClient;

public class
GameActivity extends Activity {
    private GameView gameView;

    private GoogleApiClient client;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);

        gameView = (GameView) findViewById(R.id.GameView);
        Log.e("-> onCreate <-", "before gameView");
        gameView.parentActivity = this;
        gameView.setVisibility(View.VISIBLE);

        gameView.soundOn = getIntent().getExtras().getBoolean("isSoundOn");
        Log.e("-> onCreate <-", "after gameView ");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("-> onPause <-", "onPause");
        gameView.save();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("-> onRestart <-", "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        Log.e("-> onStop <-", "onStop");
        gameView.in = false;
        gameView.setGameThread(null);
        client.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.initParameters();
        Log.e("-> onResume <-", "onRessume");

    }


    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Game Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
