package fr.upem.trashmapupem.Task;

/**
 * Created by Vatsana on 07/03/2016.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import fr.upem.trashmapupem.R;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class DeleteTrashTask extends AsyncTask<Void, Void, Boolean> {

    private int idTrash;

    public DeleteTrashTask(int idTrash)
    {
        this.idTrash = idTrash;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            String link = "http://jvorabou.esy.es/deleteTrash.php?id="+idTrash+"";

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

            if (sb.toString().equals("Trash have been deleted successfully")){
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
    }

    @Override
    protected void onCancelled() {
    }


}
