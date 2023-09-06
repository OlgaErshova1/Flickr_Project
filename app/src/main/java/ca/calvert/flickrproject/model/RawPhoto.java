package ca.calvert.flickrproject.model;

import java.io.Serializable;

public class RawPhoto implements Serializable {
    private long photo_id, server_id;
    private String secret, title;


    public RawPhoto() {
    }

    public RawPhoto(long photo_id, long server_id, String secret, String title) {
        this.photo_id = photo_id;
        this.server_id = server_id;
        this.secret = secret;
        this.title = title;
    }

    public long getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(long photo_id) {
        this.photo_id = photo_id;
    }

    public long getServer_id() {
        return server_id;
    }

    public void setServer_id(long server_id) {
        this.server_id = server_id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "photo_id=" + photo_id +
                ", server_id=" + server_id +
                ", secret='" + secret + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}