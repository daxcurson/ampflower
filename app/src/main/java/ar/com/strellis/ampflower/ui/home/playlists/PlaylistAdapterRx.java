package ar.com.strellis.ampflower.ui.home.playlists;

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
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.ui.home.artists.ArtistAdapterRx;
import ar.com.strellis.ampflower.ui.home.artists.ArtistViewHolder;

public class PlaylistAdapterRx extends PagingDataAdapter<Playlist, PlaylistViewHolder> {
    private Context context;
    public PlaylistAdapterRx()
    {
        super(PlaylistAdapterRx.callback_diff);
    }

    public PlaylistAdapterRx(Context context)
    {
        super(PlaylistAdapterRx.callback_diff);
        this.context=context;
    }
    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View itemView=inflater.inflate(R.layout.list_item_playlist,parent,false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist=getItem(position);
        holder.getPlaylistName().setText(Objects.requireNonNull(playlist).getName());
        holder.getSongsQuantity().setText(context.getString(R.string.songsQuantity,playlist.getItems()));
        // Now, attempt to download the image.
        Picasso.get().load(playlist.getArt()).into(holder.getPlaylistImage());
    }
    public static DiffUtil.ItemCallback<Playlist> callback_diff=new DiffUtil.ItemCallback<Playlist>() {
        @Override
        public boolean areItemsTheSame(@NonNull Playlist oldItem,@NonNull Playlist newItem) {
            Log.d("PlaylistAdapterRx","Callback comparing item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.getId()==newItem.getId() && oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Playlist oldItem,@NonNull Playlist newItem) {
            Log.d("PlaylistAdapterRx","Callback comparing content of item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.equals(newItem);
        }

    };
}
