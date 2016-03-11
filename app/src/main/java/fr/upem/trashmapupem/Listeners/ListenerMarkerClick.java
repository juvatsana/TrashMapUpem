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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
public class ListenerMarkerClick implements GoogleMap.OnMarkerClickListener
{
    private FragmentActivity fragmentActivity;
    private Context activityContext;
    private GoogleMap themMap;
    private Circle thecircle;

    public ListenerMarkerClick(FragmentActivity fragmentActivity,Context activityContext,GoogleMap themMap)
    {
        this.fragmentActivity = fragmentActivity;
        this.activityContext = activityContext;
        this.themMap = themMap;
    }

    private void addCircleAtPosition(double lat, double lang) {

        CircleOptions mOptions = new CircleOptions()
                .center(new LatLng(lat, lang)).radius(100)
                .strokeColor(0x110000FF).strokeWidth(1).fillColor(0x110000FF);
        if(thecircle!=null)
        {
            thecircle.remove();
        }
        thecircle = themMap.addCircle(mOptions);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        final Marker markerToDelete = marker;

        addCircleAtPosition(marker.getPosition().latitude,marker.getPosition().longitude);

        // Exit and listening
        return true;
    }
}
