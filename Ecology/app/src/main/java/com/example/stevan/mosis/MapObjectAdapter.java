package com.example.stevan.mosis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Stevan Zivkovic on 18-Sep-17.
 */

public class MapObjectAdapter extends ArrayAdapter<MapObject> {

    Marker userLocationMarker;
    GoogleMap mMap;


    public MapObjectAdapter(@NonNull Context context, ArrayList<MapObject> objects, GoogleMap mMap) {
        super(context, 0, objects);
        this.mMap = mMap;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        MapObject mapObject = getItem(position);
        System.out.println("TEST " + mapObject.getType());

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.object_layout, parent, false);
        }

        ImageView problemImage = (ImageView) convertView.findViewById(R.id.problemImage);
        Bitmap image = decodeSampledBitmapFromString(mapObject.getImage(), 75, 75);
        problemImage.setImageBitmap(image);

        TextView problemDescription = (TextView) convertView.findViewById(R.id.problemDescription);
        problemDescription.setText(mapObject.getDescription());
        TextView problemType = (TextView) convertView.findViewById(R.id.problemType);
        problemType.setText(mapObject.getType());
        TextView problemLongitude = (TextView) convertView.findViewById(R.id.problemLongitude);
        problemLongitude.setText(String.valueOf(mapObject.getLon()));
        TextView problemLatitude = (TextView) convertView.findViewById(R.id.problemLatitude);
        problemLatitude.setText(String.valueOf(mapObject.getLon()));

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(mapObject.getLat(), mapObject.getLon()));
        //.icon(BitmapDescriptorFactory.fromBitmap(user.getProfilePicture()));

        if(mapObject.getType().equals("mali"))
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.garbage_small_40p));
        else if(mapObject.getType().equals("srednji"))
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.recycle_bin_40p));
        else
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.garbage_truck_40p));

        userLocationMarker = this.mMap.addMarker(markerOptions);

        return convertView;
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
}
