package com.example.joffrey.itineris.canvas;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.preference.PreferenceManager;
import android.view.View;

import com.example.joffrey.itineris.utils.Point2D;

public class PositionCanvas extends View {
    private Canvas canvas;
    private Point2D point;
    private String colorType;
    private Point2D P_HG = new Point2D(45.644771, 5.868455);
    private Point2D P_BD = new Point2D(45.639447, 5.875998);
    private Point2D P_MAX = new Point2D(500,500);

    public PositionCanvas(Context context, Point2D point, String colorType) {
        super(context);
        this.point = point;
        this.colorType = colorType;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        this.canvas = canvas;

        // Dessine le point de la position actuelle
        Paint paint = new Paint();

        // On récupère la couleur choisie dans les paramètres en fonction de si c'est l'utilisateur ou un bâtiment
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        switch (sharedPrefs.getString(colorType, "RED")){
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

        // On traduit un point de coordonnées GPS à un point de coordonnées dans un repère orthonormé
        Point2D locationOnMap = Point2D.translatePoint2D(point, P_HG, P_BD, P_MAX);

        // Et on le dessine
        canvas.drawCircle((float) locationOnMap.getX(), (float) locationOnMap.getY(), 5, paint);
    }

}