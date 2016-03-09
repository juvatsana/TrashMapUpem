package fr.upem.trashmapupem;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import fr.upem.trashmapupem.Listeners.*;
import fr.upem.trashmapupem.Task.GetAllTrashTask;

/**
 * Created by Mourougan on 05/03/2016.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback, LocationListener, LocationSource {


    public enum FM_TYPE { BROWN,YELLOW,GRAY,GREEN    }

    private GoogleMap mMap;
    private static HashMap<String,PoubelleMarker> mapMark = new HashMap<String,PoubelleMarker>();
    private boolean onListenerAjout=false;
    private boolean onListenerDelete=false;
    private boolean onListenerMain=true;

    private OnLocationChangedListener mListener;
    private LocationManager locationManager;

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
    public void activate(OnLocationChangedListener listener)
    {
        mListener = listener;
    }

    @Override
    public void deactivate()
    {
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if( mListener != null )
        {
            mListener.onLocationChanged(location);

            //Move the camera to the user's location once it's available!
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Toast.makeText(getActivity(), "status changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Toast.makeText(getActivity(), "provider disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Toast.makeText(getActivity(), "provider enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.i("Fragment map INFO", "PREPARATION DE ONCREATE");
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_maps, container, false);
        SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        myMAPF.getMapAsync(this); // Call onMapReady
        return root;
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

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

        mMap = googleMap;

        // First check for set the location enabled
        if(!mMap.isMyLocationEnabled())
        {
            try{
                mMap.setMyLocationEnabled(true); // Try to get the location
            }
            catch(SecurityException se)
            {
                System.err.println("Aie okey you will be locate in Univ MLV.");
                showSettingsAlert(); // Pour régler les paramètres pour le prochain lancement de l'appli ...
            }
        }

        // Second check just in case that the first check is not well managed
        if(mMap.isMyLocationEnabled())
        {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if(locationManager != null)
            {
                boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if(gpsIsEnabled)
                {
                    Log.i("GPS Enable","YES");
                    try{
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location!=null)
                        {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                        }
                        else
                        {
                            Log.i("GPS Location","null");
                        }
                    }
                    catch(SecurityException se) {
                        Log.i("GPS Security violation.", "YES");
                    }
                }
                else if(networkIsEnabled)
                {
                    Log.i("Network Enable","YES (not gps)");
                    try{
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location!=null)
                        {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                        }
                        else
                        {
                            Log.i("network Location","null");
                        }
                    }
                    catch(SecurityException se) {
                        Log.i("Net Security violation.", "YES");
                    }
                }
                else
                {
                    Log.i("Cant resolve internet","oups");
                    //Show an error dialog that GPS is disabled.
                }
            }
            else
            {
                Log.i("Cant resolve location","oups");
                //Show a generic error dialog since LocationManager is null for some reason
            }
            Log.i("My location is enabled", "YES");

            // If location manager is good, initiate the camera

        }
        // DEBUT : uncomment when location is fixed
        //else
        //{
            // Init current position as default on UPEM MLV
            mMap.addMarker(new MarkerOptions()
                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                    .position(new LatLng(48.838790, 2.585753))
                    .title("UPEM MLV")
                    .snippet("University of Marne La Vallée")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markposman))
                    .flat(true));

            //Position caméra
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(48.8392733, 2.5850778), 16));

            Log.i("My location is enabled", "NO");
        //}
        // FIN : uncomment when location is fixed

        // Load application markers
        Iterator it = mapMark.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            PoubelleMarker PM = (PoubelleMarker)pair.getValue();
            mMap.addMarker(PM.getMarkerOptions());
        }

        mMap.setInfoWindowAdapter(new ListenerInfoWindow(getActivity(),getContext(),mMap));

        // Check for Main activity
        if(isOnListenerMain())
        {
            mMap.setOnMapLongClickListener(new ListenerMainLongClick(getActivity(),getContext(),mMap));
        }

        // Check if DELETE Fragment
        if(isOnListenerDelete())
        {
            mMap.setOnMarkerClickListener(new ListenerDeleteMarkerClick(getActivity(),getContext(),mMap));
        }

        // Check if AJOUT Fragment
        if(isOnListenerAjout()) {
            mMap.setOnMapClickListener(new ListenerAddClick(getActivity(),getContext(),mMap));
        }
    }

    public boolean isOnListenerAjout() {
        return onListenerAjout;
    }

    public void setOnListenerAjout(boolean onListenerAjout) {
        this.onListenerAjout = onListenerAjout;
    }

    public boolean isOnListenerDelete() {
        return onListenerDelete;
    }

    public void setOnListenerDelete(boolean onListenerDelete) {
        this.onListenerDelete = onListenerDelete;
    }

    public boolean isOnListenerMain() {
        return onListenerMain;
    }

    public void setOnListenerMain(boolean onListenerMain) {
        this.onListenerMain = onListenerMain;
    }
}

