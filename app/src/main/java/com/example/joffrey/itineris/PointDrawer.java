package com.example.joffrey.itineris;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import java.util.TimerTask;

public class PointDrawer extends TimerTask {
    private Canvas canvas;
    private Context context;
    private Point2D point2D;

    LocationManager mLocationManager;

    double latitudeMapTop = 45.643159;
    double latitudeMapBottom = 45.637620;
    double longitudeMapLeft = 5.862951;
    double longitudeMapRight = 5.875756;
    double height = 500.0;
    double width = 500.0;

    public PointDrawer(Canvas canvas, Context context, Point2D point2D) {
        this.canvas = canvas;
        this.context = context;
        this.point2D = point2D;
    }

    @Override
    public void run() {

        // https://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null
        // https://stackoverflow.com/questions/15997079/getlastknownlocation-always-return-null-after-i-re-install-the-apk-file-via-ecli/15997304

        // TODO clean le canvas à chaque run
        Paint paint = new Paint();
        paint.setColor(Color.RED);

        canvas.drawCircle((float) point2D.getX(), (float) ((float) 500.0 - point2D.getY()), 7, paint);

        /*Point2D p = gpsToMap(getCoordinates());

        canvas.drawCircle((float) p.getX(), (float) ((float) 500.0 - p.getY()), 7, paint);*/
    }

    private Point2D gpsToMap(Point2D gpsPoint) {

        double x = (gpsPoint.getX() - this.longitudeMapLeft) / (this.longitudeMapRight - this.longitudeMapLeft) * this.height;
        double y = (gpsPoint.getY() - this.latitudeMapBottom) / (this.latitudeMapTop - this.latitudeMapBottom) * this.width;

        return new Point2D(x, y);
    }

    private Point2D getCoordinates() {
        Location location = getLocation();
        Log.d("D", location.getLatitude() + ", " + location.getLongitude());
        return new Point2D(location.getLongitude(), location.getLatitude());
        //return new Point2D(this.longitudeMapLeft, this.latitudeMapBottom);
    }

    public Location getLocation() {

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("E","============================================== Erreur permission");
            return null;
        }
        Log.d("E","============================================== Bonnes permissions");

        /*locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        Location location = new Location("");*/

        /*LocationManager locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null) locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
*/
        return null;
    }

    public boolean checkLocationPermission(){
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}