package com.bq.robotic.exampledragdropgrid.app.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by arpitkh996 on 23-06-2016.
 */

public class Cell extends Coordinate{
    int cell_no_x,cell_no_y;
    int width,length;
    boolean occupied=false;


    public int getCell_no_y() {
        return cell_no_y;
    }

    public int getCell_no_x() {
        return cell_no_x;
    }

    public Cell(float x, float y, int width, int length, int cell_no_x, int cell_no_y){
        super(x,y);
        this.x=x;
         this.y=y;
         this.width=width;
         this.length=length;
         this.cell_no_x=cell_no_x;
         this.cell_no_y=cell_no_y;
     }


    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Cell) {
            Cell c=((Cell) o);
            if (c.cell_no_x == cell_no_x && c.cell_no_y==cell_no_y)
                return true;
        }
        return false;

    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public boolean liesInsideCell(float x1, float y1){
        if(x1>=x-width/2 && x1<=x+width/2 && y1>=y-length/2 && y1<=y+length/2)
            return true;
        return false;
    }
    public void drawBorders(Canvas canvas, Paint paint){
        int x=(int)this.x,y=(int)this.y,length=this.length/2,breadth=this.width/2;
        canvas.drawRect(new Rect(x-breadth,y-length,x+breadth,y+length),paint);
        canvas.drawPoint(x,y,paint);
    }
}
