package fr.upem.trashmapupem;

import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Class qui permet de représenter le marquer d'une poubelle
 */
public class PoubelleMarker {

    private FragmentMap.FM_TYPE type;
    private MarkerOptions marker;

    /**
     * Creer une nouvelle instance de marker de poubelle
     * @param type Correspond au type de poubelle
     * @param marker Contient toutes les informations du marker (longitude,latitude...)
     */
    public PoubelleMarker (FragmentMap.FM_TYPE type,MarkerOptions marker)
    {
        this.type = type;
        this.marker = marker;
    }

    /**
     * Enum qui correspond au type de poubelle
     * @return le type de poubelle
     */
    public FragmentMap.FM_TYPE getType() {
        return type;
    }

    /**
     * Informations du marker
     * @return les informations relatives au marker
     */
    public MarkerOptions getMarkerOptions()
    {
        return marker;
    }
}
