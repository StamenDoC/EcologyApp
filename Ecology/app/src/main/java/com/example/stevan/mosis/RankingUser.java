package com.example.stevan.mosis;

import android.graphics.Bitmap;
import android.support.annotation.IntegerRes;

/**
 * Created by Stevan Zivkovic on 02-Oct-17.
 */

public class RankingUser {

    Bitmap profileImage;
    String username;
    Integer points;

    public RankingUser(Bitmap profileImage, String username, Integer points) {
        this.profileImage = profileImage;
        this.username = username;
        this.points = points;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
