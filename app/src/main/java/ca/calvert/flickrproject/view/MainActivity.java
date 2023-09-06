package ca.calvert.flickrproject.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ca.calvert.flickrproject.R;
import ca.calvert.flickrproject.adapter.RecyclerViewAdapter;
import ca.calvert.flickrproject.db.DBManager;
import ca.calvert.flickrproject.model.Photo;
import ca.calvert.flickrproject.network.NetworkManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private ArrayList<Photo> photoArrayList = new ArrayList<>();
    private RecyclerViewAdapter recyclerViewAdapter;
    private ProgressBar progressBar;
    private NetworkManager networkManager;
    private FloatingActionButton fabScrollToTop;
    private DBManager dbManager;
    private ToggleButton leftToggleButton;
    private ImageButton searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbManager = (DBManager) getApplicationContext();

        setUpUI();
        setupDatabase();
        setupRecyclerView();
        fetchPhotos(leftToggleButton.isChecked());
    }

    private void setUpUI() {
        searchEditText = findViewById(R.id.searchEditText);
        progressBar = findViewById(R.id.progressBar);
        networkManager = new NetworkManager(this, "");
        fabScrollToTop = findViewById(R.id.fabScrollToTop);
        fabScrollToTop.setOnClickListener(this);
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(this);
        leftToggleButton = findViewById(R.id.leftToggleButton);
        leftToggleButton.setOnClickListener(this);
    }

    private void setupDatabase() {
        boolean isDatabaseExists = dbManager.isDatabaseExists();
        boolean areTablesExists = dbManager.doTablesExists();
        Log.d(TAG, String.valueOf(isDatabaseExists));
        if (!isDatabaseExists || !areTablesExists) {
            dbManager.createDatabaseAndTables();
        }
    }

    private void setupRecyclerView() {
        initializeRecyclerView();
        setRecyclerViewLayoutManager();
    }



    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(this); // Pass the dbManager instance to the adapter
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    private void setRecyclerViewLayoutManager() {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, RecyclerView.VERTICAL));
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void fetchPhotos(boolean fetchFromUserTable) {
        showProgressBar();

        if (fetchFromUserTable) {
            handleFetchFromUserTable();
        } else {
            handleFetchFromAPI();
        }
    }

    private void handleFetchFromUserTable() {
        boolean isUserTableEmpty = dbManager.isUserTableEmpty();

        if (isUserTableEmpty) {
            handleEmptyUserTable();
        } else {
            fetchPhotosFromUserTable();
        }
    }

    private void handleEmptyUserTable() {
        hideProgressBar();
        Toast.makeText(getApplicationContext(), "User table is empty", Toast.LENGTH_LONG).show();
    }

    private void fetchPhotosFromUserTable() {
        ArrayList<Photo> savedPhotos = dbManager.getSavedPhotos();
        photoArrayList = savedPhotos;
        recyclerViewAdapter.updatePhotoArrayList(photoArrayList);
        hideProgressBar();
    }

    private void handleFetchFromAPI() {
        String searchWord = searchEditText.getText().toString();
        if (searchWord.isEmpty()) {
            searchWord = "";
        }
        networkManager.fetchPhotos(searchWord, new Response.Listener<ArrayList<Photo>>() {
            @Override
            public void onResponse(ArrayList<Photo> response) {
                handleFetchPhotosResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleFetchPhotosError(error);
            }
        });
    }

    private void handleFetchPhotosResponse(ArrayList<Photo> response) {
        photoArrayList = response;
        recyclerViewAdapter.updatePhotoArrayList(photoArrayList);
        hideProgressBar();
        dbManager.insertPhotosLoadedTable(photoArrayList);
    }

    private void handleFetchPhotosError(VolleyError error) {
        hideProgressBar();
        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fabScrollToTop) {
            // Scroll to top logic
            recyclerView.smoothScrollToPosition(0);
            Log.d("Main", "Working");
        } else if (view.getId() == R.id.leftToggleButton) {
            // Determine whether to fetch photos from the user table or the API based on the state of the ToggleButton
            boolean fetchFromUserTable = leftToggleButton.isChecked();
            Log.d(TAG, String.valueOf(fetchFromUserTable));
            // Call fetchPhotos() with the appropriate boolean value
            fetchPhotos(fetchFromUserTable);
            refreshDisplay();
        } else if (view.getId() == R.id.searchBtn) {
            fetchPhotos(leftToggleButton.isChecked());
        }
    }

    private void refreshDisplay() {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.deleteAllRowsFromLoadedTable();
    }
}
