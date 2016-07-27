package com.bq.robotic.exampledragdropgrid.app.shapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by arpitkh996 on 20-06-2016.
 */

public class Circle extends Shape {
    int radius;
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Circle(Context c,String id, int radius) {
        super(c,id);
        this.radius=radius;
        p1=new Paint();
        p1.setColor(Color.BLACK);
        p1.setStrokeWidth(5);
        p1.setStyle(Paint.Style.STROKE);
    }


    public void draw(Canvas canvas,Paint paint2) {
        super.draw(canvas,paint2);
       canvas.drawCircle(x,y,radius,fill_color);
        drawText(canvas,x,y,paint2);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw(canvas,text_paint);
    }

    @Override
    public boolean isTouched(float x1, float y1) {
        if(x1>x-radius && x1<x+radius && y1>y-radius && y1<y+radius)
            return true;
        return false;
    }
    @Override
    public String toString() {
        return "Circle selected"+"\t"+"\t"+cell_X+"\t"+cell_Y+"\t"+radius;
    }

    @Override
    public int getShapeType() {
        return ShapesType.SHAPE_CIRCLE;
    }

}
