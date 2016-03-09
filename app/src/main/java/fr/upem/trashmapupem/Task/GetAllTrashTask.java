package fr.upem.trashmapupem.Task;

/**
 * Created by Vatsana on 07/03/2016.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.upem.trashmapupem.FragmentMap;
import fr.upem.trashmapupem.R;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class GetAllTrashTask extends AsyncTask<Void, Void, Boolean> {

    private HashMap<String,MarkerOptions> mapTrash;

    public GetAllTrashTask()
    {
        mapTrash = new HashMap<>();
    }

    //TODO : ajouter un titre et couleur dans les webservies
    public void jsonToMap(String t) throws JSONException {

        JSONArray array = new JSONArray(t);

        for(int i=0; i<array.length(); i++){
            JSONObject jsonObj  = array.getJSONObject(i);
            String id = jsonObj.getString("id_poubelle");
            String longitude = jsonObj.getString("longitude");
            String latitude =  jsonObj.getString("latitude");
            String commentaire = jsonObj.getString("commentaire");

            MarkerOptions marker = new MarkerOptions()
                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                    .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                    .title(commentaire)
                    .snippet(commentaire)
                    .draggable(true);

            mapTrash.put(id,marker);
        }

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            String link = "http://jvorabou.esy.es/getAllTrash.php";

            URL url = new URL(link);
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(link));
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";

            while ((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            in.close();

            if (!sb.toString().isEmpty()){
                jsonToMap(sb.toString());
                return true;
            }
        }catch(Exception e){
            Log.e("Exception","Exception: " + e.getMessage());
            return false;
        }

        return false;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if(success)
        {
            Log.i("sucess","sucess");
            for(Map.Entry<String, MarkerOptions> entry : mapTrash.entrySet()) {
                String key = entry.getKey();
                MarkerOptions value = entry.getValue();

                FragmentMap.addFragmentMapMarker(value, FragmentMap.FM_TYPE.GREEN);
            }
        }
    }

    @Override
    protected void onCancelled() {

    }


}
