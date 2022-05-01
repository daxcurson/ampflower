package ar.com.strellis.ampflower.ui.home.artists;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Artist;

public class ArtistAdapterRx extends PagingDataAdapter<Artist, ArtistViewHolder> {
    private Context context;
    public ArtistAdapterRx()
    {
        super(ArtistAdapterRx.callback_diff);
    }

    public ArtistAdapterRx(Context context)
    {
        super(ArtistAdapterRx.callback_diff);
        this.context=context;
    }
    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View itemView=inflater.inflate(R.layout.list_item_artist,parent,false);
        return new ArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist=getItem(position);
        holder.getArtistName().setText(Objects.requireNonNull(artist).getName());
        holder.getSongsQuantity().setText(context.getString(R.string.songsQuantity,artist.getSongcount()));
        // Now, attempt to download the image.
        Picasso.get().load(artist.getArt()).into(holder.getArtistFace());
    }
    public static DiffUtil.ItemCallback<Artist> callback_diff=new DiffUtil.ItemCallback<Artist>() {
        @Override
        public boolean areItemsTheSame(@NonNull Artist oldItem,@NonNull Artist newItem) {
            Log.d("ArtistAdapter","Callback comparing item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.getId().equals(newItem.getId()) && oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Artist oldItem,@NonNull Artist newItem) {
            Log.d("ArtistAdapter","Callback comparing content of item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.equals(newItem);
        }

    };
}
