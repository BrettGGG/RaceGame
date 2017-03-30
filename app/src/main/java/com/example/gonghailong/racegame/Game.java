package com.example.gonghailong.racegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.gonghailong.racegame.game.GameView;

import java.io.File;

/**
 * Created by gonghailong on 2017/3/22.
 */

public class Game extends Activity {

    private GameView gameView;
    private Intent intent = new Intent("com.angel.Android.MUSIC");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        Intent i = getIntent();

        if(i.getBooleanExtra("isMusic",true)) {
            startService(intent);
        }
        gameView = (GameView) findViewById(R.id.gameView);
        //0:mycar
        //1:enemy1
        //2:enemy2
        //3:enemy3
        //4:left
        //5:right
        //6:pause1
        //7:pause2
        //8:race
        //9:left2
        //10:right2


        int[] bitmapIds = {
                R.drawable.mycar,
                R.drawable.enemy1,
                R.drawable.enemy2,
                R.drawable.enemy3,
                R.drawable.left,
                R.drawable.right,
                R.drawable.pause1,
                R.drawable.pause2,
                R.drawable.race,
                R.drawable.left2,
                R.drawable.right2,
        };
        gameView.start(bitmapIds);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameView != null) {
            gameView.destroy();
        }
        gameView = null;
    }
}
