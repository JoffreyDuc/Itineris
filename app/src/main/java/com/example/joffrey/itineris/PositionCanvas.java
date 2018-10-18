package com.example.joffrey.itineris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class PositionCanvas extends View {
    Canvas canvas;
    float x, y;

    public PositionCanvas(Context context, float x, float y) {
        super(context);
        this.x = x;
        this.y = y;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        this.canvas = canvas;
        // Dessiner le point de la position actuelle
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawCircle(x, (float) 500.0 - y, 10, paint);
    }

}