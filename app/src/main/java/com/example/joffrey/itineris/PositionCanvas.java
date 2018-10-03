package com.example.joffrey.itineris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.Timer;

public class PositionCanvas extends View {
    public PositionCanvas(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Timer timer = new Timer();
        timer.schedule(new PointDrawer(canvas), 0, 1000);
        /*while(true){
            try {
                redrawPoint(canvas);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }
}
