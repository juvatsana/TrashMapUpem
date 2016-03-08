package fr.upem.trashmapupem.Listeners;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import fr.upem.trashmapupem.FragmentMap;
import fr.upem.trashmapupem.R;

/**
 * Listener for map long click in main fragments (use in all the fragments)
 */
public class ListenerMainLongClick implements GoogleMap.OnMapLongClickListener
{
    private FragmentActivity fragmentActivity;
    private Context activityContext;
    private GoogleMap themMap;

    public ListenerMainLongClick(FragmentActivity fragmentActivity,Context activityContext,GoogleMap themMap)
    {
        this.fragmentActivity = fragmentActivity;
        this.activityContext = activityContext;
        this.themMap = themMap;
    }

    @Override
    public void onMapLongClick(LatLng arg0)
    {
        final LatLng point = arg0;

        // listener for "Add some contents?"
        final DialogInterface.OnClickListener dialogClickListenerAddSomeComment = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // This case to add contents

                        // Render layout add_content_garbage
                        AlertDialog.Builder builderNo = new AlertDialog.Builder(activityContext);
                        LayoutInflater inflater = fragmentActivity.getLayoutInflater();
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
                                        FragmentMap.FM_TYPE thetype = FragmentMap.FM_TYPE.GRAY;
                                        MarkerOptions themo = new MarkerOptions().position(point);

                                        // Check Strings name and comment for message/toast
                                        if ((stringETname.compareTo("") == 0) && (stringETcomment.compareTo("") == 0)) {
                                            // Render a message/toast
                                            Toast.makeText(fragmentActivity, "Mark added but no description. Thanks you.",
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
                                            Toast.makeText(fragmentActivity, "Mark added. Thanks you.",
                                                    Toast.LENGTH_LONG).show();
                                                    /*
                                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                                    builderYes.setMessage("Mark added. Thanks you.").show();
                                                    */
                                        }

                                        // Take the good color here because its spinner
                                        thetype = FragmentMap.checkFMType(textSpinner);

                                        // Recup marker with good garbage color
                                        MarkerOptions tempMarker = FragmentMap.addFragmentMapMarker(themo, thetype);

                                        // it can't be null but we have to manage this
                                        if (tempMarker == null)
                                        {
                                            tempMarker = themo;
                                        }
                                        themMap.addMarker(themo);
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
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activityContext,
                                R.array.garbage_colors, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);

                        // Render the layout
                        builderNo.show();

                        // Exit and listening
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Render a message/toast
                        Toast.makeText(activityContext, "Mark added. Thanks you.",
                                Toast.LENGTH_LONG).show();
                                /*
                                AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                builderYes.setMessage("Mark added. Thanks you.").show();
                                */

                        // Create the marker with no drawable
                        MarkerOptions themo = new MarkerOptions().position(point);

                        // Recup marker with the good garbage color - by default ...
                        MarkerOptions tempMarker = FragmentMap.addFragmentMapMarker(themo, FragmentMap.FM_TYPE.GRAY);

                        // In case if it returns null
                        if(tempMarker==null)
                        {
                            tempMarker = themo;
                        }

                        // At it to the temp map or it will not be render yet ...
                        themMap.addMarker(tempMarker);

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
                        AlertDialog.Builder builderYes = new AlertDialog.Builder(activityContext);
                        builderYes.setMessage("Do you want to add some comments ?").setPositiveButton("Yes", dialogClickListenerAddSomeComment)
                                .setNegativeButton("No", dialogClickListenerAddSomeComment).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Render a toast
                        Toast.makeText(activityContext, "Try Again",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        android.util.Log.i("onMapClick", "MapClickAdd baby!");

        // Render a message
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
