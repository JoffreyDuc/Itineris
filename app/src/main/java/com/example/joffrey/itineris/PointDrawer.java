package com.example.joffrey.itineris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.util.TimerTask;

public class PointDrawer extends TimerTask implements LocationListener {
    Canvas canvas;

    public PointDrawer(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void run() {

        // https://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null
        // https://stackoverflow.com/questions/15997079/getlastknownlocation-always-return-null-after-i-re-install-the-apk-file-via-ecli/15997304

        // TODO clean le canvas à chaque run
        Paint paint = new Paint();
        paint.setColor(Color.RED);

        canvas.drawCircle(250, 250, 7, paint);
    }

    private Point2D gpsToMap(Point2D gpsPoint){
        double latitudeMapTop = 45.643159;
        double latitudeMapBottom = 45.637620;
        double longitudeMapLeft = 5.862951;
        double longitudeMapRight = 5.875756;
        double height = 500.0;
        double width = 500.0;

        double x = (gpsPoint.getX()-longitudeMapLeft) / (longitudeMapRight-longitudeMapLeft) * height;
        double y = (gpsPoint.getY()-latitudeMapBottom) / (latitudeMapTop-latitudeMapBottom) * width;

        return new Point2D(x, y);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}