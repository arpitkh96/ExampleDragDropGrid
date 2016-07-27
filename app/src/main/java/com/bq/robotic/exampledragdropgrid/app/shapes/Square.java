package com.bq.robotic.exampledragdropgrid.app.shapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by arpitkh996 on 20-06-2016.
 */

public class Square extends Shape{
    int side;
    public Square(Context context,String id, int side) {
        super(context,id);
        this.side=side/2;
        p1=new Paint();
        p1.setColor(Color.BLACK);
        p1.setStrokeWidth(5);
        p1.setStyle(Paint.Style.STROKE);
    }

    public void draw(Canvas canvas,Paint paint2) {

        int x=(int)this.x,y=(int)this.y;
        super.draw(canvas,paint2);
        //main table
        Rect rect=new Rect(x-side,y-side,x+side,y+side);
        canvas.drawRect(rect,fill_color);
        drawText(canvas,x,y,paint2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw(canvas,text_paint);
    }

    @Override
    public boolean isTouched(float x1, float y1) {
        if(x1>main_x-side && x1<main_x+side && y1>main_y-side && y1<main_y+side)
            return true;
        return false;
    }
    public int getSide() {
        return side*2;
    }

    public void setSide(int side) {
        this.side = side/2;
    }
    @Override
    public String toString() {
        return "Square selected"+"\t"+cell_Y+"\t"+side;
    }

    @Override
    public int getShapeType() {
        return ShapesType.SHAPE_SQUARE;
    }
}
