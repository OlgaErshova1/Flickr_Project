package ca.calvert.flickrproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import ca.calvert.flickrproject.R;
import ca.calvert.flickrproject.model.Photo;
import ca.calvert.flickrproject.view.DetailActivity;
import ca.calvert.flickrproject.view.ImageFragment;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Photo> photoArrayList;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
        this.photoArrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photoArrayList.get(position);
        Picasso.get().load(photo.getImg_url()).into(holder.photoImg);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Open the DetailActivity and pass the photo
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("photo", photo);
                    context.startActivity(intent);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showImageFragment(photo.getImg_url());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoArrayList.size();
    }

    public void updatePhotoArrayList(ArrayList<Photo> photoArrayList) {
        this.photoArrayList = photoArrayList;
        notifyDataSetChanged();
    }

    private void showImageFragment(String imageUrl) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        ImageFragment fragment = ImageFragment.newInstance(imageUrl);
        fragment.show(fragmentManager, "image_fragment");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView photoImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.rootCardView);
            photoImg = itemView.findViewById(R.id.photo_img);
        }
    }
}
