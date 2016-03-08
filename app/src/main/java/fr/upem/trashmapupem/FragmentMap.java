package fr.upem.trashmapupem;


import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

import fr.upem.trashmapupem.Listeners.*;

/**
 * Created by Mourougan on 05/03/2016.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {


    public enum FM_TYPE { BROWN,YELLOW,GRAY,GREEN    }

    private GoogleMap mMap;
    private static HashMap<String,PoubelleMarker> mapMark = new HashMap<String,PoubelleMarker>();
    private boolean onListenerAjout=false;
    private boolean onListenerDelete=false;
    private boolean onListenerMain=true;

    /**
     * Only use for convert a string to FM_TYPE
     * @param type
     * @return FM_TYPE
     */
    public static FM_TYPE checkFMType(String type)
    {
        FM_TYPE thetype = FM_TYPE.GRAY;
        if(type==null)return thetype;

        // Need to check the same value of the array garbage_colors in strings.xml ...
        switch(type)
        {
            case "Brown":
                thetype = FM_TYPE.BROWN;
                break;
            case "Yellow":
                thetype = FM_TYPE.YELLOW;
                break;
            case "Green":
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.i("Fragment map INFO", "PREPARATION DE ONCREATE");
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_maps, container, false);
        SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        myMAPF.getMapAsync(this); // Call onMapReady
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.e("RDY", "RDY");

        mMap = googleMap;

        //Position caméra
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(48.8392733, 2.5850778), 16));

        addFragmentMapMarker(new MarkerOptions()
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(48.838790, 2.585753))
                .title("UPEM MLV")
                .snippet("Info: Premiere poubelle au monde")
                .draggable(true)
                .flat(true),FM_TYPE.BROWN);

        // Load application markers
        Iterator it = mapMark.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            PoubelleMarker PM = (PoubelleMarker)pair.getValue();
            mMap.addMarker(PM.getMarkerOptions());
        }

        mMap.setInfoWindowAdapter(new ListenerInfoWindow(getActivity(),getContext(),googleMap));

        // Check for Main activity
        if(isOnListenerMain())
        {
            mMap.setOnMapLongClickListener(new ListenerMainLongClick(getActivity(),getContext(),googleMap));
        }

        // Check if DELETE Fragment
        if(isOnListenerDelete())
        {
            mMap.setOnMarkerClickListener(new ListenerDeleteMarkerClick(getActivity(),getContext(),googleMap));
        }

        // Check if AJOUT Fragment
        if(isOnListenerAjout()) {
            mMap.setOnMapClickListener(new ListenerAddClick(getActivity(),getContext(),googleMap));
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

