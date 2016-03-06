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
import android.widget.Spinner;
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
import java.util.Map;

/**
 * Created by Mourougan on 05/03/2016.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static HashMap<String,MarkerOptions> mapMark = new HashMap<String,MarkerOptions>();
    private boolean onListenerAjout=false;

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mapFragment.getMapAsync(this);
        return inflater.inflate(R.layout.activity_maps, container, false);


    }
    */

    /**
     * Add a marker for future instance
     * @param newMarker
     * @return sucess or not
     */
    public MarkerOptions addFragmentMapMarker(MarkerOptions newMarker,String color)
    {
        if(color!=null )
        {
            if(color.compareTo("Red")==0||color.compareTo("Brown")==0||color.compareTo("Yellow")==0)
            {
                newMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pb));
            }
        }
        if(newMarker == null)  return null;

        LatLng LI = newMarker.getPosition();
        String newMarkKey=String.valueOf(LI.latitude)+":"+String.valueOf(LI.longitude);
        if(!mapMark.containsKey(newMarkKey))
        {
            mapMark.put(newMarkKey,newMarker);
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

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_maps, container, false);
        SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        myMAPF.getMapAsync(this); // Call onMapReady
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

        // Load application markers
        Iterator it = mapMark.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            mMap.addMarker((MarkerOptions)pair.getValue());
        }

        /*
        for (MarkerOptions ma : listMark) {
            if (ma != null) {
                mMap.addMarker(ma);
            }
        }
        */

        // Check if AJOUT Fragment
        if(isOnListenerAjout())
        {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
            {
                @Override
                public void onMapClick(LatLng arg0)
                {
                    final LatLng point = arg0;

                    // listener for "Add some contents?"
                    final DialogInterface.OnClickListener dialogClickListenerAddSomeComment = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    // This case to add contents

                                    // Render layout add_content_garbage
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
                                                    Spinner SPspinner = (Spinner) theview.findViewById(R.id.thespinner);
                                                    String textSpinner = SPspinner.getSelectedItem().toString();
                                                    if((ETuser.getText().toString().compareTo("")==0)||(ETpass.getText().toString().compareTo("")==0))
                                                    {
                                                        // Render a message/toast
                                                        Toast.makeText(getActivity(), "Mark added but no description. Thanks you.",
                                                                Toast.LENGTH_LONG).show();
                                                        /*
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added but no description. Thanks you.").show();
                                                        */
                                                    }
                                                    else
                                                    {
                                                        // Render a message/toast
                                                        Toast.makeText(getActivity(), "Mark added. Thanks you.",
                                                                Toast.LENGTH_LONG).show();
                                                        /*
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added. Thanks you.").show();
                                                        */
                                                    }
                                                    // Add marker
                                                    MarkerOptions themo = new MarkerOptions().position(point);

                                                    // Recup marker with good garbage color
                                                    MarkerOptions tempMarker = addFragmentMapMarker(themo,textSpinner);
                                                    if(tempMarker!=null)
                                                    {
                                                        mMap.addMarker(tempMarker);
                                                    }
                                                    else // !!! Maybe we have to manage null Marker !!!
                                                    {
                                                        mMap.addMarker(themo);
                                                    }
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // need to cancel the dialog

                                                    //LoginDialogFragment.this.getDialog().cancel();
                                                }
                                            });

                                    //Create and load the spinner
                                    Spinner spinner = (Spinner) theview.findViewById(R.id.thespinner);
                                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                                            R.array.garbage_colors, android.R.layout.simple_spinner_item);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinner.setAdapter(adapter);

                                    // Render the layout
                                    builderNo.show();
                                    setOnListenerAjout(false);
                                    mMap.setOnMapClickListener(null);

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    // Render a message/toast
                                    Toast.makeText(getActivity(), "Mark added. Thanks you.",
                                            Toast.LENGTH_LONG).show();
                                    /*
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Mark added. Thanks you.").show();
                                    */

                                    // Add marker
                                    MarkerOptions themo = new MarkerOptions().position(point);
                                    mMap.addMarker(themo);
                                    addFragmentMapMarker(themo,null);

                                    // Quit Ajout
                                    setOnListenerAjout(false);
                                    mMap.setOnMapClickListener(null);
                                    break;
                            }
                        }
                    };

                    // listener for "Try Again?"
                    final DialogInterface.OnClickListener dialogClickListenerTryAgain = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    // Render a message
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Go").show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    // Render a message/toast
                                    Toast.makeText(getActivity(), "Good Bye.",
                                            Toast.LENGTH_LONG).show();
                                    /*
                                    AlertDialog.Builder builderNo = new AlertDialog.Builder(getContext());
                                    builderNo.setMessage("Then good bye").show();
                                    */

                                    // Quit Ajout
                                    setOnListenerAjout(false);
                                    mMap.setOnMapClickListener(null);
                                    break;
                            }
                        }
                    };

                    // listener for "Are You sure"
                    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    // Render a message
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Do you want to add some comments ?").setPositiveButton("Yes", dialogClickListenerAddSomeComment)
                                            .setNegativeButton("No", dialogClickListenerAddSomeComment).show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    // Render a message
                                    AlertDialog.Builder builderNo = new AlertDialog.Builder(getContext());
                                    builderNo.setMessage("Try again ?").setPositiveButton("Yes", dialogClickListenerTryAgain)
                                            .setNegativeButton("No", dialogClickListenerTryAgain).show();
                                    break;
                            }
                        }
                    };

                    android.util.Log.i("onMapClick", "MapClickAdd baby!");

                    // Render a message
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });
        }
    }

    public boolean isOnListenerAjout() {
        return onListenerAjout;
    }

    public void setOnListenerAjout(boolean onListenerAjout) {
        this.onListenerAjout = onListenerAjout;
    }
}

