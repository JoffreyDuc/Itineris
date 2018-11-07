package com.example.joffrey.itineris.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.joffrey.itineris.canvas.ItineraireCanvas;
import com.example.joffrey.itineris.adapter.ListViewAdapter;
import com.example.joffrey.itineris.canvas.PositionCanvas;
import com.example.joffrey.itineris.R;
import com.example.joffrey.itineris.dijkstra.Dijkstra;
import com.example.joffrey.itineris.dijkstra.Graph;
import com.example.joffrey.itineris.dijkstra.Node;
import com.example.joffrey.itineris.utils.Batiment;
import com.example.joffrey.itineris.utils.Point2D;
import com.example.joffrey.itineris.utils.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// Inspired by https://medium.com/@ssaurel/getting-gps-location-on-android-with-fused-location-provider-api-1001eb549089

public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SearchView.OnQueryTextListener {

    private final String JSON_POINTS_URL = "http://x-wing-cardcreator.com/others/itineris-data.json";

    private Location location;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    private ImageView imageView;
    private ImageView canvas;
    private ImageView canvasBatiment;
    private RequestQueue queue;
    private Button btnClearCanvasBatiment;
    private Button btnItineraire;
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    // Recherche
    private ListView list;
    private ListViewAdapter adapter;
    private SearchView searchView;
    private ArrayList<Batiment> arraylist = new ArrayList<>();
    // Points pour le graphe
    ArrayList<Point2D> pointsGPS = new ArrayList<>();
    ArrayList<int[]> pointsGPSlinks = new ArrayList<>();
    Graph graph;
    int indexLastBatimentSelected;


    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        // On ajoute les permissions qui permettent de récupérer la localisation de l'utilisateur
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (permissionsToRequest.size() > 0) {
            requestPermissions(permissionsToRequest.toArray(
                    new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        // On crée une instance de l'API Google qui gère la localisation
        googleApiClient = new GoogleApiClient.Builder(getContext()).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();

        // Création de l'instance d'une RequestQueue
        queue = VolleySingleton.getInstance(getContext()).getRequestQueue();

        // Tout ce qui est relatif pour la barre de recherche
        // Dans l'idée on récupère ici les bâtiments depuis un JSON distant
        /*arraylist.add(new Batiment("12A", 45.640864, 5.869404));
        arraylist.add(new Batiment("12B", 45.640900, 5.869431));
        arraylist.add(new Batiment("4C", 45.640411, 5.870449));*/
        getBatimentsFromJson();

        // Locate the Views in listview_main.xml
        searchView = rootView.findViewById(R.id.search);
        imageView = rootView.findViewById(R.id.ivPlanUsmb);
        canvas = rootView.findViewById(R.id.ivCanvas);
        canvasBatiment = rootView.findViewById(R.id.ivCanvasBat);
        btnClearCanvasBatiment = rootView.findViewById(R.id.btnClearCanvasBatiment);
        btnItineraire = rootView.findViewById(R.id.btnItineraire);
        list = rootView.findViewById(R.id.lvSearch);
        list.setVisibility(View.INVISIBLE);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapSearchVisibility(true);
            }
        });
        // Detect SearchView close
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                swapSearchVisibility(false);
                return false;
            }
        });
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(true);

        btnClearCanvasBatiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = new PositionCanvas(getActivity().getApplicationContext(), new Point2D(0,0), "colorBatiment");
                Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888); //width, height,..
                Canvas canvas = new Canvas(bitmap);
                v.draw(canvas);
                canvasBatiment.setImageBitmap(bitmap);
            }
        });

        btnItineraire.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                graph = Dijkstra.calculateShortestPathFromSource(graph, getIndexOfClosestPoint());
                ArrayList<Node> itineraire = Graph.itineraire(graph, indexLastBatimentSelected);
                drawCanvasItineraire(itineraire);
            }
        });

        return rootView;
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            Log.d("d", "LOCATION IS : " + location.getLatitude() + ", " + location.getLongitude());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                getActivity().finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            drawCanvasUser();
            Log.d("d", "LOCATION IS : " + location.getLatitude() + ", " + location.getLongitude());
        }

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Vous devez autoriser la permission pour utiliser la localisation", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;
            drawCanvasUser();
            Log.d("d", "LOCATION CHANGED TO : " + location.getLatitude() + ", " + location.getLongitude());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                        new AlertDialog.Builder(getContext()).
                                setMessage("Ces autorisations sont nécessaires pour l'utilisation de votre position. Merci de les autoriser.").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requestPermissions(permissionsRejected.
                                                toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                    }
                                }).setNegativeButton("Cancel", null).create().show();

                        return;
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }

    private void drawCanvasUser(){
        // On crée le canvas qui sera par-dessus le plan
        View v = new PositionCanvas(getActivity().getApplicationContext(), new Point2D(location.getLatitude(), location.getLongitude()), "colorUser");
        //View v = new PositionCanvas(getActivity().getApplicationContext(), new Point2D(45.640866, 5.869415));
        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888); //width, height,..
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);

        // On affiche notre canvas
        this.canvas.setImageBitmap(bitmap);
    }
    private void drawCanvasBatiment(Batiment batiment){
        // On crée le canvas du bâtiment qui sera par-dessus le plan
        View v = new PositionCanvas(getActivity().getApplicationContext(), new Point2D(batiment.getLatitude(), batiment.getLongitude()), "colorBatiment");
        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888); //width, height,..
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);

        // On affiche notre canvas
        this.canvasBatiment.setImageBitmap(bitmap);
    }
    private void drawCanvasItineraire(ArrayList<Node> arraylistNodes){
        View v = new ItineraireCanvas(getActivity().getApplicationContext(), arraylistNodes, pointsGPS);
        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888); //width, height,..
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        this.canvasBatiment.setImageBitmap(bitmap);
    }

    private void getBatimentsFromJson(){

        queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                JSON_POINTS_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jaBatiments = null;
                JSONArray jaNodesGPS = null;
                JSONArray jaLinksGPS = null;
                try {
                    /* Récupération des bâtiments */
                    jaBatiments = response.getJSONArray("batiments");
                    for (int i = 0; i < jaBatiments.length(); i++) {
                        JSONObject batiment = jaBatiments.getJSONObject(i);
                        JSONArray coordonnees = batiment.getJSONArray("coordonnees");
                        arraylist.add(new Batiment(batiment.getString("code"), coordonnees.getDouble(0), coordonnees.getDouble(1)));
                    }
                    /* Récupération des noeuds */
                    jaNodesGPS = response.getJSONArray("points");
                    for (int i = 0; i < jaNodesGPS.length(); i++){
                        JSONObject node = jaNodesGPS.getJSONObject(i);
                        JSONArray coordonnees = node.getJSONArray("coordonnees");
                        pointsGPS.add(new Point2D(coordonnees.getDouble(0), coordonnees.getDouble(1)));
                    }
                    /* Récupération des liens */
                    jaLinksGPS = response.getJSONArray("liens");
                    for (int i = 0; i < jaLinksGPS.length(); i++){
                        JSONArray coordonnees = (JSONArray) jaLinksGPS.get(i);
                        pointsGPSlinks.add(new int[]{coordonnees.getInt(0),coordonnees.getInt(1)});
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Pass results to ListViewAdapter Class
                adapter = new ListViewAdapter(getContext(), arraylist, searchView);
                // Binds the Adapter to the ListView
                list.setAdapter(adapter);

                // Initialisation du graphe
                graph = Graph.initialize(pointsGPS, pointsGPSlinks);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.append(error.getMessage());
            }
        });

        queue.add(jsonObjectRequest);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        // Afficher un point sur le batiment sélectionné
        for (Batiment batiment : arraylist){
            if (batiment.getNom().toLowerCase().equals(s.toLowerCase())) {
                drawCanvasBatiment(batiment);
                for (int i = 0; i < pointsGPS.size(); i++){
                    if (pointsGPS.get(i).getX() == batiment.getLatitude() && pointsGPS.get(i).getY() == batiment.getLongitude()){
                        indexLastBatimentSelected = i;
                    }
                }
                break;
            }
        }
        swapSearchVisibility(false);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filter(s);
        return false;
    }

    private void swapSearchVisibility(boolean visibility) {
        if (visibility){
            imageView.setVisibility(View.INVISIBLE);
            canvas.setVisibility(View.INVISIBLE);
            canvasBatiment.setVisibility(View.INVISIBLE);
            btnClearCanvasBatiment.setVisibility(View.INVISIBLE);
            list.setVisibility(View.VISIBLE);
        }else{
            imageView.setVisibility(View.VISIBLE);
            canvas.setVisibility(View.VISIBLE);
            canvasBatiment.setVisibility(View.VISIBLE);
            btnClearCanvasBatiment.setVisibility(View.VISIBLE);
            list.setVisibility(View.INVISIBLE);
            searchView.clearFocus();
        }
    }

    private int getIndexOfClosestPoint(){
        double distanceMin = Double.MAX_VALUE;
        int indexOfClosestPoint = -1;
        for(int i = 0; i < pointsGPS.size(); i++){
            Location locationPoint = new Location("point"); locationPoint.setLatitude(pointsGPS.get(i).getX()); locationPoint.setLongitude(pointsGPS.get(i).getY());
            double distance = location.distanceTo(locationPoint);
            if (distance < distanceMin) {
                distanceMin = distance;
                indexOfClosestPoint = i;
            }
        }
        return indexOfClosestPoint;
    }
}
