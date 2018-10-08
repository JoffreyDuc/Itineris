package com.example.joffrey.itineris;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.List;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;

public class PointDrawer extends TimerTask implements LocationListener {
    Canvas canvas;
    Context context;

    LocationManager mLocationManager;

    double latitudeMapTop = 45.643159;
    double latitudeMapBottom = 45.637620;
    double longitudeMapLeft = 5.862951;
    double longitudeMapRight = 5.875756;
    double height = 500.0;
    double width = 500.0;

    public PointDrawer(Canvas canvas, Context context) {
        this.canvas = canvas;
        this.context = context;
    }

    @Override
    public void run() {

        // https://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null
        // https://stackoverflow.com/questions/15997079/getlastknownlocation-always-return-null-after-i-re-install-the-apk-file-via-ecli/15997304

        // TODO clean le canvas à chaque run
        Paint paint = new Paint();
        paint.setColor(Color.RED);

        Point2D p = gpsToMap(getCoordinates());

        canvas.drawCircle((float) p.getX(), (float) ((float) 500.0 - p.getY()), 7, paint);
    }

    private Point2D gpsToMap(Point2D gpsPoint) {

        double x = (gpsPoint.getX() - this.longitudeMapLeft) / (this.longitudeMapRight - this.longitudeMapLeft) * this.height;
        double y = (gpsPoint.getY() - this.latitudeMapBottom) / (this.latitudeMapTop - this.latitudeMapBottom) * this.width;

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

    private Point2D getCoordinates() {
        Location location = getLocation();
        //return new Point2D(location.getLongitude(), location.getLatitude());
        return new Point2D(this.longitudeMapLeft, this.latitudeMapBottom);
    }

    public Location getLocation() {
        LocationManager locationManager = (LocationManager) this.context.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("E","============================================== Erreur permission");
            return null;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        Location location = new Location("");
        //lng = location.getLongitude();
        //lat = location.getLatitude();

        return location;
        //return null;
    }
}