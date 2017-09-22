package com.example.stevan.mosis;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Stevan Zivkovic on 11-Sep-17.
 */

class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View myContentsView;
    private String name, email, phoneNumber;
    private String description, type, lon, lat;
    private Bitmap image;
    boolean whichMarker;

    public MyInfoWindowAdapter(Context ctx, String name, String email, String phoneNumber) {
        myContentsView = LayoutInflater.from(ctx).inflate(R.layout.user_information, null);
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        whichMarker = true;
    }

    public MyInfoWindowAdapter(Context ctx, String description, Bitmap image, String type, String lon, String lat) {
        myContentsView = LayoutInflater.from(ctx).inflate(R.layout.object_layout, null);
        this.image = image;
        this.description = description;
        this.type = type;
        this.lon = lon;
        this.lat = lat;
        whichMarker = false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        if (whichMarker)
        {
            TextView userName = ((TextView) myContentsView
                    .findViewById(R.id.userName));
            userName.setText(name);

            TextView emailHeader = ((TextView) myContentsView
                    .findViewById(R.id.nameHeader));
            TextView phoneHeader = ((TextView) myContentsView
                    .findViewById(R.id.phoneHeader));

            LinearLayout emailLinear = (LinearLayout) myContentsView.findViewById(R.id.emailLinearLayout);
            LinearLayout phoneLinear = (LinearLayout) myContentsView.findViewById(R.id.phoneLinearLayout);

            if(email.equals("") || phoneNumber.equals(""))
            {
                emailLinear.setVisibility(View.GONE);
                phoneLinear.setVisibility(View.GONE);

            }
            else
            {
                emailLinear.setVisibility(View.VISIBLE);
                phoneLinear.setVisibility(View.VISIBLE);
                TextView userEmail = ((TextView) myContentsView
                        .findViewById(R.id.userEmail));
                userEmail.setText(email);

                TextView userPhone = ((TextView) myContentsView
                        .findViewById(R.id.userPhone));
                userPhone.setText(phoneNumber);
            }

            return myContentsView;
        }
        else
        {
            ImageView problemImage = (ImageView) myContentsView.findViewById(R.id.problemImage);
            problemImage.setImageBitmap(image);

            TextView problemDescription = (TextView) myContentsView.findViewById(R.id.problemDescription);
            problemDescription.setText(description);
            TextView problemType = (TextView) myContentsView.findViewById(R.id.problemType);
            problemType.setText(type);
            TextView problemLongitude = (TextView) myContentsView.findViewById(R.id.problemLongitude);
            problemLongitude.setText(String.valueOf(Double.parseDouble(lon)));
            TextView problemLatitude = (TextView) myContentsView.findViewById(R.id.problemLatitude);
            problemLatitude.setText(String.valueOf(Double.parseDouble(lat)));

            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(lon), Double.parseDouble(lat)));
            //.icon(BitmapDescriptorFactory.fromBitmap(user.getProfilePicture()));

            if(type.equals("mali"))
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.garbage_small_40p));
            else if(type.equals("srednji"))
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.recycle_bin_40p));
            else
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.garbage_truck_40p));


            return myContentsView;
        }


    }


}
