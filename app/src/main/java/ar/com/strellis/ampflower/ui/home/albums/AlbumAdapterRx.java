package ar.com.strellis.ampflower.ui.home.albums;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.paging.PagedList;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import ar.com.strellis.ampflower.data.model.Album;

public class AlbumAdapterRx extends PagingDataAdapter<Album,AlbumViewHolder>
{
    public AlbumAdapterRx()
    {
        super(AlbumAdapterRx.callback_diff);
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album item=getItem(position);
        AlbumViewHolder albumViewHolder=(AlbumViewHolder)holder;
        Album album=getItem(position);
        albumViewHolder.getAlbumTitle().setText(Objects.requireNonNull(album).getName());
        albumViewHolder.getArtistName().setText(Objects.requireNonNull(album).getArtist().getName());
        // Now, attempt to download the image.
        Picasso.get().load(album.getArt()).into(albumViewHolder.getAlbumArt());
    }

    public static DiffUtil.ItemCallback<Album> callback_diff=new DiffUtil.ItemCallback<Album>() {
        @Override
        public boolean areItemsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
            Log.d("DEBUG","Callback comparing item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.getId().equals(newItem.getId()) && oldItem.getArtist().getId().equals(newItem.getArtist().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Album oldItem,@NonNull Album newItem) {
            Log.d("DEBUG","Callback comparing content of item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.equals(newItem);
        }

    };
}
