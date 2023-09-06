package ca.calvert.flickrproject.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import ca.calvert.flickrproject.R;
import ca.calvert.flickrproject.db.DBManager;
import ca.calvert.flickrproject.model.Photo;

public class DetailActivity extends AppCompatActivity {
    TextView image_id, img_name;
    ImageView photo_img;
    Photo photo;
    DBManager dbManager; // Declare the DBManager instance
    Button saveBtn, returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbManager = (DBManager) getApplicationContext();

        initializeDisplay();
        initializeIntent();
        populateCard();
        setupButtons();
    }

    private void initializeDisplay() {
        image_id = findViewById(R.id.image_id);
        img_name = findViewById(R.id.img_name);
        photo_img = findViewById(R.id.photo_img);
        saveBtn = findViewById(R.id.save_btn);
        returnBtn = findViewById(R.id.return_btn);
    }

    private void initializeIntent() {
        photo = (Photo) getIntent().getSerializableExtra("photo");
    }

    private void populateCard() {
        image_id.setText(String.valueOf(photo.get_id()));
        img_name.setText(photo.getName());
        Picasso.get().load(photo.getImg_url()).into(photo_img);
    }

    private void setupButtons() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImageToDatabase();
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void saveImageToDatabase() {
        dbManager.createDatabaseAndTables(); // Create the database and tables if they don't exist
        dbManager.insertPhoto(this, photo);
    }
}
