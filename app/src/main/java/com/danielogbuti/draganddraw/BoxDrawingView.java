package com.danielogbuti.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class BoxDrawingView extends View {
    public static final String TAG = "BoxDrawingView";
    public static final int INVALID_POINTER_ID = -1;
    private Box currentBox;
    private ArrayList<Box> boxes = new ArrayList<>();
    private Paint boxPaint;
    private Paint backgroundPaint;
    private int secondPointerId = INVALID_POINTER_ID;
    private float x;
    private float y;

    //used when creating the view in code
    public BoxDrawingView(Context context) {
        this(context,null);
    }

    //used when inflating the view from XML
    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        boxPaint = new Paint();
        boxPaint.setColor(0x22ff0000);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(backgroundPaint);

        for (Box box : boxes){
            float left = Math.min(box.getOrigin().x,box.getCurrent().x);
            float right = Math.max(box.getOrigin().x,box.getCurrent().x);
            float top = Math.min(box.getOrigin().y,box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y,box.getCurrent().y);

            //negative for clockwise motion
            float angle  = -box.getAngle();
            //get the axis of rotation
            float px = (box.getOrigin().x+box.getCurrent().x)/2;
            float py  =(box.getOrigin().y+box.getCurrent().y)/2;
            canvas.save();
            canvas.rotate(angle,px,py);
            canvas.drawRect(left, top, right, bottom, boxPaint);
            canvas.restore();


        }
    }

    //standard method to get the angle of rotation
    public static float angleBetweenLines(float A1x,float A1y,float A2x,
                                          float A2y,float B1x,float B1y,float B2x, float B2y){
        float angle1 = (float)Math.atan2(A2y - A1y,A1x - A2x);
        float angle2 = (float)Math.atan2(B2y - B1y, B1x - B2x);

        float calculatedAngle = (float)Math.toDegrees(angle1 - angle2);
        if (calculatedAngle < 0 ){
            calculatedAngle +=360;
        }
        return calculatedAngle;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        PointF curr = new PointF(event.getX(),event.getY());

        Log.i(TAG, "Received event at x= "+curr.x+", y=" + curr.y +":");

        switch (event.getActionMasked()){
            case  MotionEvent.ACTION_DOWN:
                Log.i(TAG," ACTION_DOWN");
                //initialize the class so that it adds the original positon to the list
                currentBox = new Box(curr);
                boxes.add(currentBox);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //get the index of the second finger and find its id
                int pointerIndex = event.getActionIndex();
                secondPointerId = event.getPointerId(pointerIndex);
                Log.i(TAG,"ACTION_POINTER_DOWN "+secondPointerId);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG,"ACTION_MOVE");
                final int secondFingerIndex  = event.findPointerIndex(secondPointerId);

                //if the current box isnt empty set coordinates to it
                if(currentBox != null){
                    currentBox.setCurrent(curr);
                    //forces it to redraw itself
                    invalidate();
                }
                //when the second finger moves get the x and y coordinates and find the angle of rotation
                if (secondFingerIndex == 1) {
                    x = MotionEventCompat.getX(event, secondFingerIndex);
                    y = MotionEventCompat.getY(event, secondFingerIndex);

                    currentBox.setAngle((int) angleBetweenLines(currentBox.getCurrent().x,currentBox.getCurrent().y
                            ,x,y,currentBox.getCurrent().x,currentBox.getCurrent().y,curr.x,curr.y));
                    Log.d(TAG, "Angle is "+currentBox.getAngle());
                    Log.d(TAG,x + "and "+ y);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP");
                currentBox = null;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                int index = event.getActionIndex();
                int pointerId = event.getPointerId(index);
                Log.i(TAG,"ACTION_POINTER_UP" + pointerId);
                currentBox = null;
                break;

            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "ACTON_CANCEL");
                currentBox = null;
                break;
        }
        return true;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState",super.onSaveInstanceState());
        bundle.putSerializable(TAG, boxes);
        return  bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            boxes = (ArrayList<Box>) bundle.getSerializable(TAG);
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
}
