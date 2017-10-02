package com.example.stevan.mosis;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Stevan Zivkovic on 02-Oct-17.
 */

public class RankingsAdapter extends ArrayAdapter<RankingUser> {

    public RankingsAdapter(@NonNull Context context, ArrayList<RankingUser> users) {
        super(context, 0, users);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        RankingUser user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ranking_user, parent, false);
        }

        ImageView userProfilePicture = (ImageView) convertView.findViewById(R.id.rankingProfile);
        userProfilePicture.setImageBitmap(user.getProfileImage());

        TextView username = (TextView) convertView.findViewById(R.id.rankingUsername);
        username.setText(user.getUsername());

        TextView usersPoints = (TextView) convertView.findViewById(R.id.rankingPoints);
        usersPoints.setText(String.valueOf(user.getPoints()));

        return convertView;

    }
}
