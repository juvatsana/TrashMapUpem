package fr.upem.trashmapupem;

import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Juan on 08/03/2016.
 */
public class PoubelleMarker {

    private FragmentMap.FM_TYPE type;
    private MarkerOptions marker;

    public PoubelleMarker (FragmentMap.FM_TYPE type,MarkerOptions marker)
    {
        this.type = type;
        this.marker = marker;
    }

    public FragmentMap.FM_TYPE getType() {
        return type;
    }

    public MarkerOptions getMarkerOptions()
    {
        return marker;
    }
}
