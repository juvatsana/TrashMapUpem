package fr.upem.trashmapupem;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment qui permet d'afficher une liste des poubelles aux alentours.
 */
public class FragmentListDistance extends Fragment {

    /**
     * Modifie la localisation courrante
     * @param currentLocation la nouvelle localisation
     */
    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    /**
     * Custom comparator qui compare une instance de l'objet Data a partir de son champ distance.
     */
    class CustomComparatorData implements Comparator<Data> {

        /**
         * Override de la méthode compare
         * @param o1 Première instance Data à comparer
         * @param o2 Deuxième instance Data à comparer
         * @return return de la méthode Double.comparator
         */
        @Override
        public int compare(Data o1, Data o2) {

            return Double.compare(o1.distance,o2.distance);
        }
    }

    /**
     * Class Data pour stocker les informations relatives a une poubelle.
     */
    class Data {
        FragmentMap.FM_TYPE type;
        double distance;
        int photoPb;

        /**
         * Nouvelle instance de data
         * @param type Type de poubelle
         * @param distance distance par rapport à la position courrante
         * @param photoPb id de la photo de la poubelle
         */
        Data(FragmentMap.FM_TYPE type, double distance, int photoPb) {
            this.type = type;
            this.distance = distance;
            this.photoPb = photoPb;
        }
    }

    private List<Data> datas = new ArrayList<>();
    private RecyclerView rv;
    private Location currentLocation;

    /**
     * Override la methode onCreateView.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

//        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_listdistance, container, false);
//        ListView listview = (ListView) root.findViewById(R.id.listDistanceView);
//        final List<LatLng> thelist = FragmentMap.getPosOfMapMark();
//        List<String> listCoord = calculListPos(thelist);
//        final StableArrayAdapter adapter = new StableArrayAdapter(getContext(),android.R.layout.simple_list_item_1, listCoord);
//        listview.setAdapter(adapter);

        Log.i("OnCreateView","before everything");
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_recyclerview_distance, container, false);
        rv= (RecyclerView)root.findViewById(R.id.recycle_pb);
        rv.setHasFixedSize(true);
        Log.i("OnCreateView", "before after set has fixed size");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());

        if(currentLocation==null)
        {
            currentLocation = new Location("");
            currentLocation.setLatitude(48.838790d);
            currentLocation.setLongitude(2.585753d);
        }
/*
        final List<PoubelleMarker> thelist = FragmentMap.getPosOfMapMark();
        calculListPos(thelist);
        Log.i("OnCreateView","before adapter");
        RVAdapter adapter = new RVAdapter(datas);
        rv.setAdapter(adapter);*/
        return root;
    }

    /**
     * Override de la methode onStart.
     * Initialisation.
     */
    @Override
    public void onStart()
    {
        Log.i("FragmentListDistance","onStart");
        super.onStart();
        final List<PoubelleMarker> thelist = FragmentMap.getPosOfMapMark();
        calculListPos(thelist);
        RVAdapter adapter = new RVAdapter(datas,getActivity(),thelist);
        rv.setAdapter(adapter);
    }

    /**
     * Override de la methode onStop.
     */
    @Override
    public void onStop()
    {
        Log.i("FragmentListDistance","onStop");
        super.onStop();
    }

    /**
     * Calcule des positions des poubelles aux alentours.
     * @param listpm Liste de PoubelleMarker
     */
    public void calculListPos(List<PoubelleMarker> listpm)
    {
        datas.clear();
        for(PoubelleMarker pm:listpm)
        {
            // Recup Marker and position of HashMap
            MarkerOptions MO = pm.getMarkerOptions();
            LatLng ll = MO.getPosition();

            // Calculation of the distance as this instant
            // Push it on the datas list
            Double distance = CalculationByDistance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), ll);
            FragmentMap.FM_TYPE type = pm.getType();
            int drawable = 0;
            switch(type)
            {
                case GREEN:
                    drawable = R.drawable.garbagebiggreen;
                    break;
                case BROWN:
                    drawable = R.drawable.garbagebigbrown;
                    break;
                case YELLOW:
                    drawable = R.drawable.garbagebigyellow;
                    break;
                default:
                    drawable = R.drawable.garbagebiggray;
                    break;
            }
            Data d = new Data(type,distance,drawable);
            datas.add(d);
        }
        Collections.sort(datas, new CustomComparatorData());
    }

    /**
     * Creer une nouvelle instance de FragmentListDistance.
     * @param context  Context de l'application.
     * @param location LastLocation trouver sur le fragmentMap.
     * @return Le fragment cree.
     */
    public static Fragment newInstance(Context context,Location location) {
        FragmentListDistance f = new FragmentListDistance();
        f.setCurrentLocation(location);
        return f;
    }

    /**
     * Calcule de la distance entre deux instances de LatLng.
     * @param StartP LatLng de debut.
     * @param EndP LatLng de fin.
     * @return Distance au format double.
     */
    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        return Radius * c;
    }

    /**
     * Custom ArrayAdapter.
     */
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        /**
         * Nouvelle instance de StableArrayAdapter.
         * @param context Context de l'application
         * @param textViewResourceId Id de la ressource
         * @param objects List de string
         */
        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        /**
         * Override de la methode getItemId.
         * Notifie la position de chacun des ArrayAdapter.
         * @param position La position.
         * @return
         */
        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        /**
         * Override de la methode hasStableIds.
         * @return
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
