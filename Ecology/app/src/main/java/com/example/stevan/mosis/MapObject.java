package com.example.stevan.mosis;

/**
 * Created by Stevan Zivkovic on 18-Sep-17.
 */

public class MapObject {

    private String description;
    private String image;
    private String type;
    private Double lon;
    private Double lat;

    public MapObject(String description, String image, String type, Double lon, Double lat) {
        this.description = description;
        this.image = image;
        this.type = type;
        this.lon = lon;
        this.lat = lat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}
