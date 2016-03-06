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
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Mourougan on 05/03/2016.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static ArrayList<MarkerOptions> listMark = new ArrayList<>();
    private boolean onListener=false;

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mapFragment.getMapAsync(this);
        return inflater.inflate(R.layout.activity_maps, container, false);


    }
    */



    public void addFragmentMapMarker(MarkerOptions newMarker)
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

        if(isOnListener())
        {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
            {
                @Override
                public void onMapClick(LatLng arg0)
                {
                    final LatLng point = arg0;

                    final DialogInterface.OnClickListener dialogClickListenerAddSomeComment = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    // Here to add content
                                    AlertDialog.Builder builderNo = new AlertDialog.Builder(getContext());
                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    final View theview = inflater.inflate(R.layout.add_content_garbage, null);
                                    builderNo.setView(theview)
                                            // Add action buttons
                                            .setPositiveButton(R.string.ajout, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    EditText ETuser = (EditText) theview.findViewById(R.id.theusername);
                                                    EditText ETpass = (EditText) theview.findViewById(R.id.thepassword);
                                                    if((ETuser.getText().toString().compareTo("")==0)||(ETpass.getText().toString().compareTo("")==0))
                                                    {
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added but no description. Thanks you.").show();
                                                        MarkerOptions themo = new MarkerOptions().position(point);
                                                        mMap.addMarker(themo);
                                                        addFragmentMapMarker(themo);
                                                    }
                                                    else
                                                    {
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added. Thanks you.").show();
                                                        MarkerOptions themo = new MarkerOptions().position(point);
                                                        mMap.addMarker(themo);
                                                        addFragmentMapMarker(themo);
                                                    }
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // need to cancel the dialog

                                                    //LoginDialogFragment.this.getDialog().cancel();
                                                }
                                            });
                                    builderNo.show();
                                    setOnListener(false);
                                    mMap.setOnMapClickListener(null);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Mark added. Thanks you.").show();
                                    MarkerOptions themo = new MarkerOptions().position(point);
                                    mMap.addMarker(themo);
                                    addFragmentMapMarker(themo);
                                    setOnListener(false);
                                    mMap.setOnMapClickListener(null);
                                    break;
                            }
                        }
                    };

                    final DialogInterface.OnClickListener dialogClickListenerTryAgain = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Go").show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    AlertDialog.Builder builderNo = new AlertDialog.Builder(getContext());
                                    builderNo.setMessage("Then good bye").show();
                                    setOnListener(false);
                                    mMap.setOnMapClickListener(null);
                                    break;
                            }
                        }
                    };

                    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Do you want to add some comments ?").setPositiveButton("Yes", dialogClickListenerAddSomeComment)
                                            .setNegativeButton("No", dialogClickListenerAddSomeComment).show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    AlertDialog.Builder builderNo = new AlertDialog.Builder(getContext());
                                    builderNo.setMessage("Try again ?").setPositiveButton("Yes", dialogClickListenerTryAgain)
                                            .setNegativeButton("No", dialogClickListenerTryAgain).show();
                                    break;
                            }
                        }
                    };

                    android.util.Log.i("onMapClick", "MapClickAdd baby!");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });
        }
    }

    public boolean isOnListener() {
        return onListener;
    }

    public void setOnListener(boolean onListener) {
        this.onListener = onListener;
    }
}

