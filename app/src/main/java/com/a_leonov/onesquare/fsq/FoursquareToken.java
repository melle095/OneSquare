package com.a_leonov.onesquare.fsq;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by a_leonov on 13.10.2017.
 */

public class FoursquareToken extends AsyncTask<URL,Void ,String> {

    private static final String TAG = "FoursquareToken";
    private FoursquareSession mSession;

    @Override
    protected String doInBackground(URL... url) {
        try {

            Log.i(TAG, "Opening URL " + url[0].toString());

            HttpURLConnection urlConnection = (HttpURLConnection) url[0].openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            //urlConnection.setDoOutput(true);

            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            JSONObject jsonObj  = (JSONObject) new JSONTokener(buffer.toString()).nextValue();
            String mAccessToken 		= jsonObj.getString("access_token");
            Log.i(TAG, "Got access token: " + mAccessToken);

            return  mAccessToken;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);
    }
}
