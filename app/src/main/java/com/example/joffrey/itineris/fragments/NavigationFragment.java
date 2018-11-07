package com.example.joffrey.itineris.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joffrey.itineris.utils.NavigationUtils;
import com.example.joffrey.itineris.R;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.rendering.LocationNode;
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;

public class NavigationFragment extends Fragment {
    private boolean installRequested;
    private boolean hasFinishedLoading = false;

    private Snackbar loadingMessageSnackbar = null;

    private ArSceneView arSceneView;

    // Renderables
    private ViewRenderable batiment12aLayoutRenderable;
    private ViewRenderable batiment12bLayoutRenderable;
    private ViewRenderable batiment4cLayoutRenderable;

    // ARCore-Location scene
    private LocationScene locationScene;


    public static NavigationFragment newInstance() {
        NavigationFragment fragment = new NavigationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation, container, false);

        arSceneView = rootView.findViewById(R.id.ar_scene_view);

        // Build a renderable from a 2D View.
        CompletableFuture<ViewRenderable> batiment12aLayout = ViewRenderable.builder().setView(getContext(), R.layout.batiment12a_layout).build();
        CompletableFuture<ViewRenderable> batiment12bLayout = ViewRenderable.builder().setView(getContext(), R.layout.batiment12b_layout).build();
        CompletableFuture<ViewRenderable> batiment4cLayout = ViewRenderable.builder().setView(getContext(), R.layout.batiment4c_layout).build();

        CompletableFuture.allOf(
                batiment12aLayout,
                batiment12bLayout,
                batiment4cLayout)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                NavigationUtils.displayError(getContext(), "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                batiment12aLayoutRenderable = batiment12aLayout.get();
                                batiment12bLayoutRenderable = batiment12bLayout.get();
                                batiment4cLayoutRenderable = batiment4cLayout.get();
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                NavigationUtils.displayError(getContext(), "Unable to load renderables", ex);
                            }

                            return null;
                        });

        // Set an update listener on the Scene that will hide the loading message once a Plane is
        // detected.
        arSceneView
                .getScene()
                .setOnUpdateListener(
                        frameTime -> {
                            if (!hasFinishedLoading) {
                                return;
                            }

                            if (locationScene == null) {
                                locationScene = new LocationScene(getContext(), getActivity(), arSceneView);

                                LocationMarker layoutLocationMarker12a = new LocationMarker(
                                        5.869404,
                                        45.640864,
                                        getBatiment12aView()
                                );LocationMarker layoutLocationMarker12b = new LocationMarker(
                                        5.869291,
                                        45.641194,
                                        getBatiment12bView()
                                );LocationMarker layoutLocationMarker4c = new LocationMarker(
                                        5.870449,
                                        45.640411,
                                        getBatiment4cView()
                                );

                                // render event is called every frame
                                layoutLocationMarker12a.setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        View eView = batiment12aLayoutRenderable.getView();
                                        TextView distanceTextView = eView.findViewById(R.id.tvDistance);
                                        distanceTextView.setText("à " + node.getDistance() + " m");
                                    }
                                });
                                layoutLocationMarker12b.setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        View eView = batiment12bLayoutRenderable.getView();
                                        TextView distanceTextView = eView.findViewById(R.id.tvDistance);
                                        distanceTextView.setText("à " + node.getDistance() + " m");
                                    }
                                });
                                layoutLocationMarker4c.setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        View eView = batiment4cLayoutRenderable.getView();
                                        TextView distanceTextView = eView.findViewById(R.id.tvDistance);
                                        distanceTextView.setText("à " + node.getDistance() + " m");
                                    }
                                });

                                // Adding the marker
                                locationScene.mLocationMarkers.add(layoutLocationMarker12a);
                                locationScene.mLocationMarkers.add(layoutLocationMarker12b);
                                locationScene.mLocationMarkers.add(layoutLocationMarker4c);
                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            if (locationScene != null) {
                                locationScene.processFrame(frame);
                            }

                            if (loadingMessageSnackbar != null) {
                                for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                    if (plane.getTrackingState() == TrackingState.TRACKING) {
                                        hideLoadingMessage();
                                    }
                                }
                            }
                        });

        // Lastly request CAMERA & fine location permission which is required by ARCore-Location.
        ARLocationPermissionHelper.requestPermission(getActivity());

        return rootView;
    }

    /**
     * Node of batiment12a layout
     *
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Node getBatiment12aView() {
        Node base = new Node();
        base.setRenderable(batiment12aLayoutRenderable);
        Context c = getContext();
        // Add  listeners etc here
        View eView = batiment12aLayoutRenderable.getView();
        eView.setOnTouchListener((View v, MotionEvent event) -> {
            Toast.makeText(
                    c, "Bâtiment 12A.", Toast.LENGTH_LONG)
                    .show();
            return false;
        });

        return base;
    }
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Node getBatiment12bView() {
        Node base = new Node();
        base.setRenderable(batiment12bLayoutRenderable);
        Context c = getContext();
        // Add  listeners etc here
        View eView = batiment12bLayoutRenderable.getView();
        eView.setOnTouchListener((View v, MotionEvent event) -> {
            Toast.makeText(
                    c, "Bâtiment 12B.", Toast.LENGTH_LONG)
                    .show();
            return false;
        });

        return base;
    }@SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Node getBatiment4cView() {
        Node base = new Node();
        base.setRenderable(batiment4cLayoutRenderable);
        Context c = getContext();
        // Add  listeners etc here
        View eView = batiment4cLayoutRenderable.getView();
        eView.setOnTouchListener((View v, MotionEvent event) -> {
            Toast.makeText(
                    c, "Bâtiment 4C.", Toast.LENGTH_LONG)
                    .show();
            return false;
        });

        return base;
    }

    /**
     * Make sure we call locationScene.resume();
     */
    @Override
    public void onResume() {
        super.onResume();

        if (locationScene != null) {
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = NavigationUtils.createArSession(getActivity(), installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(getActivity());
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                NavigationUtils.handleSessionException(getActivity(), e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            NavigationUtils.displayError(getContext(), "Impossible d'utiliser la caméra", ex);
            getActivity().finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }
    }

    /**
     * Make sure we call locationScene.pause();
     */
    @Override
    public void onPause() {
        super.onPause();

        if (locationScene != null) {
            locationScene.pause();
        }

        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(getActivity())) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(getActivity())) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(getActivity());
            } else {
                Toast.makeText(
                        getContext(), "La permission de la caméra est nécessaire pour exécuter cette application", Toast.LENGTH_LONG)
                        .show();
            }
            getActivity().finish();
        }
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        getActivity().findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_LONG);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }
}