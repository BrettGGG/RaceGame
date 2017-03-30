package com.example.gonghailong.racegame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private boolean flag = true;   //set hint visible
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the typeface of Title
        Typeface typeface = Typeface.createFromAsset(getAssets(),"demo.ttf");
        TextView title = (TextView) findViewById(R.id.tvGame);
        title.setTypeface(typeface);

        //Set the spark Text
        spark();

        //Set the title animation
        AnimationSet animation = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(4000);
        animation.addAnimation(rotate);

        ScaleAnimation scale = new ScaleAnimation(1,2,1,2, Animation.RELATIVE_TO_PARENT,
                Animation.RELATIVE_TO_PARENT);
        scale.setDuration(4000);
        animation.addAnimation(scale);

        title.startAnimation(animation);
    }

    //Set the "Touch screen to continue" to spark
    public void spark() {
        final TextView touchScreen = (TextView)findViewById(R.id.tv1);
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (flag) {
                            flag = false;
                            touchScreen.setTextColor(Color.TRANSPARENT);
                        }
                        else{
                            flag = true;
                            touchScreen.setTextColor(Color.RED);
                        }
                    }
                });
            }
        };
        timer.schedule(task,1,1000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction ()==MotionEvent.ACTION_DOWN)
        {
            Intent intent=new Intent(this,Welcome.class);
            startActivity(intent);
            finish();
            return true;
        }
        else return false;
    }

}
