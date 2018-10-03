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

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        View v = new PositionCanvas(getActivity().getApplicationContext());
        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888); //width, height,..
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        ImageView iv = (ImageView) rootView.findViewById(R.id.ivCanvas);
        iv.setImageBitmap(bitmap);

        return rootView;
    }

}