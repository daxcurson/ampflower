package ar.com.strellis.ampflower.ui.home.albums;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.NetworkState;

public class AlbumAdapterRx extends PagingDataAdapter<Album,RecyclerView.ViewHolder>
{
    private NetworkState networkState;
    public AlbumAdapterRx() {
        super(callback_diff);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType== R.layout.list_item_album) {
            View albumItemView = inflater.inflate(R.layout.list_item_album, parent, false);
            return new AlbumViewHolder(albumItemView);
        }
        else if(viewType==R.layout.network_state_item)
        {
            View view=inflater.inflate(R.layout.network_state_item,parent,false);
            return new NetworkStateItemViewHolder(view);
        }
        else
            throw new IllegalArgumentException("Unknown view type");
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==R.layout.list_item_album)
        {
            AlbumViewHolder albumViewHolder=(AlbumViewHolder)holder;
            Album album=getItem(position);
        }
        if(getItemViewType(position)==R.layout.network_state_item)
        {
            ((NetworkStateItemViewHolder) holder).bindView(networkState);
        }
    }
    public static DiffUtil.ItemCallback<Album> callback_diff=new DiffUtil.ItemCallback<Album>() {
        @Override
        public boolean areItemsTheSame(@NonNull Album oldItem,@NonNull Album newItem) {
            Log.d("DEBUG","Callback comparing item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.getId()==newItem.getId() && oldItem.getArtist().getId()==newItem.getArtist().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Album oldItem,@NonNull Album newItem) {
            Log.d("DEBUG","Callback comparing content of item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.equals(newItem);
        }

    };
}
