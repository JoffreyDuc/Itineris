package com.example.joffrey.itineris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class PositionCanvas extends View {
    public PositionCanvas(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        //canvas.drawRect(150, 150, 200, 300, paint);
        canvas.drawCircle(250, 250, 10, paint);
    }
}
