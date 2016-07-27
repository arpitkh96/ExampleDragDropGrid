package com.bq.robotic.exampledragdropgrid.app.shapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by arpitkh996 on 20-06-2016.
 */

public abstract class Shape extends ImageView {
    String id,color;
    int capacity = 0;
    boolean dataAdded = false;
    int cell_X = -1, cell_Y = -1;
    Paint p1;
    float main_x=0,main_y=0;
    float x=0,y=0;
    Paint fill_color;
    long startTime=0,timerLength=0;
    boolean isTimerRunning=false;
    Paint text_paint;
    public Shape(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    DisplayMetrics displayMetrics;
    public Shape(Context c, String id) {
        super(c,null);
        this.id = id;
        fill_color=new Paint();
        fill_color.setStyle(Paint.Style.FILL_AND_STROKE);
        fill_color.setStrokeWidth(5);


        text_paint = new Paint();
        text_paint.setColor(Color.BLACK);
        text_paint.setStrokeWidth(2);
        text_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        text_paint.setTextSize(dpToPx(12));
    }

    public int dpToPx(int dp) {
        if (displayMetrics == null) displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof Shape) {
            if (((Shape) o).id == id)
                return true;
        }
        return false;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        x=w/2;
        y=h/2;
    }
    protected void drawText(Canvas canvas, float x1,float y1, Paint paint) {
        long calculatedTime = Math.round((calculateTime() / (double) 60000));
        if (isTimerRunning && calculatedTime >= 0)
            drawText(canvas, x1, y1, paint, calculatedTime + "m");
        else
            drawText(canvas, x1, y1, paint, capacity + "");
    }
    private void drawText(Canvas canvas, float x1,float y1, Paint paint, String text) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        float x = x1- (bounds.width() / 2);
        float y = y1 - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText(text, x, y, paint);
    }
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public int getCell_X() {
        return cell_X;
    }

    public void setCell_X(int cell_X) {
        this.cell_X = cell_X;
    }

    public int getCell_Y() {
        return cell_Y;
    }

    public void setCell_Y(int cell_Y) {
        this.cell_Y = cell_Y;
    }

    public void draw(Canvas canvas, Paint paint2) {
        p1.setStyle(Paint.Style.STROKE);
        if (dataAdded)
            p1.setColor(Color.BLACK);
        else p1.setColor(Color.GRAY);

    }


    public long getStartTime() {
        return startTime;
    }

    public float getMain_x() {
        return main_x;
    }

    public void setMain_x(float main_x) {
        this.main_x = main_x;
    }

    public float getMain_y() {
        return main_y;
    }

    public void setMain_y(float main_y) {
        this.main_y = main_y;
    }

    public void setFill_color(String fill_color) {
        this.color=fill_color;
        this.fill_color.setColor(Color.parseColor(fill_color));
    }


    public long getTimerLength() {
        return timerLength;
    }

    public void setTimerLength(long timerLength) {
        this.timerLength = timerLength;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        isTimerRunning = timerRunning;
    }
    long calculateTime(){
        long i=SystemClock.uptimeMillis()-startTime;
        long finaltime=timerLength-i;
        if(finaltime<=0)isTimerRunning=false;
        return finaltime;
    }
    public String getColor(){
        return color;
    }

    public void setDataAdded(boolean x) {
        dataAdded = x;
    }
    public abstract boolean isTouched(float x, float y);

    public abstract String toString();

    public abstract int getShapeType();

}
