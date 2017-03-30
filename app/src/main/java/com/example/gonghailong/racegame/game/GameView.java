package com.example.gonghailong.racegame.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.gonghailong.racegame.R;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gonghailong on 2017/3/23.
 */

public class GameView extends View {

    private Paint paint;
    private Paint textPaint;
    private float fontSize = 18;
    private float fontSize2 = 20;//用于在Game Over的时候绘制Dialog中的文本
    private float borderSize = 2;//Game Over的Dialog的边框
    private float density = getResources().getDisplayMetrics().density;//屏幕密度
    private long frame = 0;//总共绘制的帧数
    private long score = 0;//总得分
    private Rect continueRect = new Rect();//"继续"、"重新开始"按钮的Rect
    private Car car = null;
    private List<Enemy> enemies = new ArrayList<Enemy>();
    private List<Enemy> enemiesNeedAdded = new ArrayList<Enemy>();
    //0:mycar
    //1:enemy1
    //2:enemy2
    //3:enemy3
    //4:left
    //5:right
    //6:pause1
    //7:pause2
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    public static final int STATUS_GAME_STARTED = 1;//游戏开始
    public static final int STATUS_GAME_PAUSED = 2;//游戏暂停
    public static final int STATUS_GAME_OVER = 3;//游戏结束
    public static final int STATUS_GAME_DESTROYED = 4;//游戏销毁
    private int status = STATUS_GAME_DESTROYED;//初始为销毁状态

    //触摸事件相关的变量
    private static final int TOUCH_MOVE = 1;//移动
    private static final int TOUCH_SINGLE_CLICK = 2;//单击
    private static final int TOUCH_DOUBLE_CLICK = 3;//双击
    //一次单击事件由DOWN和UP两个事件合成，假设从down到up间隔小于200毫秒，我们就认为发生了一次单击事件
    private static final int singleClickDurationTime = 200;
    //一次双击事件由两个点击事件合成，两个单击事件之间小于300毫秒，我们就认为发生了一次双击事件
    private static final int doubleClickDurationTime = 300;
    private long lastSingleClickTime = -1;//上次发生单击的时刻
    private long touchDownTime = -1;//触点按下的时刻
    private long touchUpTime = -1;//触点弹起的时刻
    private float touchX = -1;//触点的x坐标
    private float touchY = -1;//触点的y坐标


    //构造函数
    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.GameView, defStyle, 0);
        a.recycle();
        //初始化paint
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        //设置textPaint，设置为抗锯齿，且是粗体
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        textPaint.setColor(0xff000000);
        fontSize = textPaint.getTextSize();
        fontSize *= density;
        fontSize2 *= density;
        textPaint.setTextSize(fontSize);
        borderSize *= density;
    }

    public void start(int[] bitmapIds){
        destroy();
        for(int bitmapId : bitmapIds){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bitmapId);
            bitmaps.add(bitmap);
        }
        startWhenBitmapsReady();
    }

    private void startWhenBitmapsReady(){
        car = new Car(bitmaps.get(0));
        //将游戏设置为开始状态
        status = STATUS_GAME_STARTED;
        postInvalidate();
    }

    private void restart(){
        destroyNotRecyleBitmaps();
        startWhenBitmapsReady();
    }

    public void pause(){
        //将游戏设置为暂停状态
        status = STATUS_GAME_PAUSED;
    }

    private void resume(){
        //将游戏设置为运行状态
        status = STATUS_GAME_STARTED;
        postInvalidate();
    }


    private long getScore(){
        //获取游戏得分
        return score;
    }
    /*-------------------------------draw-------------------------------------*/


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

         //我们在每一帧都检测是否满足延迟触发单击事件的条件
        if(isSingleClick()){
            onSingleClick(touchX, touchY);
        }
        if(status == STATUS_GAME_STARTED){
            drawGameStarted(canvas);
        }else if(status == STATUS_GAME_PAUSED){
            drawGamePaused(canvas);
        }else if(status == STATUS_GAME_OVER){
            drawGameOver(canvas);
        }
    }

    //绘制运行状态的游戏
    private void drawGameStarted(Canvas canvas){
        drawRaceAndButton(canvas);
        //第一次绘制时，将车移到赛道最左边
        if(frame == 0){
            float leftX = canvas.getWidth() / 2 - 2 * 65 *density;
            float leftY = canvas.getHeight() - car.getHeight()*2 - 10;
            car.moveTo(leftX,leftY);//小调整10
        }
        //将spritesNeedAdded添加到sprites中
        if(enemiesNeedAdded.size() > 0){
            enemies.addAll(enemiesNeedAdded);
            enemiesNeedAdded.clear();
        }

        //每隔50帧随机添加Sprite
        if(frame % 50 == 0){
            createRandomSprites(canvas);
        }
        frame++;

        //遍历sprites，绘制敌车
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()){
            Enemy s = iterator.next();
            if(!s.isDestroyed()){
                //在Sprite的draw方法内有可能会调用destroy方法
                s.draw(canvas, paint, this);
            }
            //我们此处要判断Sprite在执行了draw方法后是否被destroy掉了
            if(s.isDestroyed()){
                //如果Sprite被销毁了，那么从Sprites中将其移除
                iterator.remove();
            }
            //超过后得分
            if(s.getY() >= canvas.getHeight() - car.getHeight() - 10)
            {
                addScore(s.getValue());
                drawScore(canvas,s.getValue());
                s.destroy();
            }

            Point p = car.getCollidePointWithOther(s);
            if(p != null) {
                //p为战斗机与敌机的碰撞点，如果p不为null，则表明战斗机被敌机击中
                //如果碰撞，那么游戏结束
                status = STATUS_GAME_OVER;
                saveScore(getScore());
                break;
            }
        }

        if(car != null){
            //最后绘制车
            car.draw(canvas, paint, this);
            //通过调用postInvalidate()方法使得View持续渲染，实现动态效果
            postInvalidate();
        }
    }

    //绘制暂停状态的游戏
    private void drawGamePaused(Canvas canvas){
        drawRaceAndButton(canvas);

        //调用Sprite的onDraw方法，而非draw方法，这样就能渲染静态的Sprite，而不让Sprite改变位置
        for(Enemy s : enemies){
            s.onDraw(canvas, paint, this);
        }
        if(car != null){
            car.onDraw(canvas, paint, this);
        }
        //绘制Dialog，显示得分
        drawScoreDialog(canvas, "Continue");
    }

    //绘制结束状态的游戏
    private void drawGameOver(Canvas canvas){
        //Game Over之后只绘制弹出窗显示最终得分
        drawScoreDialog(canvas, "Restart");
        if(lastSingleClickTime > 0){
            postInvalidate();
        }
    }
    //绘制提示对话框
    private void drawScoreDialog(Canvas canvas, String operation){
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        //存储原始值
        float originalFontSize = textPaint.getTextSize();
        Paint.Align originalFontAlign = textPaint.getTextAlign();
        int originalColor = paint.getColor();
        Paint.Style originalStyle = paint.getStyle();
        /*
        W = 360
        w1 = 20
        w2 = 320
        buttonWidth = 140
        buttonHeight = 42
        H = 558
        h1 = 150
        h2 = 60
        h3 = 124
        h4 = 76
        */
        int w1 = (int)(20.0 / 360.0 * canvasWidth);
        int w2 = canvasWidth - 2 * w1;
        int buttonWidth = (int)(140.0 / 360.0 * canvasWidth);

        int h1 = (int)(150.0 / 558.0 * canvasHeight);
        int h2 = (int)(60.0 / 558.0 * canvasHeight);
        int h3 = (int)(124.0 / 558.0 * canvasHeight);
        int h4 = (int)(76.0 / 558.0 * canvasHeight);
        int buttonHeight = (int)(42.0 / 558.0 * canvasHeight);

        canvas.translate(w1, h1);
        //绘制背景色
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFD7DDDE);
        Rect rect1 = new Rect(0, 0, w2, canvasHeight - 2 * h1);
        canvas.drawRect(rect1, paint);
        //绘制边框
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFF515151);
        paint.setStrokeWidth(borderSize);
        //paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        canvas.drawRect(rect1, paint);
        //绘制文本"分数"
        textPaint.setTextSize(fontSize2);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Your Score :", w2 / 2, (h2 - fontSize2) / 2 + fontSize2, textPaint);
        //绘制"分数"下面的横线
        canvas.translate(0, h2);
        canvas.drawLine(0, 0, w2, 0, paint);
        //绘制实际的分数
        String allScore = String.valueOf(getScore());
        canvas.drawText(allScore, w2 / 2, (h3 - fontSize2) / 2 + fontSize2, textPaint);
        //绘制分数下面的横线
        canvas.translate(0, h3);
        canvas.drawLine(0, 0, w2, 0, paint);
        //绘制按钮边框
        Rect rect2 = new Rect();
        rect2.left = (w2 - buttonWidth) / 2;
        rect2.right = w2 - rect2.left;
        rect2.top = (h4 - buttonHeight) / 2;
        rect2.bottom = h4 - rect2.top;
        canvas.drawRect(rect2, paint);
        //绘制文本"继续"或"重新开始"
        canvas.translate(0, rect2.top);
        canvas.drawText(operation, w2 / 2, (buttonHeight - fontSize2) / 2 + fontSize2, textPaint);
        continueRect = new Rect(rect2);
        continueRect.left = w1 + rect2.left;
        continueRect.right = continueRect.left + buttonWidth;
        continueRect.top = h1 + h2 + h3 + rect2.top;
        continueRect.bottom = continueRect.top + buttonHeight;

        //重置
        textPaint.setTextSize(originalFontSize);
        textPaint.setTextAlign(originalFontAlign);
        paint.setColor(originalColor);
        paint.setStyle(originalStyle);
    }

    //绘制得分提示
    private void drawScore(Canvas canvas,int value)
    {
        int originalColor = textPaint.getColor();
        float originalFontSize = textPaint.getTextSize();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(fontSize*2);
        canvas.drawText(value+"",getWidth()/2,getHeight()/2,textPaint);
        textPaint.setColor(originalColor);
        textPaint.setTextSize(originalFontSize);
    }

    //绘制左上角的得分、右上角按钮、左右键和赛道
    private void drawRaceAndButton(Canvas canvas){
        //绘制右上角的暂停按钮
        Bitmap pauseBitmap = status == STATUS_GAME_STARTED ? bitmaps.get(6) : bitmaps.get(7);
        RectF pauseBitmapDstRecF = getPauseBitmapDstRecF();
        float pauseLeft = pauseBitmapDstRecF.left;
        float pauseTop = pauseBitmapDstRecF.top;
        canvas.drawBitmap(pauseBitmap, pauseLeft, pauseTop, paint);
        //绘制左上角的总得分数
        float scoreLeft =  10 * density;
        float scoreTop = fontSize + pauseTop + pauseBitmap.getHeight() / 2 - fontSize / 2;
        //存储原始值
        float originalFontSize = textPaint.getTextSize();
        Paint.Align originalFontAlign = textPaint.getTextAlign();
        int originalColor = textPaint.getColor();
        Typeface type = Typeface.SANS_SERIF;
        textPaint.setTypeface(type);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(fontSize*2);
        canvas.drawText("Score :" + score + "", scoreLeft, scoreTop, textPaint);
        //绘制左键
        Bitmap leftBitmap = isClickLeft(touchX,touchY)?bitmaps.get(4):bitmaps.get(9);
        RectF leftBitmapDstRecF = getLeftBitmapDstRecF();
        float leftLeft = leftBitmapDstRecF.left;
        float leftTop = leftBitmapDstRecF.top;
        canvas.drawBitmap(leftBitmap, leftLeft, leftTop, paint);
        //绘制右键
        Bitmap rightBitmap = isClickRight(touchX,touchY)?bitmaps.get(5):bitmaps.get(10);
        RectF rightBitmapDstRecF = getRightBitmapDstRecF();
        float rightLeft = rightBitmapDstRecF.left;
        float rightTop = rightBitmapDstRecF.top;
        canvas.drawBitmap(rightBitmap, rightLeft, rightTop, paint);
        //绘制赛道
        Bitmap raceBitmap = bitmaps.get(8);
        float raceLeft = getWidth()/2 - raceBitmap.getWidth()/2;
        float raceTop = getHeight() - 20*density -leftBitmap.getHeight()/2-raceBitmap.getHeight();
        canvas.drawBitmap(raceBitmap, raceLeft, raceTop, paint);

        //重置
        textPaint.setTextSize(originalFontSize);
        textPaint.setColor(originalColor);
    }

    private RectF getPauseBitmapDstRecF(){
        Bitmap pauseBitmap = status == STATUS_GAME_STARTED ? bitmaps.get(6) : bitmaps.get(7);
        RectF recF = new RectF();
        recF.left = getWidth()-pauseBitmap.getWidth()-20 * density;
        recF.top = 20 * density;
        recF.right = recF.left + pauseBitmap.getWidth();
        recF.bottom = recF.top + pauseBitmap.getHeight();
        return recF;
    }

    private RectF getLeftBitmapDstRecF(){
        Bitmap leftBitmap = bitmaps.get(4);
        RectF recF = new RectF();
        recF.left = 20*density;
        recF.top = getHeight() - leftBitmap.getHeight()-20 * density;
        recF.right = recF.left + leftBitmap.getWidth();
        recF.bottom = recF.top + leftBitmap.getHeight();
        return recF;
    }

    private RectF getRightBitmapDstRecF(){
        Bitmap rightBitmap = bitmaps.get(5);
        RectF recF = new RectF();
        recF.left = getWidth()- rightBitmap.getWidth() - 20*density;
        recF.top = getHeight() - rightBitmap.getHeight()-20 * density;
        recF.right = recF.left + rightBitmap.getWidth();
        recF.bottom = recF.top + rightBitmap.getHeight();
        return recF;
    }

    //生成随机的enemy
    private void createRandomSprites(Canvas canvas){
        Enemy enemy = null;
        int speed = 2;
        int[] nums = {1,1,1,3,1,2,1,1,2,1,1,1,1,2,3,2,2,3,1,3};
        int index = (int)Math.floor(nums.length*Math.random());
        int type = nums[index];
        if(type == 1){
            //绿车
            enemy = new Enemy(bitmaps.get(3));
            enemy.setValue(10);
            if(Math.random() < 0.66){
                speed = 4;
                enemy.setValue(40);
            }

        }
        else if(type == 2){
            //蓝车
            enemy = new Enemy(bitmaps.get(2));
            enemy.setValue(20);
        }
        else if(type == 3){
            //黄车
            enemy = new Enemy(bitmaps.get(1));
            enemy.setValue(30);
            if(Math.random() < 0.33){
                speed = 4;
                enemy.setValue(50);
            }
        }

        if(enemy != null){
            float spriteWidth = enemy.getWidth();
            float spriteHeight = enemy.getHeight();
            float x = ((index%4)*65+200)* density;
            float y = 50*density-spriteHeight;
            enemy.setX(x);
            enemy.setY(y);
            if(enemy instanceof AutoSprite){
                AutoSprite autoSprite = (AutoSprite)enemy;
                autoSprite.setSpeed(speed);
            }

            enemiesNeedAdded.add(enemy);
        }
    }

    /*-------------------------------Touch------------------------------------*/

    @Override
    public boolean onTouchEvent(MotionEvent event){
        //通过调用resolveTouchType方法，得到我们想要的事件类型
        //需要注意的是resolveTouchType方法不会返回TOUCH_SINGLE_CLICK类型
        //我们会在onDraw方法每次执行的时候，都会调用isSingleClick方法检测是否触发了单击事件
        int touchType = resolveTouchType(event);
        if(status == STATUS_GAME_STARTED){
            if(touchType == TOUCH_MOVE) {
                if (isClickLeft(touchX, touchY)) {
                    if (car != null) {
                        car.move(-5, 0);
                    }
                }
                if (isClickRight(touchX, touchY)) {
                    if (car != null) {
                        car.move(5, 0);
                    }
                }
            }
            else if (event.getAction()== MotionEvent.ACTION_DOWN)
            {
                if (isClickLeft(touchX,touchY))
                {
                    if (car != null) {
                        car.move(-10, 0);
                    }
                }
                if (isClickRight(touchX,touchY))
                {
                    if (car != null) {
                        car.move(10, 0);
                    }
                }
            }
            else if(touchType == TOUCH_DOUBLE_CLICK){
                if(status == STATUS_GAME_STARTED){
                    if(!isClickLeft(touchX,touchY)&&!isClickRight(touchX,touchY))
                    pause();
                }
            }
        }
        else if(status == STATUS_GAME_PAUSED){
            if(lastSingleClickTime > 0){
                postInvalidate();
            }
        }
        else if(status == STATUS_GAME_OVER){
            if(lastSingleClickTime > 0){
                postInvalidate();
            }
        }
        return true;
    }

    //合成我们想要的事件类型
    private int resolveTouchType(MotionEvent event){
        int touchType = -1;
        int action = event.getAction();
        touchX = event.getX();
        touchY = event.getY();
        if(action == MotionEvent.ACTION_MOVE){
            long deltaTime = System.currentTimeMillis() - touchDownTime;
            if(deltaTime > singleClickDurationTime){
                //触点移动
                touchType = TOUCH_MOVE;
            }
        }else if(action == MotionEvent.ACTION_DOWN){
            //触点按下
            touchDownTime = System.currentTimeMillis();
        }else if(action == MotionEvent.ACTION_UP){
            //触点弹起
            touchUpTime = System.currentTimeMillis();
            //计算触点按下到触点弹起之间的时间差
            long downUpDurationTime = touchUpTime - touchDownTime;
            //如果此次触点按下和抬起之间的时间差小于一次单击事件指定的时间差，
            //那么我们就认为发生了一次单击
            if(downUpDurationTime <= singleClickDurationTime){
                //计算这次单击距离上次单击的时间差
                long twoClickDurationTime = touchUpTime - lastSingleClickTime;

                if(twoClickDurationTime <=  doubleClickDurationTime){
                    //如果两次单击的时间差小于一次双击事件执行的时间差，
                    //那么我们就认为发生了一次双击事件
                    touchType = TOUCH_DOUBLE_CLICK;
                    //重置变量
                    lastSingleClickTime = -1;
                    touchDownTime = -1;
                    touchUpTime = -1;
                }else{
                    //如果这次形成了单击事件，但是没有形成双击事件，那么我们暂不触发此次形成的单击事件
                    //我们应该在doubleClickDurationTime毫秒后看一下有没有再次形成第二个单击事件
                    //如果那时形成了第二个单击事件，那么我们就与此次的单击事件合成一次双击事件
                    //否则在doubleClickDurationTime毫秒后触发此次的单击事件
                    lastSingleClickTime = touchUpTime;
                }
            }
        }
        return touchType;
    }

    //是否单击了右上角的暂停按钮
    private boolean isClickPause(float x, float y){
        RectF pauseRecF = getPauseBitmapDstRecF();
        return pauseRecF.contains(x, y);
    }

    //是否单击了暂停状态下的“继续”按钮
    private boolean isClickContinueButton(float x, float y){
        return continueRect.contains((int)x, (int)y);
    }

    //是否单击了GAME OVER状态下的“重新开始”按钮
    private boolean isClickRestartButton(float x, float y){
        return continueRect.contains((int)x, (int) y);
    }


    //是否单击了左键
    private boolean isClickLeft(float x, float y){
        RectF leftRecF = getLeftBitmapDstRecF();
        return leftRecF.contains(x, y);
    }

    //是否单击了右键
    private boolean isClickRight(float x, float y){
        RectF rightRecF = getRightBitmapDstRecF();
        return rightRecF.contains(x, y);
    }

    //在onDraw方法中调用该方法，在每一帧都检查是不是发生了单击事件
    private boolean isSingleClick(){
        boolean singleClick = false;
        //我们检查一下是不是上次的单击事件在经过了doubleClickDurationTime毫秒后满足触发单击事件的条件
        if(lastSingleClickTime > 0){
            //计算当前时刻距离上次发生单击事件的时间差
            long deltaTime = System.currentTimeMillis() - lastSingleClickTime;

            if(deltaTime >= doubleClickDurationTime){
                //如果时间差超过了一次双击事件所需要的时间差，
                //那么就在此刻延迟触发之前本该发生的单击事件
                singleClick = true;
                //重置变量
                lastSingleClickTime = -1;
                touchDownTime = -1;
                touchUpTime = -1;
            }
        }
        return singleClick;
    }

    private void onSingleClick(float x, float y){
        if(status == STATUS_GAME_STARTED){
            if(isClickPause(x, y)){
                //单击了暂停按钮
                pause();
            }
        }else if(status == STATUS_GAME_PAUSED){
            if(isClickContinueButton(x, y)){
                //单击了“继续”按钮
                resume();
            }
        }else if(status == STATUS_GAME_OVER){
            if(isClickRestartButton(x, y)){
                //单击了“重新开始”按钮
                restart();
            }
        }
    }



     /*-------------------------------destroy------------------------------------*/

    private void destroyNotRecyleBitmaps(){
        //将游戏设置为销毁状态
        status = STATUS_GAME_DESTROYED;

        //重置frame
        frame = 0;

        //重置得分
        score = 0;

        //销毁战斗机
        if(car != null){
            car.destroy();
        }
        car = null;

        //销毁敌机、子弹、奖励、爆炸
        for(Enemy s : enemies){
            s.destroy();
        }
        enemies.clear();
    }

    public void destroy(){
        destroyNotRecyleBitmaps();

        //释放Bitmap资源
        for(Bitmap bitmap : bitmaps){
            bitmap.recycle();
        }
        bitmaps.clear();
    }



    //-----------------------------public method
    public float getDensity(){
        return density;
    }

    //添加得分
    public void addScore(int value){
        score += value;
    }


    //获取处于活动状态的敌车
    public List<Enemy> getAliveEnemys(){
        List<Enemy> enemys = new ArrayList<Enemy>();
        for(Enemy s : enemys){
            if(!s.isDestroyed() && s instanceof Enemy){
                Enemy sprite = (Enemy)s;
                enemys.add(sprite);
            }
        }
        return enemys;
    }

    //Save the score
    public void saveScore(long score) {
        FileHelper fileHelper = new FileHelper(getContext());
        String[] strs = null;
        String str = null;
        str = fileHelper.readFromSD("score.txt");
        if(str == null) str = "0,0,0,0,0";
        str = score+ "," + str;
        strs = str.split(",");
        if(!str.isEmpty()) {

            Long[] longs = new Long[strs.length];

            for(int i=0;i<longs.length;i++) {//将字符串数组转化为整数数组
                longs[i] =Long.valueOf(strs[i]);
            }

            for(int i=0;i<longs.length-1;i++)//冒泡排序
            {
                for(int j =longs.length-1;j>i;j--)
                {
                    if(longs[j]>longs[j-1]){
                        long temp = 0;
                        temp = longs[j];
                        longs[j] = longs[j-1];
                        longs[j-1]=temp;
                    }
                }
            }

            for(int i=0;i<longs.length;i++) {//将整数数组转化为字符串数组
                strs[i]=longs[i].toString();
            }
        }
        str = "";
        for(int i=0;i<strs.length-1;i++) {//将数组转化为字符串
            str +=strs[i]+",";
        }
        fileHelper.saveToSDCard("score.txt",str);
    }
}
