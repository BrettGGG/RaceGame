package com.example.gonghailong.racegame.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by gonghailong on 2017/3/23.
 */

public class Enemy extends AutoSprite {

    private int value = 0;//被超得分

    public Enemy(Bitmap bitmap){
        super(bitmap);
    }

    public void setValue(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

}
