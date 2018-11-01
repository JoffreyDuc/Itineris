package com.example.joffrey.itineris;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.preference.PreferenceManager;
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

        // On récupère la couleur choisie dans les paramètres
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        switch (sharedPrefs.getString("color", "RED")){
            case "RED":
                paint.setColor(Color.RED);
                break;
            case "BLUE":
                paint.setColor(Color.BLUE);
                break;
            case "YELLOW":
                paint.setColor(Color.YELLOW);
                break;
            case "GREEN":
                paint.setColor(Color.GREEN);
                break;
            default:
                paint.setColor(Color.RED);
                break;
        }

        // On traduit un point de coordonnées GPS à un point de coordonnées orthonormée
        Point2D locationOnMap = Point2D.translatePoint2D(point, P_HG, P_BD, P_MAX);
        canvas.drawCircle((float) locationOnMap.getX(), (float) locationOnMap.getY(), 8, paint);
    }

}