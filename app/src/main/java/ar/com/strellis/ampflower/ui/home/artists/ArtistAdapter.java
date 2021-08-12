package ar.com.strellis.ampflower.ui.home.artists;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.NetworkState;

public class ArtistAdapter extends PagedListAdapter<Artist, RecyclerView.ViewHolder>
{
    private NetworkState networkState;
    private Context myContext;
    public ArtistAdapter()
    {
        super(ArtistAdapter.callback_diff);
    }
    public ArtistAdapter(Context context)
    {
        super(ArtistAdapter.callback_diff);
        this.myContext=context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType== R.layout.list_item_artist) {
            View artistItemView = inflater.inflate(R.layout.list_item_artist, parent, false);
            return new ArtistViewHolder(artistItemView);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==R.layout.list_item_artist)
        {
            ArtistViewHolder artistViewHolder=(ArtistViewHolder)holder;
            Artist artist=getItem(position);
            artistViewHolder.getArtistName().setText(Objects.requireNonNull(artist).getName());
            artistViewHolder.getSongsQuantity().setText(myContext.getString(R.string.songsQuantity,artist.getSongs()));
            // Now, attempt to download the image.
            Picasso.get().load(artist.getArt()).into(artistViewHolder.getArtistFace());
        }
        if(getItemViewType(position)==R.layout.network_state_item)
        {
            ((NetworkStateItemViewHolder) holder).bindView(networkState);
        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }
    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }
    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.network_state_item;
        } else {
            return R.layout.list_item_artist;
        }
    }

    public static DiffUtil.ItemCallback<Artist> callback_diff=new DiffUtil.ItemCallback<Artist>() {
        @Override
        public boolean areItemsTheSame(@NonNull Artist oldItem,@NonNull Artist newItem) {
            Log.d("DEBUG","Callback comparing item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.getId()==newItem.getId() && oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Artist oldItem,@NonNull Artist newItem) {
            Log.d("DEBUG","Callback comparing content of item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.equals(newItem);
        }

    };
}
