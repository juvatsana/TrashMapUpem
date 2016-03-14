package fr.upem.trashmapupem;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.PolylineOptions;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.upem.trashmapupem.Listeners.*;
import fr.upem.trashmapupem.Task.HttpConnection;
import fr.upem.trashmapupem.Task.PathJSONParser;

/**
 * Fragment qui permet d'afficher la GoogleMap.
 * Created by Mourougan on 05/03/2016.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback,ConnectionCallbacks,OnConnectionFailedListener,LocationListener {


    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setTrackLocation(Location trackLocation) { this.trackLocation = trackLocation;}

    public enum FM_TYPE { BROWN,YELLOW,GRAY,GREEN    }
    public enum FM_CONFIG {ADD,DELETE,MAP,TRACK}

    private GoogleMap mMap;
    private static HashMap<String,PoubelleMarker> mapMark = new HashMap<String,PoubelleMarker>();
    private boolean onListenerAjout=false;
    private boolean onListenerDelete=false;
    private boolean onListenerMain=true;
    private boolean onListenerTrack=false;

    private GoogleApiClient playServices;
    private LocationRequest theLocationRequest;

    private Location currentLocation;
    private Marker currentMarker;
    private Location trackLocation;

    private boolean firstStart=true;

    /**
     * Utiliser uniquement pour converter une couleur en enum FM_TYPE
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

    /**
     * Récupère l'instance PoubelleMarker de mapMark à partir de la clé de la map.
     * @param key Clé de la map
     * @return La valeur PoubelleMarker
     */
    public static PoubelleMarker getPoubelleMarkerFromMap(String key)
    {
        if(mapMark.containsKey(key))
        {
            return mapMark.get(key);
        }
        return null;
    }

    /**
     * Retourne une liste des PoubelleMarker de la map mapMark
     * @return
     */
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
     * Supprimer un marker de la map mapMark
     * @param newMarker Marker à supprimer
     */
    public static void removeFragmentMapMarker(Marker newMarker)
    {
        if(newMarker==null)return;

        LatLng LL = newMarker.getPosition();
        String newMarkKey=String.valueOf(LL.latitude)+":"+String.valueOf(LL.longitude);

        if(mapMark.containsKey(newMarkKey))
        {
            mapMark.remove(newMarkKey);
        }
        return;
    }

    /**
     * Ajoute une marker sur la GoogleMap.
     * @param newMarker MarkerOptions à ajouter
     * @param color Couleur à ajouter sous forme de FM_TYPE
     * @return Nouvelle MarkerOptions
     */
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

    /**
     * Créer une nouvelle instance du FragmentMap.
     * @param context Context de l'application
     * @return Instance du FragmentMap créé.
     */
    public static Fragment newInstance(Context context) {
        FragmentMap f = new FragmentMap();
        return f;
    }

    /**
     * Override de la methode onCreateView.
     * Initialise la GoogleApiClient.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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

    /**
     * Override de la methode onStart.
     * Se connecte à la GoogleApiClient. Si il est deja connecte car il s'est mis en pause, il charge la derniere localisation.
     */
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

    /**
     * Override de la methode onStop.
     * Se deconnecte de la GoogleApiClient.
     */
    @Override
    public void onStop()
    {
        Log.i("onStop","yes");
        playServices.disconnect();
        super.onStop();
    }

    /**
     * Initialise la requete de localisation.
     */
    public void initLocationRequest()
    {
        theLocationRequest = LocationRequest.create();
        theLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        theLocationRequest.setInterval(1000); // Update location every second
    }

    /**
     * Initialise la position courrante.
     */
    public void initCurrentlocation()
    {
        currentLocation = new Location("");
        getCurrentLocation().setLatitude(48.8392733d);
        getCurrentLocation().setLongitude(2.5850778d);
    }

    /**
     * Renvoie true si la distance entre les deux localisation est superieur a la distance minimum renseignee.
     * @param location Première localisation.
     * @param location2 Deuxième localisation.
     * @param distanceMin Distance minimum.
     * @return
     */
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

    /**
     * Charge la derniere localisation trouvee sinon prend celle par default.
     */
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

    /**
     * Charge le marker current.
     * @param googleMap
     */
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

    /**
     * Charge les marker de la map dans la GoogleMap.
     * @param googleMap
     */
    public void loadApplicationMarkers(GoogleMap googleMap)
    {
        Iterator it = mapMark.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            PoubelleMarker PM = (PoubelleMarker)pair.getValue();
            googleMap.addMarker(PM.getMarkerOptions());
        }
    }

    /**
     * Charge une config.
     * @param config La config voulue.
     */
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
                onListenerTrack = false;
                break;
            case DELETE:
                onListenerDelete = true;
                onListenerAjout = false;
                onListenerMain = false;
                onListenerTrack = false;
                break;
            case MAP:
                onListenerDelete = false;
                onListenerAjout = false;
                onListenerMain = true;
                onListenerTrack = false;
                break;
            case TRACK:
                onListenerDelete = false;
                onListenerAjout = false;
                onListenerMain = false;
                onListenerTrack = true;
                break;
        }
    }

    /**
     * Initialise les listeners.
     * @param googleMap
     */
    public void initListeners(GoogleMap googleMap)
    {
        googleMap.setOnMapLongClickListener(null);
        googleMap.setOnMarkerClickListener(null);
        googleMap.setOnMapClickListener(null);
    }

    /**
     * Override de la methode onConnected.
     * Charge la derniere localisation.
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("Google API", "OK");
        loadLastLocation();

        // Don't move it
        SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        myMAPF.getMapAsync(this); // Call onMapReady
    }

    /**
     * Override de la methode onConnectionSuspended.
     * @param i Indice.
     */
     @Override
    public void onConnectionSuspended(int i) {
        Log.d("Connection suspended", "Connection suspended");
    }

    /**
     * Override de la methode onConnectionFailed.
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Connection failed", "Connection failed");
    }

    /**
     * Override de la methode onLocationChanged. Depend de la LocationRequest.
     * Permis de charger la derniere localisation.
     * @param location La nouvelle localisation.
     */
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

    /**
     * Affiche une alerte si votre gps est desactive.
     */
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

    /**
     * Override de la methode onDestroy.
     */
    @Override
    public void onDestroy()
    {
        Log.i("onDestroy", "Yes");
        super.onDestroy();
    }

    /**
     * Appel de la methode onMapReady appele avec la methode getMapAsync.
     * @param googleMap La GoogleMap actuelle.
     */
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

        if(!isOnListenerTrack())
        {
            loadApplicationMarkers(googleMap);
        }
        else
        {

//            googleMap.addMarker(new MarkerOptions()
//                    .anchor(0.5f, 1.0f)
//                    .position(new LatLng(trackLocation.getLatitude(), trackLocation.getLongitude()))
//                    .title("Your here")
//                    .snippet("...")
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markposman)));

            MarkerOptions markerOption = new MarkerOptions()
                    .anchor(0.5f, 1.0f)
                    .position(new LatLng(trackLocation.getLatitude(), trackLocation.getLongitude()))
                    .title("Your here")
                    .snippet("...")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markposman));

            LatLng platlng = markerOption.getPosition();

            MarkerOptions options = new MarkerOptions();
            options.position(platlng);
            options.position(currentMarker.getPosition());

            googleMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 1.0f)
                    .position(new LatLng(trackLocation.getLatitude(), trackLocation.getLongitude()))
                    .title("Your trash selected !!!")
                    .snippet("...")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markposman)));

            googleMap.addMarker(markerOption);
            String url = getMapsApiDirectionsUrl(currentLocation.getLatitude(),currentLocation.getLongitude(),platlng.latitude,platlng.longitude);

            ReadPathTask pathTask = new ReadPathTask();
            pathTask.execute(url);
        }


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

    /***
     *  Private method creating the maps api directions url with the origin and destination
     * @param curlat current position latitude
     * @param curlng current position longitude
     * @param poublat the destination trash latitude
     * @param poublng the destination trash longitude
     * @return a string -- google maps api url
     */
    private String getMapsApiDirectionsUrl(double curlat,double curlng,double poublat,double poublng ) {
        String waypoints = "origin="
                + curlat + "," + curlng
                + "&destination=" + poublat + ","
                + poublng;
        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;

        String url = "https://maps.googleapis.com/maps/api/directions/json?"+params;
        Log.i("URL MOFOFOFOFO",url);
        return url;
    }

    /***
     *  private AsynTask that connect to google api direction service and create path, we don't bother the main task
     *  because it may take some time
     */
    private class ReadPathTask extends AsyncTask<String, Void, String> {

        /***
         *  Send url with HttpConnection class and retrieve the answer
         * @param url the google maps direction url
         * @return
         */
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
                Log.i("INFO Data",data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        /***
         * After we get the json responve from google api we lauche an other asynctask that parse the json
         * and add the path created to mMap
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserPathTask().execute(result);
        }
    }

    private class ParserPathTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        /***
         * Parse json answer from maps direction api and create a List<List<HashMap<String, String>>> to represent the
         * path we will take
         * @param jsonData
         * @return
         */
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return routes;
        }

        /***
         * Draw the path on google map with route create beforehand
         * @param routes
         */
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;


            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(20);
                polyLineOptions.color(R.color.colorPrimary);
            }

            mMap.addPolyline(polyLineOptions);
        }
    }


    /**
     * Check si le listener Ajout est true.
     * @return Le boolean Ajout.
     */
    public boolean isOnListenerAjout() {
        return onListenerAjout;
    }

    /**
     * Check si le listener Delete est true.
     * @return Le boolean Delete.
     */
    public boolean isOnListenerDelete() {
        return onListenerDelete;
    }

    /**
     * Check si le listener Main est true.
     * @return Le boolean Main.
     */
    public boolean isOnListenerMain() {
        return onListenerMain;
    }

    /**
     * Check si le listener Track est true.
     * @return Le boolean Track.
     */
    public boolean isOnListenerTrack() {
        return onListenerTrack;
    }

}

