package fr.upem.trashmapupem;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.upem.trashmapupem.Listeners.*;

/**
 * Created by Mourougan on 05/03/2016.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback,ConnectionCallbacks,OnConnectionFailedListener,LocationListener {


    public Location getCurrentLocation() {
        return currentLocation;
    }

    public enum FM_TYPE { BROWN,YELLOW,GRAY,GREEN    }
    public enum FM_CONFIG {ADD,DELETE,MAP}

    private GoogleMap mMap;
    private static HashMap<String,PoubelleMarker> mapMark = new HashMap<String,PoubelleMarker>();
    private boolean onListenerAjout=false;
    private boolean onListenerDelete=false;
    private boolean onListenerMain=true;

    private GoogleApiClient playServices;
    private LocationRequest theLocationRequest;

    private Location currentLocation;
    private Marker currentMarker;

    private boolean firstStart=true;

    /**
     * Only use for convert a string to FM_TYPE
     * @param type
     * @return FM_TYPE
     */
    public static FM_TYPE checkFMType(String type)
    {
        FM_TYPE thetype = FM_TYPE.GRAY;
        if(type==null) return thetype;

        // Need to check the same value of the array garbage_colors in strings.xml ...
        switch(type)
        {
            case "Brown":
            case "BROWN":
                thetype = FM_TYPE.BROWN;
                break;
            case "Yellow":
            case "YELLOW":
                thetype = FM_TYPE.YELLOW;
                break;
            case "Green":
            case "GREEN":
                thetype = FM_TYPE.GREEN;
                break;
            default:
                break;
        }
        return thetype;
    }

    public static PoubelleMarker getPoubelleMarkerFromMap(String key)
    {
        if(mapMark.containsKey(key))
        {
            return mapMark.get(key);
        }
        return null;
    }

    public static List<PoubelleMarker> getPosOfMapMark()
    {
        List<PoubelleMarker> list = new ArrayList<>();
        Iterator it = mapMark.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            list.add((PoubelleMarker)pair.getValue());
        }
        return list;
    }

    /**
     * Add a marker for future instance
     * @param newMarker
     * @return sucess or not
     */
    public static Marker removeFragmentMapMarker(Marker newMarker)
    {
        if(newMarker==null)return null;

        LatLng LL = newMarker.getPosition();
        String newMarkKey=String.valueOf(LL.latitude)+":"+String.valueOf(LL.longitude);

        if(mapMark.containsKey(newMarkKey))
        {
            mapMark.remove(newMarkKey);
            return newMarker;
        }
        return null;
    }

    public static MarkerOptions addFragmentMapMarker(MarkerOptions newMarker,FM_TYPE color)
    {
        if(newMarker == null)  return null;
        newMarker.draggable(false) // Pour pas pouvoir le bouger, on vera autrement si on veut gérer sa
        .anchor(0.5f, 1.0f) // Ratio de la map pour que l'icone s'affiche bien
        .flat(false); // Pour que l'icone rotate en même temps que la map
        if(color!=null )
        {

            switch(color)
            {
                case BROWN:
                    newMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.iconpbbrown));
                    break;
                case GREEN:
                    newMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.iconpbgreen));
                    break;
                case GRAY:
                    newMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.iconpbgray));
                    break;
                case YELLOW:
                    newMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.iconpbyellow));
                    break;
            }
        }
        else
        {
            newMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.iconpbgray));
        }

        LatLng LI = newMarker.getPosition();
        String newMarkKey=String.valueOf(LI.latitude)+":"+String.valueOf(LI.longitude);
        if(!mapMark.containsKey(newMarkKey))
        {
            if(color==null)
            {
                mapMark.put(newMarkKey,new PoubelleMarker(FM_TYPE.GRAY, newMarker));
            }
            else
            {
                mapMark.put(newMarkKey,new PoubelleMarker(color, newMarker));
            }
            return newMarker;
        }
        return null;
    }

    public static Fragment newInstance(Context context) {
        FragmentMap f = new FragmentMap();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.i("Fragment map INFO", "PREPARATION DE ONCREATE");
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_maps, container, false);

        playServices = new GoogleApiClient.Builder(getContext())
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        return root;
    }

    @Override
    public void onStart()
    {
        Log.i("onStart", "yes");
        super.onStart();
        if(playServices.isConnected())
        {
            Log.i("onStart","alreadyConnected");
            loadLastLocation();
            // Don't move it
            SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            myMAPF.getMapAsync(this); // Call onMapReady
            return;
        }
        playServices.connect();
    }

    @Override
    public void onStop()
    {
        Log.i("onStop","yes");
        playServices.disconnect();
        super.onStop();
    }

    public void initLocationRequest()
    {
        theLocationRequest = LocationRequest.create();
        theLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        theLocationRequest.setInterval(1000); // Update location every second
    }

    public void initCurrentlocation()
    {
        currentLocation = new Location("");
        getCurrentLocation().setLatitude(48.8392733d);
        getCurrentLocation().setLongitude(2.5850778d);
    }

    public boolean comparatorDistanceWithMinimum(Location location,Location location2,Double distanceMin)
    {
        LatLng latLocation = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng latLocation2= new LatLng(location2.getLatitude(),location2.getLongitude());
        Double thedistance = FragmentListDistance.CalculationByDistance(latLocation,latLocation2);
        if(thedistance>distanceMin)
        {
            return true;
        }
        return false;
    }

    public void loadLastLocation()
    {
        Location mobileLocation = null;
        if(playServices.isConnected())
        {
            try
            {
                initLocationRequest();
                LocationServices.FusedLocationApi.requestLocationUpdates(playServices, theLocationRequest, this);
                mobileLocation = LocationServices.FusedLocationApi.getLastLocation(playServices);
                if (mobileLocation != null) {
                    currentLocation = new Location("");
                    getCurrentLocation().setLatitude(mobileLocation.getLatitude());
                    getCurrentLocation().setLongitude(mobileLocation.getLongitude());
                    Log.i("lastLocation Position", mobileLocation.getLatitude() + ":" + mobileLocation.getLongitude());
                }
            }
            catch(SecurityException se)
            {
                Log.i("Security on connect","exception");
            }
        }
        if(mobileLocation == null)
        {
            Log.i("OnConnect Position", "use position by default");
            if(getCurrentLocation() ==null)
            {
                initCurrentlocation();
            }
            if(firstStart)
            {
                showSettingsAlert();
                firstStart=false;
            }
        }
    }

    public void loadCurrentMarker(GoogleMap googleMap)
    {
        if(currentMarker!=null)
        {
            currentMarker.remove();
        }
        LatLng ll = new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude());
        currentMarker = googleMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 1.0f)
                    .position(ll)
                    .title("Your here")
                    .snippet("...")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markposman)));
    }
    public void loadApplicationMarkers(GoogleMap googleMap)
    {
        Iterator it = mapMark.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            PoubelleMarker PM = (PoubelleMarker)pair.getValue();
            googleMap.addMarker(PM.getMarkerOptions());
        }
    }

    public void loadConfig(FM_CONFIG config)
    {
        if(config==null)
        {
            return;
        }
        switch(config)
        {
            case ADD:
                onListenerDelete = false;
                onListenerAjout = true;
                onListenerMain = false;
                break;
            case DELETE:
                onListenerDelete = true;
                onListenerAjout = false;
                onListenerMain = false;
                break;
            case MAP:
                onListenerDelete = false;
                onListenerAjout = false;
                onListenerMain = true;
                break;
        }
    }

    public void initListeners(GoogleMap googleMap)
    {
        googleMap.setOnMapLongClickListener(null);
        googleMap.setOnMarkerClickListener(null);
        googleMap.setOnMapClickListener(null);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("Google API", "OK");
        loadLastLocation();

        // Don't move it
        SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        myMAPF.getMapAsync(this); // Call onMapReady
    }

     @Override
    public void onConnectionSuspended(int i) {
        Log.d("Connection suspended", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Connection failed", "Connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        if(currentLocation==null)return;
        if(mMap==null)return;
        if(comparatorDistanceWithMinimum(location,currentLocation,0.3d))
        {
            currentLocation=location;
            loadCurrentMarker(mMap);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude), 16));
        }
        Log.i("Location received: ", location.toString());
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        /*
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

        googleMap.clear();

        mMap = googleMap;

        initListeners(googleMap);
        loadCurrentMarker(googleMap);

        //Position caméra on currentMarker
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude), 16));

        loadApplicationMarkers(googleMap);

        // START Start listeners or others customs things for map
        // Custom marker click
        googleMap.setOnMarkerClickListener(new ListenerMarkerClick(getActivity(), getContext(), googleMap));

        // Custom info windows
        googleMap.setInfoWindowAdapter(new ListenerInfoWindow(getActivity(),getContext(),googleMap));

        // Check for Main activity
        if(isOnListenerMain())
        {
            googleMap.setOnMapLongClickListener(new ListenerMainLongClick(getActivity(),getContext(),googleMap));
        }

        // Check if DELETE Fragment
        if(isOnListenerDelete())
        {
            googleMap.setOnMarkerClickListener(new ListenerDeleteMarkerClick(getActivity(),getContext(),googleMap));
        }

        // Check if AJOUT Fragment
        if(isOnListenerAjout()) {
            googleMap.setOnMapClickListener(new ListenerAddClick(getActivity(),getContext(),googleMap));
        }
    }

    public boolean isOnListenerAjout() {
        return onListenerAjout;
    }

    public boolean isOnListenerDelete() {
        return onListenerDelete;
    }

    public boolean isOnListenerMain() {
        return onListenerMain;
    }
}

