package com.example.stevan.mosis;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Rankings extends AppCompatActivity {

    ListView rankingUsers;
    ArrayList<RankingUser> users;
    RankingsAdapter rankingsAdapter;

    final static int REQ_WIDTH = 640;
    final static int REQ_HEIGHT = 480;

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ranking_layout);

        rankingUsers = (ListView) findViewById(R.id.rankingList);
        users = new ArrayList<RankingUser>();
        rankingsAdapter = new RankingsAdapter(this, users);
        rankingUsers.setAdapter(rankingsAdapter);

        new GetRankingList().execute();

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromString(String input, int reqWidth, int reqHeight) {

        byte[] decodedBytes = Base64.decode(input, 0);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);
    }

    public class GetRankingList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(Rankings.this);
            dialog.setMessage("Loading...");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            String urlAddress = "http://qosstamen.esy.es/Server/return_top_ten_players.php";

            try {

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest request = new StringRequest(StringRequest.Method.POST, urlAddress, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Rankings response", response);

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            users.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                final String userName = jsonObject.getString("Username");
                                final String profilePicture = jsonObject.getString("Avatar");
                                final Integer points = jsonObject.getInt("Points");

                                final Bitmap bitmap1 = decodeSampledBitmapFromString(profilePicture, REQ_WIDTH, REQ_HEIGHT);

                                final RankingUser user = new RankingUser(bitmap1, userName, points);

                                users.add(user);

                            }
                            rankingsAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                        catch (Exception e)
                        {

                        }
                    }
                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        return hashMap;
                    }
                };

                requestQueue.add(request);

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String aVoid) {

        }
    }

}
