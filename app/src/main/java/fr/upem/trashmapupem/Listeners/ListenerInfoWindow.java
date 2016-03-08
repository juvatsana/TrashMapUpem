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
import fr.upem.trashmapupem.PoubelleMarker;
import fr.upem.trashmapupem.R;

/**
 * Listener info on window marker click
 */
public class ListenerInfoWindow implements GoogleMap.InfoWindowAdapter{

    private FragmentActivity fragmentActivity;
    private Context activityContext;
    private GoogleMap themMap;

    public ListenerInfoWindow(FragmentActivity fragmentActivity,Context activityContext,GoogleMap themMap)
    {
        this.fragmentActivity = fragmentActivity;
        this.activityContext = activityContext;
        this.themMap = themMap;
    }

    // Use default InfoWindow frame
    @Override
    public View getInfoWindow(Marker arg0) {
        View v = fragmentActivity.getLayoutInflater().inflate(R.layout.activity_infolayout, null);
        TextView tvinfoTitle = (TextView) v.findViewById(R.id.infoTitle);
        TextView tvinfoSnippet = (TextView) v.findViewById(R.id.infoSnippet);
        ImageView ivinfoimage1 = (ImageView) v.findViewById(R.id.imgInfoImage1);
        ImageView ivinfoimage2 = (ImageView) v.findViewById(R.id.imgInfoImage2);

        final LatLng ll = arg0.getPosition();
        String newMarkKey =String.valueOf(ll.latitude)+":"+String.valueOf(ll.longitude);

        PoubelleMarker PM = FragmentMap.getPoubelleMarkerFromMap(newMarkKey);

        if(PM!=null)
        {
            FragmentMap.FM_TYPE type = PM.getType();
            if(type!=null)
            {
                switch(type)
                {
                    case GREEN:
                        ivinfoimage1.setImageResource(R.drawable.garbmidgreen);
                        ivinfoimage2.setImageResource(R.drawable.pimspbgreen);
                        break;
                    case BROWN:
                        ivinfoimage1.setImageResource(R.drawable.garbmidbrown);
                        ivinfoimage2.setImageResource(R.drawable.pimspbbrown);
                        break;
                    case YELLOW:
                        ivinfoimage1.setImageResource(R.drawable.garbmidyellow);
                        ivinfoimage2.setImageResource(R.drawable.pimspbyellow);
                        break;
                    default:
                        ivinfoimage1.setImageResource(R.drawable.garbmidgray);
                        ivinfoimage2.setImageResource(R.drawable.pimspbgray);
                        break;
                }
            }
            else
            {
                ivinfoimage1.setImageResource(R.drawable.garbmidgray);
                ivinfoimage2.setImageResource(R.drawable.pimspbgray);
            }
        }
        else
        {
            ivinfoimage1.setImageResource(R.drawable.garbmidgray);
            ivinfoimage2.setImageResource(R.drawable.pimspbgray);
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
}
