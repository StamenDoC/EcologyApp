package com.example.stevan.mosis;

/**
 * Created by Stevan Zivkovic on 31-Aug-17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Stevan Zivkovic on 29-Jul-17.
 */

public class UserAdapter extends ArrayAdapter<User> {

    Marker userLocationMarker;
    GoogleMap mMap;


    public UserAdapter(@NonNull Context context, ArrayList<User> users, GoogleMap mMap) {
        super(context, 0, users);
        this.mMap = mMap;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        User user = getItem(position);
        System.out.println("TEST " + user.getFirstName());

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.thumbnail, parent, false);
        }

        CircleImageView userProfilePicture = (CircleImageView) convertView.findViewById(R.id.userProfilePicture);
        userProfilePicture.setImageBitmap(user.getProfilePicture());

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(user.getLatitude(), user.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(user.getProfilePicture()));


        userLocationMarker = this.mMap.addMarker(markerOptions);

        userProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        return convertView;
    }
}

