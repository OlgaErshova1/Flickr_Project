package ca.calvert.flickrproject.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.calvert.flickrproject.model.RawPhoto;

public class PhotoParser {
    public static ArrayList<RawPhoto> photoJsonParser(JSONObject jsonObject){
        ArrayList<RawPhoto> photoList = new ArrayList<>();
        try {
            JSONObject dataObject = jsonObject.getJSONObject("photos");
            JSONArray jsonArray = dataObject.getJSONArray("photo");
            for (int i = 1; i < jsonArray.length() ; i++){
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                RawPhoto photo = new RawPhoto();
                photo.setPhoto_id(jsonobject.getLong("id"));
                photo.setServer_id(jsonobject.getLong("server"));
                photo.setSecret(jsonobject.getString("secret"));
                photo.setTitle(jsonobject.getString("title"));
                photoList.add(photo);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return photoList;
    }
}
