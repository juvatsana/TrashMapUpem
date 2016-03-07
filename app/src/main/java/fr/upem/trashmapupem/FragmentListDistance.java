package fr.upem.trashmapupem;

import android.content.Context;
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
import android.widget.ListView;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Juan on 06/03/2016.
 */
public class FragmentListDistance extends Fragment {


    class Data {
        String type;
        String distance;
        int photoPb;

        Data(String type, String distance, int photoPb) {
            this.type = type;
            this.distance = distance;
            this.photoPb = photoPb;
        }
    }

    private List<Data> datas = new ArrayList<>();
    private RecyclerView rv;

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

        final List<FragmentMap.PoubelleMarker> thelist = FragmentMap.getPosOfMapMark();
        calculListPos(thelist);
        Log.i("OnCreateView","before adapter");
        RVAdapter adapter = new RVAdapter(datas);
        rv.setAdapter(adapter);
        return root;
    }

    public void calculListPos(List<FragmentMap.PoubelleMarker> listpm)
    {
        for(FragmentMap.PoubelleMarker pm:listpm)
        {
            // Recup Marker and position of HashMap
            MarkerOptions MO = pm.getMarkerOptions();
            LatLng ll = MO.getPosition();

            // Calculation of the distance as this instant
            // Push it on the datas list
            Double distance = CalculationByDistance(new LatLng(48.838790, 2.585753), ll);
            String type = pm.getType();
            int drawable = 0;
            switch(type)
            {
                case "Green":
                    drawable = R.drawable.iconpbgreen;
                    break;
                case "Brown":
                    drawable = R.drawable.iconpbbrown;
                    break;
                case "Yellow":
                    drawable = R.drawable.iconpbyellow;
                    break;
                default:
                    type="Gray";
                    drawable = R.drawable.iconpbgray;
                    break;
            }
            Data d = new Data(type,String.valueOf(distance)+" km",drawable);
            datas.add(d);
        }

    }

    public static Fragment newInstance(Context context) {
        FragmentListDistance f = new FragmentListDistance();
        return f;
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
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
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
