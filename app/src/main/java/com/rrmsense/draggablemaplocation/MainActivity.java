package com.rrmsense.draggablemaplocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements DrawRoute.onDrawRoute {

    MapView mapView;
    GoogleMap googleMap;
    double longitude;
    double latitude;
    Marker marker;

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.locationGPS)
    TextView locationGPS;
    @BindView(R.id.locationAddress)
    TextView locationAddress;
    private TrackGPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getPermissions();


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                googleMap = gMap;

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        return true;
                    }
                });

                googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        LatLng latLng = googleMap.getCameraPosition().target;
                        marker.setPosition(latLng);
                    }
                });

                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        LatLng latLng = googleMap.getCameraPosition().target;

                        latitude = latLng.latitude;
                        longitude = latLng.longitude;


                    }
                });
                mapView.onResume();
                updateLocation();


            }
        });

    }

    void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CALL_PHONE};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public void updateLocation() {

        gps = new TrackGPS(getApplicationContext());
        if (gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            LatLng latLng = new LatLng(latitude, longitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
            googleMap.animateCamera(cameraUpdate);
            marker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );

        } else {
            gps.showSettingsAlert();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void afterDraw(String result) {
        Toast.makeText(this, "Route", Toast.LENGTH_SHORT).show();

    }

    @OnClick(R.id.button)
    public void onClick() {
        String key = "AIzaSyAKHdw1ZwNzaZejEhi-8sWLczAscCY1oAI";//"AIzaSyCXu4kn4jzdLVp54AhkHNOrBMAyq4q4bXI";
        DrawRoute.getInstance(MainActivity.this, MainActivity.this).setFromLatLong(gps.getLatitude(),gps.getLongitude())
                .setToLatLong(latitude,longitude).setGmapAndKey(key, googleMap).run();



       /* String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + gps.getLatitude() + "," + gps.getLongitude() + "&destination=" +
                latitude + "," + longitude + "&sensor=false&mode=driving&alternatives=true&key=" + key;
        RequestParams params = new RequestParams();
        //params.put("key", key);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        locationAddress.setText(res);
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("json", res);
                        client.post("http://www.rrmelectronics.com/appserver/direction.php", params, new TextHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, String res) {

                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                                    }
                                }
                        );
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                    }
                }
        );*/
    }
}
