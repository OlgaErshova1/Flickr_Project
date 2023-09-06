package ca.calvert.flickrproject.model;

import java.io.Serializable;

public class Photo implements Serializable {
    private long _id;
    private String name, img_url;

    public Photo() {
    }

    public Photo(long _id, String name, String img_url) {
        this._id = _id;
        this.name = name;
        this.img_url = img_url;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    @Override
    public String toString() {
        return "Gallery{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", img_url='" + img_url + '\'' +
                '}';
    }
}
