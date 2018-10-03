package com.example.joffrey.itineris;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MapFragment extends Fragment {
    private ImageView iv_canvas;
    private Canvas canvas;
    private Paint paint;
    private Bitmap baseBitmap;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*
        http://joerg-richter.fuyosoft.com/?p=120
        https://www.programering.com/a/MTO4gDMwATU.html
         */
        super.onCreate(savedInstanceState);

        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);

        if (baseBitmap == null) {
            /*baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(), iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
            canvas.drawColor(Color.WHITE);*/
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

}