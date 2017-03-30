package com.example.gonghailong.racegame.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by gonghailong on 2017/3/23.
 */

public class AutoSprite extends Sprite {
    //每帧移动的像素数,以向下为正
    private float speed = 1;

    public AutoSprite(Bitmap bitmap){
        super(bitmap);
    }

    public void setSpeed(float speed){
        this.speed = speed;
    }

    public float getSpeed(){
        return speed;
    }

    @Override
    protected void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
        if(!isDestroyed()){
            //在y轴方向移动speed像素
            move(0, speed * gameView.getDensity());
        }
    }

    protected void afterDraw(Canvas canvas, Paint paint, GameView gameView){
        if(!isDestroyed()){
            //检查Sprite是否超出了Canvas的范围，如果超出，则销毁Sprite
            RectF canvasRecF = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
            RectF spriteRecF = getRectF();
            if(!RectF.intersects(canvasRecF, spriteRecF)){
                destroy();
            }
        }
    }
}