package com.example.gonghailong.racegame.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.List;

/**
 * Created by gonghailong on 2017/3/23.
 */

public class Car extends Sprite {


    public Car(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    protected void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
        if (!isDestroyed()) {
            //确保车完全位于赛道范围内
            validatePosition(canvas, gameView);

        }
    }


    //确保车完全位于赛道范围内
    private void validatePosition(Canvas canvas, GameView gameView) {
        float a = canvas.getWidth() / 2 - 2 * 65 * gameView.getDensity();
        if (getX() <= a) {
            setX(a);
        }
        if (getX() >= a + 3 * 65 * gameView.getDensity()) {
            setX(a + 3 * 65 * gameView.getDensity());
        }
    }


}
