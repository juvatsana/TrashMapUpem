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
import fr.upem.trashmapupem.Task.DeleteTrashTask;
import fr.upem.trashmapupem.Task.InsertTrashTask;

import com.google.android.gms.maps.GoogleMap;

/**
 * Listener for marker click in delete fragment
 */
public class ListenerDeleteMarkerClick implements GoogleMap.OnMarkerClickListener
{
    private FragmentActivity fragmentActivity;
    private Context activityContext;
    private GoogleMap themMap;

    private DeleteTrashTask deleteTrashTask;

    public ListenerDeleteMarkerClick(FragmentActivity fragmentActivity,Context activityContext,GoogleMap themMap)
    {
        this.fragmentActivity = fragmentActivity;
        this.activityContext = activityContext;
        this.themMap = themMap;

        deleteTrashTask = null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final Marker markerToDelete = marker;

        // listener for "Do you want to delete this mark?"
        final DialogInterface.OnClickListener dialogClickListenerDeleteMark = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        //TODO : trouver une methode pour recuperer l'id de la poubelle selectionner
                        //deleteTrashTask = new DeleteTrashTask(O);
                        deleteTrashTask.execute();

                        // Delete marker
                        Marker newMarkerToDelete = FragmentMap.removeFragmentMapMarker(markerToDelete);
                        markerToDelete.remove();

                        // Render a message/toast
                        Toast.makeText(activityContext, "Marker Delete.",
                                Toast.LENGTH_LONG).show();
                                    /*
                                    AlertDialog.Builder builderYes = new AlertDialog.Builder(getContext());
                                    builderYes.setMessage("Marker delete").show();
                                    */
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Render a message
                        Toast.makeText(activityContext, "Delete canceled...",
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
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setMessage("Do you really want to delete this mark ?").setPositiveButton("Yes", dialogClickListenerDeleteMark)
                .setNegativeButton("No", dialogClickListenerDeleteMark).show();

        // Exit and listening
        return true;
    }
}
