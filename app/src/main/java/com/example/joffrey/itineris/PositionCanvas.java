package com.example.joffrey.itineris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class PositionCanvas extends View {
    Canvas canvas;
    Point2D point;
    private Point2D P_HG = new Point2D(45.644771, 5.868455);
    private Point2D P_BD = new Point2D(45.639447, 5.875998);
    private Point2D P_MAX = new Point2D(500,500);

    public PositionCanvas(Context context, Point2D point) {
        super(context);
        this.point = point;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        this.canvas = canvas;

        // Dessine le point de la position actuelle
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        Point2D locationOnMap = Point2D.translatePoint2D(point, P_HG, P_BD, P_MAX);
        canvas.drawCircle((float) locationOnMap.getX(), (float) locationOnMap.getY(), 10, paint);
    }

}