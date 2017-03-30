package com.example.gonghailong.racegame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by gonghailong on 2017/3/22.
 */

public class Welcome extends Activity {
    private Intent intent = new Intent("com.angel.Android.MUSIC");

    private boolean music = true;//depend on whether there are sounds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        startService(intent);
        //set the typeface of Title
        Typeface typeface = Typeface.createFromAsset(getAssets(),"demo.ttf");
        TextView title = (TextView) findViewById(R.id.tvWlc);
        title.setTypeface(typeface);

        Button start = (Button) findViewById(R.id.btnStart);
        Button score = (Button) findViewById(R.id.btnScore);
        Button how = (Button) findViewById(R.id.btnHow);
        Button exit = (Button) findViewById(R.id.btnExit);
        final ImageButton sound = (ImageButton)findViewById(R.id.imgBtn);

        Typeface typeface1 = Typeface.createFromAsset(getAssets(),"demo1.TTF");
        start.setTypeface(typeface1);
        score.setTypeface(typeface1);
        how.setTypeface(typeface1);

        //set the Click Event
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),Game.class);
                intent.putExtra("isMusic",music);
                startActivity(intent);
            }
        });

        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),Score.class);
                startActivity(intent);
            }
        });

        how.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),Howtoplay.class);
                startActivity(intent);
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(music)
                {
                    sound.setImageDrawable(getResources().getDrawable(R.drawable.nosound));
                    stopService(intent);
                    music = false;
                }
                else
                {
                    sound.setImageDrawable(getResources().getDrawable(R.drawable.sound));
                    startService(intent);
                    music = true;
                }
            }
        });

        //set the leave confirmation
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 一个相对复杂的弹出对话框
                 new AlertDialog.Builder(v.getContext())
                         .setTitle("Leave Confirmation")
                        .setIcon(android.R.drawable.ic_dialog_info) // 设置标题图片
                         .setMessage("Are you sure to leave now ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() { // 设置弹框的确认按钮所显示的文本，以及单击按钮后的响应行为
                            @Override
                            public void onClick(DialogInterface a0, int a1) {
                               finish();
                            }
                        })
                         .setNegativeButton("NO", new DialogInterface.OnClickListener() { // 设置弹框的取消按钮所显示的文本，以及单击按钮后的响应行为
                              @Override
                              public void onClick(DialogInterface a0, int a1) {
                              }
                          })
                         // 其他常用方法如下
                         // .setMultiChoiceItems(arg0, arg1, arg2)
                         // .setSingleChoiceItems(arg0, arg1, arg2)
                         // .setNeutralButton(arg0, arg1)
                         .show();
            }
        });
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(Welcome.this,MusicServer.class);
        stopService(intent);
        super.onStop();
        super.finish();
    }
}