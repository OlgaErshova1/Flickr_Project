package ca.calvert.flickrproject.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.calvert.flickrproject.model.Photo;
import ca.calvert.flickrproject.adapter.PhotoParser;
import ca.calvert.flickrproject.model.RawPhoto;

public class NetworkManager {
    private static final String BASE_URL = "https://www.flickr.com/services/rest/";

    private String searchWord;

    private RequestQueue requestQueue;

    public NetworkManager(Context context, String searchWord) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.searchWord = searchWord;
    }

    public void fetchPhotos(String searchWord, Response.Listener<ArrayList<Photo>> successListener, Response.ErrorListener errorListener) {
        this.searchWord = searchWord;
        String url = buildPhotoRequestUrl();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ArrayList<RawPhoto> rawPhotoArrayList = PhotoParser.photoJsonParser(jsonObject);
                    ArrayList<Photo> photoArrayList = convertRawPhotoToGallery(rawPhotoArrayList);
                    successListener.onResponse(photoArrayList);
                } catch (JSONException e) {
                    errorListener.onErrorResponse(new VolleyError(e));
                }
            }
        }, errorListener);

        requestQueue.add(request);
    }

    private String buildPhotoRequestUrl() {
        if(searchWord.isEmpty()) {
            return BASE_URL + "?method=flickr.photos.getRecent&api_key=9f7237832c8dc67b22d04b6e11f4f05a&format=json&nojsoncallback=1";
        } else {
            return BASE_URL + "?method=flickr.photos.search&api_key=9f7237832c8dc67b22d04b6e11f4f05a&format=json&nojsoncallback=1&tags=" + searchWord;
        }
    }

    private ArrayList<Photo> convertRawPhotoToGallery(ArrayList<RawPhoto> rawPhotoArrayList) {
        ArrayList<Photo> photoArrayList = new ArrayList<>();
        for (RawPhoto rawPhoto : rawPhotoArrayList) {
            long serverId = rawPhoto.getServer_id();
            long id = rawPhoto.getPhoto_id();
            String name = rawPhoto.getTitle();
            String secret = rawPhoto.getSecret();

            String imageUrl = "https://live.staticflickr.com/" + serverId + "/" + id + "_" + secret + "_" + "w.jpg";

            Photo photo = new Photo(id, name, imageUrl);
            photoArrayList.add(photo);
        }
        return photoArrayList;
    }
}
