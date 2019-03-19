package arocher.fr.topmap.controler;

import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import arocher.fr.topmap.R;
import arocher.fr.topmap.SessionManager;
import arocher.fr.topmap.VolleySingleton;
import arocher.fr.topmap.myrequest.MyRequest;

public class CarteActivity extends AppCompatActivity implements LocationListener {

    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private static final int PERMS_CALL_ID = 1234;
    private LocationManager lm;
    private Spinner spinner_groupe;
    private MyRequest request;
    private SessionManager sessionManager;
    private String NOM = "";
    private final List<String> groupeList = new ArrayList<>();
    // https://developers.google.com/maps/documentation/android-sdk/location?hl=fr

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carte);

        sessionManager = new SessionManager(this);

        FragmentManager fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);

        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        // SPINNER CHOIX GROUPE ACTUEL
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupeList);
        spinner_groupe = findViewById(R.id.spinner_groupe);
        request.recupGroupe(sessionManager.getId(),new MyRequest.recupGroupeCallback() {
            @Override
            public void onSuccess(String nom, int nbGroupe) {
                groupeList.add(nom);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_groupe.setAdapter(adapter);
            }
        });
        // SELECTION DU GROUPE
        spinner_groupe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Object nom = parent.getItemAtPosition(position);
                googleMap.clear();
                request.recevoirCoordonnee(nom.toString(), new MyRequest.recevoirCoordonneeCallback() {
                    @Override
                    public void onSuccess(double lat, double lng, String pseudo) {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(pseudo));
                    }
                });
                NOM = nom.toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMS_CALL_ID);
            return;
        }
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        if(lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        loadMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMS_CALL_ID) {
            checkPermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lm != null) {
            lm.removeUpdates(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void loadMap() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                CarteActivity.this.googleMap = googleMap;
                googleMap.moveCamera(CameraUpdateFactory.zoomBy( 50 ));
                googleMap.setMyLocationEnabled( true );
            }
        });
        // Récupération de la lat et lng dans la bd de l'utilisateur connecté pour centrer sur sa derniere pos
        request.maPos(sessionManager.getId(), new MyRequest.maPosCallback() {
            @Override
            public void onSuccess(double lat, double lng) {
                LatLng googgleLocation = new LatLng(lat, lng);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(googgleLocation));
            }
        });
        // Recupération des lat et lng des membre du groupe choisi pour placer les marqueurs
        request.recevoirCoordonnee(NOM, new MyRequest.recevoirCoordonneeCallback() {
            @Override
            public void onSuccess(double lat, double lng, String pseudo) {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(pseudo));
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onLocationChanged(Location location) {
        // Récupération de la latitude et de la longitude grace à Location
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String lat =  Double.toString(latitude);
        String lng =  Double.toString(longitude);
        String id = sessionManager.getId();
        // Mise a jour des lat et lng dans la bd
        request.envoyerCoordonnee(lat, lng, id);
        // Effacage de tous les marqueur pour placer les nouveaux
        googleMap.clear();
        // Recupération des lat et lng des membre du groupe choisi pour placer les marqueurs
        request.recevoirCoordonnee(NOM, new MyRequest.recevoirCoordonneeCallback() {
            @Override
            public void onSuccess(double lat, double lng, String pseudo) {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(pseudo));
            }
        });
        Toast.makeText(this, "Location : " + latitude + " / " + longitude, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
