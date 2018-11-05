package com.example.joffrey.itineris.utils;

import java.util.ArrayList;

public class Point2D {

    private double x;
    private double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(Point2D p) {
        this.x = p.x;
        this.y = p.y;
    }

    // Charge les points depuis le json
    public static ArrayList<Point2D> initializePointsGPS(String url) {
        ArrayList<Point2D> pointsGPS = new ArrayList<>();


        return pointsGPS;
    }

    public static ArrayList<int[]> initializePointsLinksGPS(String json_points_url) {
        ArrayList<int[]> pointsGPSlinks = new ArrayList<>();


        return pointsGPSlinks;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString(){
        return this.x + ", " + this.y;
    }

    public void rotate(Point2D centre, double angle){
        double newX = centre.x + (this.x-centre.x)*Math.cos(angle) - (this.y-centre.y)*Math.sin(angle);
        double newY = centre.y + (this.x-centre.x)*Math.sin(angle) + (this.y-centre.y)*Math.cos(angle);
        this.x = newX;
        this.y = newY;
    }

    public void rotate(double angle){
        double newX = this.x*Math.cos(angle) - this.y*Math.sin(angle);
        double newY = this.x*Math.sin(angle) + this.y*Math.cos(angle);
        this.x = newX;
        this.y = newY;
    }

    public void translate(double tX, double tY){
        this.x += tX;
        this.y += tY;
    }

    public void resize(double s){
        this.x *= s;
        this.y *= s;
    }

    public void resize(double s1, double s2){
        this.x *= s1;
        this.y *= s2;
    }

    public void swap(){
        double s = this.x;
        this.x = this.y;
        this.y = s;
    }

    public static Point2D translatePoint2D(Point2D p, Point2D hd, Point2D bd, Point2D max){
        Point2D t = new Point2D(p.getX() - hd.getX(), p.getY() - hd.getY());
        // 0 -> 1
        t.resize(1 / ( bd.getX() - hd.getX() ),1 / ( bd.getY() - hd.getY() ) );
        // 0 -> max
        t.resize(max.getX(), max.getY());
        t.swap();
        return t;
    }
}
