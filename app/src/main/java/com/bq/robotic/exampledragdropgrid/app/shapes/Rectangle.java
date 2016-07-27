package com.bq.robotic.exampledragdropgrid.app.shapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by arpitkh996 on 20-06-2016.
 */

public class Rectangle extends Shape {
    int length, breadth;

    public Rectangle(Context c,String id,int length, int breadth) {
        super(c, id);
        this.length = length;
        this.breadth = breadth;
        p1 = new Paint();
        p1.setColor(Color.BLACK);
        p1.setStrokeWidth(5);
        p1.setStyle(Paint.Style.STROKE);
    }


    public int getLength() {
        return length;
    }

    public int getBreadth() {
        return breadth;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setBreadth(int breadth) {
        this.breadth = breadth;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        System.out.print(id+" "+gainFocus);
    }

    public void draw(Canvas canvas, Paint paint2) {
        super.draw(canvas, paint2);
        canvas.drawARGB(255,150,100,170);
        drawText(canvas,x,y,paint2);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw(canvas,text_paint);
    }

    @Override
    public boolean isTouched(float x1, float y1) {
        if (x1 > main_x - length / 2 && x1 < main_x + length / 2 && y1 > main_y - breadth / 2 && y1 < main_y + breadth / 2)
            return true;
        return false;
    }


    public void rotate() {
        int x = length;
        length = breadth;
        breadth = x;
    }

    @Override
    public String toString() {
        return "Rectangle selected" + "\t" + "\t" +  "\t" + cell_X + "\t" + cell_Y + "\t" + length + "\t" + breadth;
    }

    @Override
    public int getShapeType() {
        return ShapesType.SHAPE_RECTANGLE;
    }
}
