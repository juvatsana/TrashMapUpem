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

/**
 * Created by Mourougan on 05/03/2016.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {


    public enum FM_TYPE { BROWN,YELLOW,GRAY,GREEN    }

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

    public MarkerOptions addFragmentMapMarker(MarkerOptions newMarker,FM_TYPE color)
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
                .flat(true),FM_TYPE.BROWN);

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

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.activity_infolayout, null);
                TextView tvinfoTitle = (TextView) v.findViewById(R.id.infoTitle);
                TextView tvinfoSnippet = (TextView) v.findViewById(R.id.infoSnippet);
                ImageView ivinfoimage1 = (ImageView) v.findViewById(R.id.imgInfoImage1);
                ImageView ivinfoimage2 = (ImageView) v.findViewById(R.id.imgInfoImage2);

                final LatLng ll = arg0.getPosition();
                String newMarkKey =String.valueOf(ll.latitude)+":"+String.valueOf(ll.longitude);

                if(mapMark.containsKey(newMarkKey))
                {
                    PoubelleMarker PM = mapMark.get(newMarkKey);
                    FM_TYPE type = PM.getType();
                    if(type!=null)
                    {
                        switch(type)
                        {
                            case GREEN:
                                ivinfoimage1.setImageResource(R.drawable.poubgreen);
                                break;
                            case BROWN:
                                ivinfoimage1.setImageResource(R.drawable.poubbrown);
                                break;
                            case YELLOW:
                                ivinfoimage1.setImageResource(R.drawable.poubyellow);
                                break;
                            default:
                                ivinfoimage1.setImageResource(R.drawable.poubgray);
                                break;
                        }
                    }
                    else
                    {
                        ivinfoimage1.setImageResource(R.drawable.poubgray);
                    }
                }
                else
                {
                    ivinfoimage1.setImageResource(R.drawable.poubgray);
                }

                tvinfoTitle.setText(arg0.getTitle());
                tvinfoSnippet.setText(arg0.getSnippet());
                return v;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

               return null;
            }
        });

        if(isOnListenerMain())
        {
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

                                                    // Recup Strings
                                                    String stringETname = ETname.getText().toString();
                                                    String stringETcomment = ETcomment.getText().toString();

                                                    // init some variables
                                                    FM_TYPE thetype = FM_TYPE.GRAY;
                                                    MarkerOptions themo = new MarkerOptions().position(point);

                                                    // Check Strings name and comment for message/toast
                                                    if ((stringETname.compareTo("") == 0) && (stringETcomment.compareTo("") == 0)) {
                                                        // Render a message/toast
                                                        Toast.makeText(getActivity(), "Mark added but no description. Thanks you.",
                                                                Toast.LENGTH_LONG).show();
                                                        /*
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added but no description. Thanks you.").show();
                                                        */
                                                    }
                                                    else // Add title or description with conditions
                                                    {
                                                        if (stringETname.compareTo("") != 0) {
                                                            themo.title(stringETname);
                                                        }
                                                        if (stringETcomment.compareTo("") != 0) {
                                                            themo.snippet(stringETcomment);
                                                        }
                                                        // Render a message/toast
                                                        Toast.makeText(getActivity(), "Mark added. Thanks you.",
                                                                Toast.LENGTH_LONG).show();
                                                        /*
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added. Thanks you.").show();
                                                        */
                                                    }

                                                    // Take the good color here
                                                    thetype = FragmentMap.checkFMType(textSpinner);

                                                    // Recup marker with good garbage color
                                                    MarkerOptions tempMarker = addFragmentMapMarker(themo, thetype);

                                                    // it can't be null but we have to manage this
                                                    if (tempMarker == null)
                                                    {
                                                        tempMarker = themo;
                                                    }
                                                    mMap.addMarker(themo);
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

                                    // Exit and listening
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    // Render a message/toast
                                    Toast.makeText(getActivity(), "Mark added. Thanks you.",
                                            Toast.LENGTH_LONG).show();
                                    /*
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Mark added. Thanks you.").show();
                                    */

                                    // Create the marker with no drawable
                                    MarkerOptions themo = new MarkerOptions().position(point);

                                    // Recup marker with the good garbage color - by default ...
                                    MarkerOptions tempMarker = addFragmentMapMarker(themo, null);

                                    // In case if it returns null
                                    if(tempMarker==null)
                                    {
                                        tempMarker = themo;
                                    }

                                    // At it to the temp map or it will not be render yet ...
                                    mMap.addMarker(tempMarker);

                                    // Exit and still listening
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

                    // Exit and listening
                    return true;
                }
            });
        }

        // Check if AJOUT Fragment
        if(isOnListenerAjout()) {
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

                                                    // Recup string of EditTexts
                                                    String stringETname = ETname.getText().toString();
                                                    String stringETcomment = ETcomment.getText().toString();

                                                    // init
                                                    MarkerOptions themo = new MarkerOptions().position(point);
                                                    FM_TYPE thetype = FM_TYPE.GRAY;

                                                    if ((stringETname.compareTo("") == 0) && (stringETcomment.compareTo("") == 0)) {
                                                        // Render a message/toast
                                                        Toast.makeText(getActivity(), "Mark added but no description. Thanks you.",
                                                                Toast.LENGTH_LONG).show();
                                                        /*
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added but no description. Thanks you.").show();
                                                        */
                                                    } else {
                                                        if (stringETname.compareTo("") != 0) {
                                                            themo.title(stringETname);
                                                        }
                                                        if (stringETcomment.compareTo("") != 0) {
                                                            themo.snippet(stringETcomment);
                                                        }
                                                        // Render a message/toast
                                                        Toast.makeText(getActivity(), "Mark added. Thanks you.",
                                                                Toast.LENGTH_LONG).show();
                                                        /*
                                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                        builderYes.setMessage("Mark added. Thanks you.").show();
                                                        */
                                                    }
                                                    // Take the good color here
                                                    thetype = FragmentMap.checkFMType(textSpinner);

                                                    // Recup marker with good garbage color
                                                    MarkerOptions tempMarker = addFragmentMapMarker(themo, thetype);

                                                    // It can't return null mark but he we have to manage it
                                                    if (tempMarker == null) {
                                                        tempMarker = themo;
                                                    }
                                                    mMap.addMarker(tempMarker);
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

                                    // Exit and listening
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
                                    MarkerOptions themo = new MarkerOptions().position(point);

                                    // Return the good garbage color - be default here
                                    MarkerOptions thenewgenmarker = addFragmentMapMarker(themo, null);

                                    // it can't be null but in case
                                    if (thenewgenmarker == null) {
                                        thenewgenmarker = themo;
                                    }
                                    mMap.addMarker(thenewgenmarker);

                                    // Exit and listenning
                                    break;
                            }
                        }
                    };


                    // listener for "Try Again?"
                    //final DialogInterface.OnClickListener dialogClickListenerTryAgain = new DialogInterface.OnClickListener() {
                    //    @Override
                    //    public void onClick(DialogInterface dialog, int which) {
                    //        switch (which) {
                    //            case DialogInterface.BUTTON_POSITIVE:
                    //                // Render a message/toast
                    //                Toast.makeText(getActivity(), "GO",
                    //                        Toast.LENGTH_LONG).show();
                    //                /*
                    //                AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                    //              builderYes.setMessage("Go").show();
                    //              */
                    //              break;
                    //
                    //            case DialogInterface.BUTTON_NEGATIVE:
                    //                // Render a message/toast
                    //                Toast.makeText(getActivity(), "Good Bye.",
                    //                        Toast.LENGTH_LONG).show();
                    //                /*
                    //                AlertDialog.Builder builderNo = new AlertDialog.Builder(getContext());
                    //                builderNo.setMessage("Then good bye").show();
                    //                */
                    //
                    //                // Quit Ajout
                    //                setOnListenerAjout(false);
                    //                mMap.setOnMapClickListener(null);
                    //                break;
                    //        }
                    //    }
                    //};

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
                                    // Render a message/Toast
                                    Toast.makeText(getActivity(), "Try again.",
                                            Toast.LENGTH_LONG).show();
                                    //AlertDialog.Builder builderNo = new AlertDialog.Builder(getContext());
                                    //builderNo.setMessage("Try again ?").setPositiveButton("Yes", dialogClickListenerTryAgain)
                                    //        .setNegativeButton("No", dialogClickListenerTryAgain).show();
                                    break;
                            }
                        }
                    };

                    android.util.Log.i("onMapClick", "MapClickAdd baby!");

                    // Render a message
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                    // Exit in case of negative reponse for "Are you sure"
                    // Exit and listening
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

