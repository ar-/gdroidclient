package org.gdroid.gdroid;

public class AppDescriptor {
    private String name;
    private float stars;
    private int thumbnail;

    public AppDescriptor() {
    }

    public AppDescriptor(String name, float stars, int thumbnail) {
        this.name = name;
        this.stars = stars;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}