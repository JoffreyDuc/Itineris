package com.example.joffrey.itineris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Toast;

import com.example.joffrey.itineris.dijkstra.Node;
import com.example.joffrey.itineris.utils.Point2D;

import java.util.ArrayList;

public class ItineraireCanvas extends View {
    private ArrayList<Node> nodes;
    private ArrayList<Point2D> pointsGPS;
    private Point2D P_HG = new Point2D(45.644771, 5.868455);
    private Point2D P_BD = new Point2D(45.639447, 5.875998);
    private Point2D P_MAX = new Point2D(500,500);

    public ItineraireCanvas(Context context, ArrayList<Node> nodes, ArrayList<Point2D> pointsGPS) {
        super(context);
        this.nodes = nodes;
        this.pointsGPS = pointsGPS;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);

        double distance = 0;

        for (int i = 0; i < nodes.size()-1; i++){
            Node node = nodes.get(i);
            Node nextNode = nodes.get(i+1);

            Point2D point = pointsGPS.get(Integer.valueOf(node.getName()));
            Point2D nextPoint = pointsGPS.get(Integer.valueOf(nextNode.getName()));

            // On traduit un point de coordonnées GPS à un point de coordonnées dans un repère orthonormé
            Point2D locationOnMapPoint = Point2D.translatePoint2D(point, P_HG, P_BD, P_MAX);
            Point2D locationOnMapNextPoint = Point2D.translatePoint2D(nextPoint, P_HG, P_BD, P_MAX);

            //canvas.drawCircle((float) locationOnMapPoint.getX(), (float) locationOnMapPoint.getY(), 3, paint);
            canvas.drawLine((float)locationOnMapPoint.getX(), (float)locationOnMapPoint.getY(),
                            (float)locationOnMapNextPoint.getX(), (float)locationOnMapNextPoint.getY(),
                            paint);

            if (i == nodes.size()-2) {
                canvas.drawCircle((float) locationOnMapNextPoint.getX(), (float) locationOnMapNextPoint.getY(), 5, paint);
                distance += nextNode.getDistance();
            }
        }
        Toast.makeText(getContext(),"Distance à parcourir : " + (int) distance + " mètres", Toast.LENGTH_LONG).show();
    }
}
