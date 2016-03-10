package fr.upem.trashmapupem.Listeners;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fr.upem.trashmapupem.FragmentMap;
import fr.upem.trashmapupem.Task.InsertTrashTask;
import fr.upem.trashmapupem.R;

/**
 * Listener for map click in add fragment
 */
public class ListenerAddClick implements GoogleMap.OnMapClickListener{

    private InsertTrashTask insertTask = null;

    private FragmentActivity fragmentActivity;
    private Context activityContext;
    private GoogleMap themMap;

    public ListenerAddClick(FragmentActivity fragmentActivity,Context activityContext,GoogleMap themMap)
    {
        this.fragmentActivity = fragmentActivity;
        this.activityContext = activityContext;
        this.themMap = themMap;
    }

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

                                        // Recup string of EditTexts
                                        String stringETname = ETname.getText().toString();
                                        String stringETcomment = ETcomment.getText().toString();
                                        String toastMessage= "Mark added. Thanks you.";

                                        // init
                                        MarkerOptions themo = new MarkerOptions().position(point);
                                        FragmentMap.FM_TYPE thetype = FragmentMap.FM_TYPE.GRAY;

                                        if ((stringETname.compareTo("") == 0) && (stringETcomment.compareTo("") == 0)) {
                                            toastMessage= "Mark added but no description. Thanks you.";

                                        } else {
                                            if (stringETname.compareTo("") != 0) {
                                                themo.title(stringETname);
                                            }
                                            if (stringETcomment.compareTo("") != 0) {
                                                themo.snippet(stringETcomment);
                                            }
                                        }
                                        // Render a message/toast
                                        Toast.makeText(fragmentActivity, toastMessage,
                                                Toast.LENGTH_LONG).show();
                                         /*
                                        AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                        builderYes.setMessage(toastMessage).show();
                                        */

                                        // Take the good color here
                                        thetype = FragmentMap.checkFMType(textSpinner);

                                        //Ajout d'une poubelle en base avec commentaire
                                        insertTask = new InsertTrashTask(point.longitude,point.latitude,stringETname,stringETcomment,thetype.toString());
                                        insertTask.execute();

                                        // Recup marker with good garbage color
                                        MarkerOptions tempMarker = FragmentMap.addFragmentMapMarker(themo, thetype);

                                        // It can't return null mark but he we have to manage it
                                        if (tempMarker == null) {
                                            tempMarker = themo;
                                        }
                                        themMap.addMarker(tempMarker);
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
                        Toast.makeText(fragmentActivity, "Mark added. Thanks you.",
                                Toast.LENGTH_LONG).show();
                                    /*
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Mark added. Thanks you.").show();
                                    */

                        // Add marker by default
                        MarkerOptions themo = new MarkerOptions().position(point);

                        // Return the good garbage color - be default here
                        MarkerOptions thenewgenmarker = FragmentMap.addFragmentMapMarker(themo, null);

                        // it can't be null but in case
                        if (thenewgenmarker == null) {
                            thenewgenmarker = themo;
                        }
                        themMap.addMarker(thenewgenmarker);

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
                        AlertDialog.Builder builderYes = new AlertDialog.Builder(activityContext);
                        builderYes.setMessage("Do you want to add some comments ?").setPositiveButton("Yes", dialogClickListenerAddSomeComment)
                                .setNegativeButton("No", dialogClickListenerAddSomeComment).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Render a message/Toast
                        Toast.makeText(fragmentActivity, "Try again.",
                                Toast.LENGTH_LONG).show();
                        //AlertDialog.Builder builderNo = new AlertDialog.Builder(getContext());
                        //builderNo.setMessage("Try again ?").setPositiveButton("Yes", dialogClickListenerTryAgain)
                        //        .setNegativeButton("No", dialogClickListenerTryAgain).show();
                        break;
                }
            }
        };

        // Render a message
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

        // Exit in case of negative reponse for "Are you sure"
        // Exit and listening
    }
}
