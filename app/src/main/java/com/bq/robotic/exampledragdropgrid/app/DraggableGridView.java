/*
* This file is part of the drag_drop_grid library
*
* Copyright (C) 2013 Mundo Reader S.L.
*
* Date: April 2014
* Author: Estefanía Sarasola Elvira <estefania.sarasola@bq.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/

package com.bq.robotic.exampledragdropgrid.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.Toast;


import com.bq.robotic.exampledragdropgrid.app.shapes.Cell;
import com.bq.robotic.exampledragdropgrid.app.shapes.Circle;
import com.bq.robotic.exampledragdropgrid.app.shapes.Rectangle;
import com.bq.robotic.exampledragdropgrid.app.shapes.Shape;
import com.bq.robotic.exampledragdropgrid.app.shapes.ShapesType;
import com.bq.robotic.exampledragdropgrid.app.shapes.Square;

import java.util.ArrayList;
import java.util.Collections;


public class DraggableGridView extends ViewGroup implements View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {
    // Layout vars
    protected int columnCount, scroll = 0;
    protected int numberOfColumns = 0; // if want to set a fixed value for the columns
    protected float lastDelta = 0;
    protected Handler handler = new Handler();
    protected Float fixedChildrenWidth = null;
    protected Float fixedChildrenHeight = null;

    protected boolean centerChildrenInGrid = false;

    // Dragging
    protected int lastX = -1, lastY = -1, lastTarget = -1;
    protected boolean enabled = true, touching = false;
    Shape dragged;
    // Animation
    public static int animT = 150;
    protected ArrayList<Integer> newPositions = new ArrayList<Integer>();

    // Listeners
    protected OnRearrangeListener onRearrangeListener;
    //	protected OnClickListener secondaryOnClickListener;
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    // Context
    Context context;

    // Delete zone
    protected DeleteDropZoneView deleteZone;
    protected boolean draggedInDeleteZone = false;
    Paint p;
    // Manage child sizes and padding
    protected int biggestChildWidth, biggestChildHeight;
    protected float screenWidth;

    // Debugging
    private static final String LOG_TAG = "DraggableGridView";

    /***********************************************************************************************
     *                                      CONSTRUCTORS                                           *
     **********************************************************************************************/

    /**
     * XML constructors
     */
    public DraggableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        setListeners();
        setWillNotDraw(false);
        handler.removeCallbacks(updateTask);
        handler.postAtTime(updateTask, SystemClock.uptimeMillis() + 500);
        setChildrenDrawingOrderEnabled(true);
        p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(5);
        p.setStyle(Paint.Style.STROKE);
    }


    public DraggableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(5);
        p.setStyle(Paint.Style.STROKE);
        this.context = context;
        setListeners();
        setWillNotDraw(false);
        handler.removeCallbacks(updateTask);
        handler.postAtTime(updateTask, SystemClock.uptimeMillis() + 500);
        setChildrenDrawingOrderEnabled(true);

    }


    /**
     * Programmatically constructor
     */
    public DraggableGridView(Context context) {
        super(context);

        this.context = context;
        setListeners();
        setWillNotDraw(false);
        handler.removeCallbacks(updateTask);
        handler.postAtTime(updateTask, SystemClock.uptimeMillis() + 500);
        setChildrenDrawingOrderEnabled(true);
    }


    /**
     * Task for the scrolling and repaint the layout
     */
    protected Runnable updateTask = new Runnable() {
        public void run() {


            requestLayout();

            handler.postDelayed(this, 25);
        }
    };


    /***********************************************************************************************
     *                                   GETTERS AND SETTERS                                       *
     **********************************************************************************************/

    /**
     * Set a fixed number of columns for the grid, if there is enough space available
     *
     * @param numberOfColumns desired number of columns
     */
    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }


    /**
     * Get the fixed number of columns set if there is space enough for them
     *
     * @return the fixed number of columns
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }


    /**
     * Checks if it is set that the children of the grid must be centered
     *
     * @return if the children are centered in teh grid or not
     */
    public boolean isCenterChildrenInGrid() {
        return centerChildrenInGrid;
    }


    /**
     * Set if the children must be centered in the grid or not
     *
     * @param centerChildrenInGrid set if the children are centered in teh grid or not
     */
    public void setCenterChildrenInGrid(boolean centerChildrenInGrid) {
        this.centerChildrenInGrid = centerChildrenInGrid;
    }


    /**
     * All children have this width
     *
     * @return children's width
     */
    public float getFixedChildrenWidth() {
        return fixedChildrenWidth;
    }


    /**
     * Give a fixed width to all the children
     *
     * @param fixedChildrenWidth children's width
     */
    public void setFixedChildrenWidth(float fixedChildrenWidth) {
        this.fixedChildrenWidth = fixedChildrenWidth;
    }


    /**
     * All children have this height
     *
     * @return children's height
     */
    public float getFixedChildrenHeight() {
        return fixedChildrenHeight;
    }


    /**
     * Give a fixed height to all the children
     *
     * @param fixedChildrenHeight children's height
     */
    public void setFixedChildrenHeight(float fixedChildrenHeight) {
        this.fixedChildrenHeight = fixedChildrenHeight;
    }


    /***********************************************************************************************
     *                                       MANAGE CHILDREN                                       *
     **********************************************************************************************/

    /**
     * And a new child
     *
     * @param child a new child
     */
    @Override
    public void addView(View child) {
        if(child instanceof Shape){
            Shape shape=(Shape)child;
            newPositions.add(-1);
            Cell cell = findFirstEmptyCell();
            if(cell==null)return;
            cell.setOccupied(true);
            shape.setCell_X(cell.getCell_no_x());
            shape.setCell_Y(cell.getCell_no_y());
            super.addView(shape);
            if (deleteZone != null) {
                deleteZone.bringToFront();
            }
        }
    }

    int side = 80;
    private int w, h;
    int cell_width, cell_height;
    final int COLUMN_COUNT = 6, ROW_COUNT = 6;
    ArrayList<ArrayList<Cell>> cells = new ArrayList<>();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        cell_width = w / COLUMN_COUNT;
        cell_height = h / ROW_COUNT;
        side = cell_width / 2;
        generateCellLayout();
    }

    void generateCellLayout() {
        if (cells.size() == ROW_COUNT) {
            int y = 0, x = 0;
            for (int i = 1; i <= ROW_COUNT; i++) {
                if (i == 1) y = cell_height / 2;
                else y = y + cell_height;
                for (int j = 1; j <= COLUMN_COUNT; j++) {
                    if (j == 1) x = cell_width / 2;
                    else x = x + cell_width;
                    //values subtracted by 1 as loop starts from 1
                    Cell cell = cells.get(i - 1).get(j - 1);
                    cell.setX(x);
                    cell.setY(y);
                }
            }
        } else {
            cells.clear();
            int y = 0, x = 0;
            for (int i = 1; i <= ROW_COUNT; i++) {
                ArrayList<Cell> arrayList = new ArrayList<>();
                ;
                if (i == 1) y = cell_height / 2;
                else y = y + cell_height;
                for (int j = 1; j <= COLUMN_COUNT; j++) {
                    if (j == 1) x = cell_width / 2;
                    else x = x + cell_width;
                    Cell cell = new Cell(x, y, cell_width, cell_height, i, j);
                    arrayList.add(cell);
                }
                cells.add(arrayList);
            }
            invalidate();
        }
    }

    /**
     * Remove a child
     *
     * @param index the index of the child to remove
     */
    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        newPositions.remove(index);
    }


    /**
     * Remove all children
     */
    public void removeAll() {
        super.removeAllViews();
        newPositions.clear();
        invalidate();
    }


    /***********************************************************************************************
     *                                  MANAGE CHILDREN POSITIONS                                  *
     **********************************************************************************************/

    /**
     * Ask all children to measure themselves and compute the measurement of this
     * layout based on the children.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);


        if (deleteZone != null) {
//            measureChild(deleteZone, MeasureSpec.makeMeasureSpec(display.getWidth(), MeasureSpec.EXACTLY),
//                    MeasureSpec.makeMeasureSpec(getPixelFromDip(40), MeasureSpec.EXACTLY));

            measureChild(deleteZone, widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(widthSize,heightSize);
    }


    /**
     * Manage where to position the children (the ImageViews) in the grid layout.
     * We find out how many columns are needed to arrange the children's views, attending to the
     * biggest padding of all the children an using it for all children, in order to have the same
     * number of columns in all rows.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

//        Log.e(LOG_TAG, "screen dpi: " + (right -left));


        // Request layout its children at specific positions
        for (int i = 0; i < getChildCount(); i++) {
            Shape shape=(Shape)getChildAt(i);
            if (shape != dragged) {
                Cell cell = getCellByNumber(shape.getCell_X(),shape.getCell_Y());
                if (cell != null) {
                    switch (shape.getShapeType()){
                        case ShapesType.SHAPE_RECTANGLE:
                            int x = (int) cell.getX(), y = (int) cell.getY(), length = cell.getLength()/2, breadth = cell.getWidth()/2;
                           getChildAt(i).layout(x - breadth+5, y - length+5, x + breadth-5, y + length-5);
                            break;
                        case ShapesType.SHAPE_CIRCLE:
                            break;
                        case ShapesType.SHAPE_SQUARE:
                            break;
                    }
                }
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(ArrayList<Cell> arrayList:cells){
            for(Cell cell:arrayList){
                cell.drawBorders(canvas,p);
            }
        }
    }

    void addRectangle(Context context, String id){
        Rectangle rectangle=new Rectangle(context ,id,side + side / 4, side);
        rectangle.setFill_color("#314e56");
        addView(rectangle);
    }
    void addSquare(Context context, String id){
        Square rectangle=new Square(context ,id, side);
        rectangle.setFill_color("#314e56");
        addView(rectangle);
    }
    void addCircle(Context context, String id){
        Circle rectangle=new Circle(context ,id, side);
        rectangle.setFill_color("#314e56");
        addView(rectangle);
    }
    Cell getCellByNumber(int i, int j) {
        for (ArrayList<Cell> arrayList : cells) {
            for (Cell cell : arrayList)
            {
                if (cell.getCell_no_x() == i && cell.getCell_no_y() == j)
                    return cell;
            }
        }
        return null;
    }
    Cell findFirstEmptyCell() {
        for (ArrayList<Cell> arrayList : cells)
            for (Cell cell : arrayList)
                if (!cell.isOccupied())
                    return cell;
        return null;
    }



    /**
     * Reorder children
     */
    protected void reorderChildren() {
    }


    /***********************************************************************************************
     *                     SEARCH FOR CHILDREN AND COORDINATES METHODS                             *
     **********************************************************************************************/

    /**
     * Gets the index of the child given by the last position of the finger or mouse in the screen
     *
     * @return the index of the child that the user pressed lastly
     */
  /*  public int getLastIndex() {
        return getIndexFromCoor(lastX, lastY);
    }
*/

    /**
     * Gets the index of this child depending on its coordinates in the layout
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return the index of the view
     */

    /**
     * Get the coordinates positions of a child given the index of it
     *
     * @param index of teh child
     * @return the coordinates of the top left point of the child view
     */
    protected Point getCoorFromIndex(int index) {
        int col = index % columnCount;
        int row = index / columnCount;
        int widthForHorizontalCentering = 0;

        // For centering the children if there isn't room for more children
        float emptySpaceInGrid = screenWidth - getPaddingLeft() - getPaddingRight()
                - (biggestChildWidth * columnCount);

        if (centerChildrenInGrid || emptySpaceInGrid < biggestChildWidth) {
            widthForHorizontalCentering = Math.round(emptySpaceInGrid / 2);
        }

        // Take care about the padding of each child and the padding of the grid view itself
        // You return the coordinates of the top left point of the child view
        return new Point(widthForHorizontalCentering + getPaddingLeft() + biggestChildWidth * col,
                getPaddingTop() + biggestChildHeight * row);
    }


    /**
     * Get the index of a child
     *
     * @param child the child of who you want to know the index
     * @return the index of the child
     */
    public int getIndexOf(View child) {

        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) == child) {
                return i;
            }
        }

        return -1;
    }


    /***********************************************************************************************
     *                                      EVENT HANDLERS                                         *
     **********************************************************************************************/

    /**
     * Manage the onClick events. The default behaviour is drag-drop functionality, but it can manage
     * other functionality if it is set in the Activity a new onItemClick listener
     *
     * @param view which was clicked
     */
    public void onClick(View view) {
        if (!enabled || getChildCount() == 0) {
            return;
        }
/*

        int index = getLastIndex();

        // Other functionality set with a new onItemClick listener
        if (onItemClickListener != null && index != -1) {
            onItemClickListener.onItemClick(null, getChildAt(getLastIndex()), getLastIndex(), getLastIndex() / columnCount);
            return;
        }

        // Default behaviour: drag-drop
        if (index != -1) {
            dragged = index;
            animateDragged();
            showDeleteView();

        }
*/

    }

    Shape getTouchedShape(float x, float y) {
        for (int i=getChildCount()-1;i>=0;i--){
            Shape s=(Shape)getChildAt(i);
            if (s.isTouched(x, y))
                return s;
        }
        return null;
    }

    /**
     * Manage the onLongClick events. The default behaviour is drag-drop functionality, but it can
     * manage other functionality if it is set in the Activity a new onItemLongClick listener
     *
     * @param view which was long clicked
     */
    public boolean onLongClick(View view) {
        if (!enabled || getChildCount() == 0) {
            return false;
        }

        dragged=getTouchedShape(lastX,lastY);

        // Default behaviour: drag-drop
        if (dragged != null) {
            animateDragged();
            showDeleteView();
            return true;
        }

        return false;
    }


    /**
     * Manage touch events
     *
     * @param view  the view touched
     * @param event the event for that view
     * @return managed the touch event or not
     */
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                enabled = true;
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                touching = true;
                break;

            case MotionEvent.ACTION_MOVE:
                manageMoveEvent(event);
                break;

            case MotionEvent.ACTION_UP:
                manageUpEvent();
                break;
        }

        if (dragged != null) {
            return true;
        }

        return false;
    }


    /**
     * Manage the move event when dragging a child
     *
     * @param event move event
     */
    protected void manageMoveEvent(MotionEvent event) {
        int delta = lastY - (int) event.getY();
        invalidate();

        if (dragged != null) {
            //change draw location of dragged visual
            int x = (int) event.getX();
            int y = (int) event.getY();
            int l = x - (3 * biggestChildWidth / 4);
            int t = y - (3 * biggestChildHeight / 4);
            dragged.layout(l, t, l + (biggestChildWidth * 3 / 2), t
                    + (biggestChildHeight * 3 / 2));


        } else {
            if (Math.abs(delta) > 2) {
                enabled = false;
            }
            requestLayout();
        }

        lastX = (int) event.getX();
        lastY = (int) event.getY();
        manageDeleteZoneHover(lastX, lastY);
        lastDelta = delta;
    }


    /**
     * Manage the up event when the user point the finger or mouse up from the screen
     */
    protected void manageUpEvent() {
        if (dragged != null) {
            View v = (dragged);

            if (touchUpInDeleteZoneDrop(lastX, lastY)) {
                draggedInDeleteZone = true;

            } else {

            }


            lastTarget = -1;
            dragged = null;
            hideDeleteView();
            draggedInDeleteZone = false;
        }
        touching = false;
        cancelAnimations();
    }


    /***********************************************************************************************
     *                                       ANIMATIONS                                            *
     **********************************************************************************************/

    /**
     * Animate the dragged child
     */
    protected void animateDragged() {
        int x = (int)(dragged).getMain_x() + biggestChildWidth / 2;
        int y = (int)dragged.getMain_y() + biggestChildHeight / 2;
        int l = x - (3 * biggestChildWidth / 4);
        int t = y - (3 * biggestChildHeight / 4);
        dragged.layout(l, t, l + (biggestChildWidth * 3 / 2), t + (biggestChildHeight * 3 / 2));

        AnimationSet animSet = new AnimationSet(true);
        ScaleAnimation scale = new ScaleAnimation(.667f, 1, .667f, 1, biggestChildWidth * 3 / 4,
                biggestChildHeight * 3 / 4);
        scale.setDuration(animT);
        AlphaAnimation alpha = new AlphaAnimation(1, .5f);
        alpha.setDuration(animT);

        animSet.addAnimation(scale);
        animSet.addAnimation(alpha);
        animSet.setFillEnabled(true);
        animSet.setFillAfter(true);

        dragged.clearAnimation();
        dragged.startAnimation(animSet);
    }


    /**
     * Animate all the children
     */


    /**
     * Create a simple translation animation
     *
     * @param oldOffset old position of the view
     * @param newOffset new position of the view
     * @return the translate animation
     */
    private Animation createTranslateAnimation(Point oldOffset, Point newOffset) {
        TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, oldOffset.x,
                Animation.ABSOLUTE, newOffset.x,
                Animation.ABSOLUTE, oldOffset.y,
                Animation.ABSOLUTE, newOffset.y);
        translate.setDuration(animT);
        translate.setFillEnabled(true);
        translate.setFillAfter(true);
        translate.setInterpolator(new AccelerateDecelerateInterpolator());

        return translate;
    }
    @Override
    public boolean onInterceptTouchEvent (MotionEvent event) {
        return false;
    }

    /**
     * Create a simple rotation animation
     *
     * @return the rotate animation
     */


    /**
     * Cancel all the animations
     */
    private void cancelAnimations() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.clearAnimation();
        }
    }


    /***********************************************************************************************
     *                                      DELETE ZONE                                            *
     **********************************************************************************************/

    /**
     * Sets the delete zone if you want to have one. If you drag a child view into the delete zone
     * it will be remove from the grid
     *
     * @param deleteZone the delete zone
     */
    public void setDeleteZone(DeleteDropZoneView deleteZone) {
        this.deleteZone = deleteZone;
    }


    /**
     * Creates a hover effect if the user drags a child in to the delete zone
     *
     * @param x the current x coordinate of the dragged view
     * @param y the current y coordinate of the dragged view
     */
    private void manageDeleteZoneHover(int x, int y) {

        if (deleteZone == null) {
            return;
        }

        if (touchUpInDeleteZoneDrop(x, y)) {
            deleteZone.highlight();
        } else {
            deleteZone.smother();
        }
    }


    /**
     * Checks if the user drops the dragged child into the delete zone
     *
     * @param x the current x coordinate of the dragged view
     * @param y the current y coordinate of the dragged view
     * @return if the child was dragged in the delete zone or not
     */
    private boolean touchUpInDeleteZoneDrop(int x, int y) {

        if (deleteZone == null || dragged == null) {
            return false;
        }

        Rect zone = new Rect();
        deleteZone.getGlobalVisibleRect(zone);

        View draggedChild =(dragged);
        Rect draggedZone = new Rect();
        draggedChild.getGlobalVisibleRect(draggedZone);

        int offset = getPixelFromDip(40);

        if (draggedZone.centerX() > zone.left - offset && draggedZone.centerX() < zone.right + offset &&
                draggedZone.centerY() > zone.top - offset && draggedZone.centerY() < zone.bottom + offset) {
            deleteZone.smother();
            return true;
        }

        return false;
    }

    /**
     * If it was set a delete view zone , makes it visible
     */
    private void showDeleteView() {
        if (deleteZone != null) {
            deleteZone.setVisibility(View.VISIBLE);
        }
    }


    /**
     * If it was set a delete view zone , makes it invisible
     */
    private void hideDeleteView() {
        if (deleteZone != null) {
            deleteZone.setVisibility(View.INVISIBLE);
        }
    }


    /***********************************************************************************************
     *                                        LISTENERS                                            *
     **********************************************************************************************/

    /**
     * Sets the default listeners
     */
    protected void setListeners() {
        setOnTouchListener(this);
        super.setOnClickListener(this);
        setOnLongClickListener(this);
    }


//    @Override
//    public void setOnClickListener(OnClickListener l) {
//        secondaryOnClickListener = l;
//    }

    /**
     * Sets the rearrange listener
     *
     * @param l the rearrange listener
     */
    public void setOnRearrangeListener(OnRearrangeListener l) {
        this.onRearrangeListener = l;
    }


    /**
     * Set the listener for the clicks on a item of the grid, for give a new functionality other than
     * the default drag-drop effect
     *
     * @param l the onItemClick listener
     */
    public void setOnItemClickListener(OnItemClickListener l) {
        this.onItemClickListener = l;
    }


    /**
     * Set the listener for the longClicks on a item of the grid, for give a new functionality other than
     * the default drag-drop effect
     *
     * @param l the onItemLongClick listener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        this.onItemLongClickListener = l;
    }


    /***********************************************************************************************
     *                                        UTILITIES                                            *
     **********************************************************************************************/

    /**
     * Transform a dpi value in to its equivalent number of pixel depending on the resolution of the
     * screen
     *
     * @param dp in dpi
     * @return number of pixels
     */
    private int getPixelFromDip(int dp) {
        Resources res = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    private int getPixelFromDip(float dp) {
        Resources res = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

}