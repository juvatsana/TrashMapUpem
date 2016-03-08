package fr.upem.trashmapupem.Task;

/**
 * Created by Vatsana on 07/03/2016.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class InsertTrashTask extends AsyncTask<Void, Void, Boolean> {

    private final double longitude;
    private final double latitude;
    private final String commentaire;

    public InsertTrashTask(double longitude, double latitude,String commentaire) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.commentaire = commentaire;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.

        try {
            // Simulate network access.
            //Thread.sleep(2000);

            String link = "http://jvorabou.esy.es/insertTrash.php?username=julien&password=julien&longitude="+longitude+"&latitude="+latitude+"&commentaire="+commentaire+"";

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
            Log.i("insert resultat", sb.toString());
            if (sb.toString().equals("Trash have been inserted successfully") ){
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
       //TODO Ajouter sur la map ?
    }

    @Override
    protected void onCancelled() {

    }


}
