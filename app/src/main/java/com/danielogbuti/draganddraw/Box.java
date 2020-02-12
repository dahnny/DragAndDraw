package com.danielogbuti.draganddraw;

import android.graphics.PointF;
import android.os.Parcelable;

import java.io.Serializable;

public class Box implements Serializable {
    private transient PointF origin;
    private transient PointF current;
    public int angle;

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public Box(PointF origin) {
        this.origin = current = origin;
    }

    public PointF getOrigin() {
        return origin;
    }

    public void setOrigin(PointF origin) {
        this.origin = origin;
    }

    public PointF getCurrent() {
        return current;
    }

    public void setCurrent(PointF current) {
        this.current = current;
    }
}
