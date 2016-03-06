package fr.upem.trashmapupem;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Mourougan on 05/03/2016.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static ArrayList<MarkerOptions> listMark = new ArrayList<>();

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mapFragment.getMapAsync(this);
        return inflater.inflate(R.layout.activity_maps, container, false);


    }
    */

    public static void addMarker(MarkerOptions newMarker)
    {
        if(newMarker != null)
        {
            listMark.add(newMarker);
        }
    }

    public static Fragment newInstance(Context context) {
        FragmentMap f = new FragmentMap();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_maps, container, false);
        SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        myMAPF.getMapAsync(this);
        //onMapReady(mMap);
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.e("RDY", "RDY");

        mMap = googleMap;

        //Position caméra
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(48.8392733, 2.5850778), 16));

        //Position d'un marker poubelle
        mMap.addMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pb))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(48.838790, 2.585753))
                .title("UPEM MLV")
                .snippet("Info: Premiere poubelle au monde")
                .draggable(true)
                .flat(true));

        for (MarkerOptions ma : listMark) {
            if (ma != null) {
                mMap.addMarker(ma);
            }
        }
    }
}

