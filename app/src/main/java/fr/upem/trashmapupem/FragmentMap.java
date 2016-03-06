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

/**
 * Created by Mourougan on 05/03/2016.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {

    public class PoubelleMarker {
        String type;
        MarkerOptions marker;

        PoubelleMarker(String type, MarkerOptions marker) {
            this.type = type;
            this.marker = marker;
        }

        public String getType()
        {
            return type;
        }

        public MarkerOptions getMarkerOptions()
        {
            return marker;
        }
    }

    private GoogleMap mMap;
    //private static HashMap<String,MarkerOptions> mapMark = new HashMap<String,MarkerOptions>();
    private static HashMap<String,PoubelleMarker> mapMark = new HashMap<String,PoubelleMarker>();
    private boolean onListenerAjout=false;
    private boolean onListenerDelete=false;
    private boolean onListenerMain=true;

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mapFragment.getMapAsync(this);
        return inflater.inflate(R.layout.activity_maps, container, false);


    }
    */

    public static List<LatLng> getPosOfMapMark()
    {
        List<LatLng> list = new ArrayList<>();
        Iterator it = mapMark.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            PoubelleMarker PM = (PoubelleMarker)pair.getValue();
            MarkerOptions MO = PM.getMarkerOptions();
            list.add(MO.getPosition());
        }
        return list;
    }

    /**
     * Add a marker for future instance
     * @param newMarker
     * @return sucess or not
     */
    public Marker removeFragmentMapMarker(Marker newMarker)
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

    public MarkerOptions addFragmentMapMarker(MarkerOptions newMarker,String color)
    {
        if(newMarker == null)  return null;

        if(color!=null )
        {
            if(color.compareTo("Red")==0||color.compareTo("Brown")==0||color.compareTo("Yellow")==0)
            {
                newMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pb));
            }
        }
        else
        {
            newMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pb));
        }

        LatLng LI = newMarker.getPosition();
        String newMarkKey=String.valueOf(LI.latitude)+":"+String.valueOf(LI.longitude);
        if(!mapMark.containsKey(newMarkKey))
        {
            if(color==null)
            {
                mapMark.put(newMarkKey,new PoubelleMarker("Brown", newMarker));
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

        addFragmentMapMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pb))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(48.838790, 2.585753))
                .title("UPEM MLV")
                .snippet("Info: Premiere poubelle au monde")
                .draggable(true)
                .flat(true),"Brown");

        /*
        //Position d'un marker poubelle
        mMap.addMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pb))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(48.838790, 2.585753))
                .title("UPEM MLV")
                .snippet("Info: Premiere poubelle au monde")
                .draggable(true)
                .flat(true));
        */

        // Load application markers
        Iterator it = mapMark.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            PoubelleMarker PM = (PoubelleMarker)pair.getValue();
            mMap.addMarker(PM.getMarkerOptions());
        }

        /*
        for (MarkerOptions ma : listMark) {
            if (ma != null) {
                mMap.addMarker(ma);
            }
        }
        */

        if(isOnListenerMain())
        {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {

                    View v = getActivity().getLayoutInflater().inflate(R.layout.activity_infolayout, null);
                    TextView tvinfoTitle = (TextView) v.findViewById(R.id.infoTitle);
                    TextView tvinfoSnippet = (TextView) v.findViewById(R.id.infoSnippet);

                    tvinfoTitle.setText(arg0.getTitle());
                    tvinfoSnippet.setText(arg0.getSnippet());
                    return v;

                    /*
                    // Getting view from the layout file info_window_layout
                    View v = getActivity().getLayoutInflater().inflate(R.layout.activity_infolayout, null);
                    // Getting the position from the marker
                    LatLng latLng = arg0.getPosition();
                    // Getting reference to the TextView to set latitude
                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                    // Getting reference to the TextView to set longitude
                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
                    // Setting the latitude
                    tvLat.setText("Latitude:" + latLng.latitude);
                    // Setting the longitude
                    tvLng.setText("Longitude:"+ latLng.longitude);
                    // Returning the view containing InfoWindow contents
                    return v;
                    */
                }
            });

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng arg0) {
                    final LatLng point = arg0;

                    // listener for "Add some contents?"
                    final DialogInterface.OnClickListener dialogClickListenerAddSomeComment = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
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

                                                    // Recup items of layout
                                                    EditText ETname = (EditText) theview.findViewById(R.id.thename);
                                                    EditText ETcomment = (EditText) theview.findViewById(R.id.thecomment);
                                                    Spinner SPspinner = (Spinner) theview.findViewById(R.id.thespinner);
                                                    String textSpinner = SPspinner.getSelectedItem().toString();

                                                    // init marker
                                                    MarkerOptions themo = new MarkerOptions().position(point);

                                                    if ((ETname.getText().toString().compareTo("") == 0) && (ETcomment.getText().toString().compareTo("") == 0)) {
                                                        // Render a message/toast
                                                        Toast.makeText(getActivity(), "Mark added but no description. Thanks you.",
                                                                Toast.LENGTH_LONG).show();
                                                        /*
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added but no description. Thanks you.").show();
                                                        */
                                                    } else {
                                                        if (ETname.getText().toString().compareTo("") != 0) {
                                                            themo.title(ETname.getText().toString());
                                                        }
                                                        if (ETcomment.getText().toString().compareTo("") != 0) {
                                                            themo.snippet(ETcomment.getText().toString());
                                                        }
                                                        // Render a message/toast
                                                        Toast.makeText(getActivity(), "Mark added. Thanks you.",
                                                                Toast.LENGTH_LONG).show();
                                                        /*
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added. Thanks you.").show();
                                                        */
                                                    }
                                                    // Recup marker with good garbage color
                                                    MarkerOptions tempMarker = addFragmentMapMarker(themo, textSpinner);
                                                    if (tempMarker != null) {
                                                        mMap.addMarker(tempMarker);
                                                    } else // !!! Maybe we have to manage null Marker !!!
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

                                    // Quit ajout
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

                                    // Add marker by default
                                    MarkerOptions themo = new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.pb));
                                    mMap.addMarker(themo);
                                    addFragmentMapMarker(themo, null);

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
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    // Render a message
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Do you want to add some comments ?").setPositiveButton("Yes", dialogClickListenerAddSomeComment)
                                            .setNegativeButton("No", dialogClickListenerAddSomeComment).show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    // Render a toast
                                    Toast.makeText(getActivity(), "Try Again",
                                            Toast.LENGTH_LONG).show();
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

        // Check if DELETE Fragment
        if(isOnListenerDelete())
        {
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {



                @Override
                public boolean onMarkerClick(Marker arg0) {

                    final Marker markerToDelete = arg0;

                    // listener for "Do you want to delete this mark?"
                    final DialogInterface.OnClickListener dialogClickListenerDeleteMark = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:

                                    // Delete marker
                                    Marker newMarkerToDelete = removeFragmentMapMarker(markerToDelete);
                                    markerToDelete.remove();

                                    // Render a message/toast
                                    Toast.makeText(getActivity(), "Marker Delete.",
                                            Toast.LENGTH_LONG).show();
                                    /*
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Marker delete").show();
                                    */
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    // Render a message
                                    Toast.makeText(getActivity(), "Delete canceled...",
                                            Toast.LENGTH_LONG).show();
                                    /*
                                    AlertDialog.Builder builderNo = new AlertDialog.Builder(getContext());
                                    builderNo.setMessage("Delete canceled...").show();
                                    */
                                    break;
                            }
                        }
                    };

                    // Starte die Navigation mit Google Maps, sobald der Marker gedrckt wird

                    // Render a message
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Do you really want to delete this mark ?").setPositiveButton("Yes", dialogClickListenerDeleteMark)
                            .setNegativeButton("No", dialogClickListenerDeleteMark).show();

                    // Quit ajout
                    setOnListenerDelete(false);
                    mMap.setOnMarkerClickListener(null);
                    return true;
                }
            });
        }

        // Check if AJOUT Fragment
        if(isOnListenerAjout())
        {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng arg0) {
                    final LatLng point = arg0;

                    // listener for "Add some contents?"
                    final DialogInterface.OnClickListener dialogClickListenerAddSomeComment = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
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

                                                    // Recup items of layout
                                                    EditText ETname = (EditText) theview.findViewById(R.id.thename);
                                                    EditText ETcomment = (EditText) theview.findViewById(R.id.thecomment);
                                                    Spinner SPspinner = (Spinner) theview.findViewById(R.id.thespinner);
                                                    String textSpinner = SPspinner.getSelectedItem().toString();

                                                    // init marker
                                                    MarkerOptions themo = new MarkerOptions().position(point);

                                                    if ((ETname.getText().toString().compareTo("") == 0) && (ETcomment.getText().toString().compareTo("") == 0)) {
                                                        // Render a message/toast
                                                        Toast.makeText(getActivity(), "Mark added but no description. Thanks you.",
                                                                Toast.LENGTH_LONG).show();
                                                        /*
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added but no description. Thanks you.").show();
                                                        */
                                                    } else {
                                                        if (ETname.getText().toString().compareTo("") != 0) {
                                                            themo.title(ETname.getText().toString());
                                                        }
                                                        if (ETcomment.getText().toString().compareTo("") != 0) {
                                                            themo.snippet(ETcomment.getText().toString());
                                                        }
                                                        // Render a message/toast
                                                        Toast.makeText(getActivity(), "Mark added. Thanks you.",
                                                                Toast.LENGTH_LONG).show();
                                                        /*
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added. Thanks you.").show();
                                                        */
                                                    }
                                                    // Recup marker with good garbage color
                                                    MarkerOptions tempMarker = addFragmentMapMarker(themo, textSpinner);
                                                    if (tempMarker != null) {
                                                        mMap.addMarker(tempMarker);
                                                    } else // !!! Maybe we have to manage null Marker !!!
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

                                    // Quit ajout
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

                                    // Add marker by default
                                    MarkerOptions themo = new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.pb));
                                    mMap.addMarker(themo);
                                    addFragmentMapMarker(themo, null);

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
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    // Render a message/toast
                                    Toast.makeText(getActivity(), "GO",
                                            Toast.LENGTH_LONG).show();
                                    /*
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Go").show();
                                    */
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
                            switch (which) {
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

