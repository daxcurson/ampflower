package ar.com.strellis.ampflower.ui.home.favorites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.guilhe.views.PlaceHolderAdapter;

import ar.com.strellis.ampflower.R;

public class AlbumsPlaceHolderAdapter extends RecyclerView.Adapter<AlbumsPlaceHolderAdapter.AlbumViewHolder>
        implements PlaceHolderAdapter
{
    @NonNull
    @Override
    public AlbumsPlaceHolderAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View itemView=inflater.inflate(R.layout.item_home_featured_album,parent,false);
        return new AlbumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }
    public static class AlbumViewHolder extends RecyclerView.ViewHolder
    {
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
