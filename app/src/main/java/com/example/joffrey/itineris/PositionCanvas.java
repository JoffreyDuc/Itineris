package com.example.joffrey.itineris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;

public class PositionCanvas extends View implements LocationListener {
    Canvas canvas;

    public PositionCanvas(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        this.canvas = canvas;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Redessiner le canvas (à voir)
        // Dessiner le point de la posision actuelle
        new PointDrawer(canvas, getContext(), new Point2D(location.getLongitude(), location.getLatitude()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        // Redessiner le canvas (pour enlever les points)
    }
}
