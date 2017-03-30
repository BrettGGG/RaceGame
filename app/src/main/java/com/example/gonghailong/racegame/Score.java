package com.example.gonghailong.racegame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gonghailong.racegame.FileHelper;

/**
 * Created by gonghailong on 2017/3/22.
 */

public class Score extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);

        Button back = (Button)findViewById(R.id.btnBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FileHelper fileHelper = new FileHelper(getBaseContext());
        fileHelper.readFromSD("score.txt");
        String[] strs = null;
        String str = null;
        str = fileHelper.readFromSD("score.txt");
        if(str == null) str = "0,0,0,0,0";
        strs = str.split(",");
        TextView t1 = (TextView)findViewById(R.id.tv1);
        TextView t2 = (TextView)findViewById(R.id.tv2);
        TextView t3 = (TextView)findViewById(R.id.tv3);
        TextView t4 = (TextView)findViewById(R.id.tv4);
        TextView t5 = (TextView)findViewById(R.id.tv5);
        t1.setText(strs[0]);
        t2.setText(strs[1]);
        t3.setText(strs[2]);
        t4.setText(strs[3]);
        t5.setText(strs[4]);
    }
}
